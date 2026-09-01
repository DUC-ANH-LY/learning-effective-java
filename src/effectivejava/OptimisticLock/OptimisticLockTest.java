package effectivejava.OptimisticLock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(classes = OptimisticLockApplication.class)
public class OptimisticLockTest {

    @Autowired
    private ProductInventoryService inventoryService;

    @Test
    @DisplayName("Test Optimistic Locking with Retry succeeds for concurrent updates")
    void testOptimisticLockingWithRetry() throws InterruptedException {
        ProductInventory product = inventoryService.createProduct("Headphones", 100);
        Long productId = product.getId();

        int numberOfThreads = 4;
        int quantityPerOrder = 5;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startGate.await();
                    boolean success = inventoryService.decreaseStockWithRetry(productId, quantityPerOrder, 5);
                    if (success) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception ignored) {
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        endGate.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        ProductInventory finalProduct = inventoryService.getProduct(productId);

        Assertions.assertEquals(4, successCount.get(), "All 4 orders should succeed with retries");
        // 100 - (4 * 5) = 80
        Assertions.assertEquals(80, finalProduct.getStock(), "Stock should be exactly 80");
        Assertions.assertEquals(4L, finalProduct.getVersion(), "Version should have incremented 4 times to 4");
    }
}
