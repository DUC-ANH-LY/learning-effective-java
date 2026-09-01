package effectivejava.OptimisticLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class OptimisticLockRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(OptimisticLockRunner.class);

    private final ProductInventoryService inventoryService;

    public OptimisticLockRunner(ProductInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void run(String... args) throws Exception {
        runConflictDetectionDemo();
        Thread.sleep(1000);
        runAutomaticRetryDemo();
    }

    /**
     * DEMO 1: Concurrent Updates -> Conflict Detection
     */
    private void runConflictDetectionDemo() throws InterruptedException {
        log.info("\n");
        log.info("################################################################################");
        log.info("  DEMO 1: OPTIMISTIC LOCKING - CONFLICT DETECTION (@Version)                   ");
        log.info("################################################################################");

        ProductInventory product = inventoryService.createProduct("Laptop", 10);
        Long productId = product.getId();
        log.info("Created Product [{}] - Stock: 10, Version: {}", product.getProductName(), product.getVersion());

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);

        // Thread 1 buys 2 items
        executor.submit(() -> {
            try {
                Thread.currentThread().setName("Buyer-Thread-1");
                startGate.await();
                inventoryService.decreaseStock(productId, 2);
            } catch (Exception e) {
                log.error("[Buyer-Thread-1] Exception: {}", e.getClass().getSimpleName() + ": " + e.getMessage());
            } finally {
                endGate.countDown();
            }
        });

        // Thread 2 buys 3 items concurrently
        executor.submit(() -> {
            try {
                Thread.currentThread().setName("Buyer-Thread-2");
                startGate.await();
                inventoryService.decreaseStock(productId, 3);
            } catch (Exception e) {
                log.error("[Buyer-Thread-2] Exception: {}", e.getClass().getSimpleName() + ": " + e.getMessage());
            } finally {
                endGate.countDown();
            }
        });

        log.info(">>> Firing 2 concurrent updates simultaneously...");
        startGate.countDown();

        endGate.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        ProductInventory finalProduct = inventoryService.getProduct(productId);
        log.info("--------------------------------------------------------------------------------");
        log.info("DEMO 1 RESULT:");
        log.info("Initial Stock:       10 (Version 0)");
        log.info("Final Stock in DB:   {} (Version {})", finalProduct.getStock(), finalProduct.getVersion());
        log.info("Observation: One transaction succeeded (Version incremented to 1).");
        log.info("             The concurrent transaction failed with ObjectOptimisticLockingFailureException!");
        log.info("--------------------------------------------------------------------------------\n");
    }

    /**
     * DEMO 2: Optimistic Locking with Automatic Retry
     */
    private void runAutomaticRetryDemo() throws InterruptedException {
        log.info("\n");
        log.info("################################################################################");
        log.info("  DEMO 2: OPTIMISTIC LOCKING - WITH AUTOMATIC RETRY                            ");
        log.info("################################################################################");

        ProductInventory product = inventoryService.createProduct("Smartphone", 10);
        Long productId = product.getId();
        log.info("Created Product [{}] - Stock: 10, Version: {}", product.getProductName(), product.getVersion());

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            final int buyerNum = i;
            final int quantity = 2; // each buys 2 items
            executor.submit(() -> {
                try {
                    String threadName = "Retry-Buyer-" + buyerNum;
                    Thread.currentThread().setName(threadName);
                    startGate.await();
                    inventoryService.decreaseStockWithRetry(productId, quantity, 3);
                } catch (Exception e) {
                    log.error("Error: {}", e.getMessage());
                } finally {
                    endGate.countDown();
                }
            });
        }

        log.info(">>> Firing 2 concurrent updates WITH RETRY mechanism...");
        startGate.countDown();

        endGate.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        ProductInventory finalProduct = inventoryService.getProduct(productId);
        log.info("--------------------------------------------------------------------------------");
        log.info("DEMO 2 RESULT (WITH RETRY):");
        log.info("Initial Stock:       10 (Version 0)");
        log.info("Final Stock in DB:   {} (Version {})", finalProduct.getStock(), finalProduct.getVersion());
        log.info("Observation: Both transactions succeeded! Conflicted thread re-fetched & retried safely.");
        log.info("--------------------------------------------------------------------------------\n");
    }
}
