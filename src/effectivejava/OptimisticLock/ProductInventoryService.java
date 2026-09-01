package effectivejava.OptimisticLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductInventoryService {

    private static final Logger log = LoggerFactory.getLogger(ProductInventoryService.class);

    private final ProductInventoryRepository repository;

    public ProductInventoryService(ProductInventoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProductInventory createProduct(String name, int stock) {
        ProductInventory product = new ProductInventory(name, stock);
        return repository.save(product);
    }

    /**
     * Attempts to decrease product stock using Optimistic Locking.
     * No DB row lock is held during reading.
     * When transaction commits, JPA checks 'WHERE id = :id AND version = :version'.
     * If another transaction updated the record first, an ObjectOptimisticLockingFailureException is thrown.
     */
    @Transactional
    public boolean decreaseStock(Long productId, int quantity) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] Fetching product ID: {}", threadName, productId);

        ProductInventory product = repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        log.info("[{}] -> Current Stock: {}, Version: {}", threadName, product.getStock(), product.getVersion());

        if (product.getStock() < quantity) {
            log.warn("[{}] -> INSUFFICIENT STOCK: requested {}, available {}.", threadName, quantity, product.getStock());
            return false;
        }

        // Simulate some processing delay
        try {
            log.info("[{}] -> Processing order for {} items (simulating 500ms delay)...", threadName, quantity);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        product.setStock(product.getStock() - quantity);
        repository.save(product);

        log.info("[{}] -> Saved product. Decreased stock to {}. Ready to commit.", threadName, product.getStock());
        return true;
    }

    /**
     * Retries the stock decrease if an OptimisticLockingFailure occurs.
     */
    public boolean decreaseStockWithRetry(Long productId, int quantity, int maxRetries) {
        String threadName = Thread.currentThread().getName();
        int attempts = 0;

        while (attempts < maxRetries) {
            attempts++;
            try {
                log.info("[{}] Attempt {}/{} to decrease stock...", threadName, attempts, maxRetries);
                return decreaseStock(productId, quantity);
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("[{}] -> CONFLICT DETECTED! OptimisticLockException on attempt {}. Retrying...",
                        threadName, attempts);
                try {
                    Thread.sleep(100); // brief backoff before retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        log.error("[{}] -> FAILED after {} attempts due to concurrent updates.", threadName, maxRetries);
        return false;
    }

    @Transactional(readOnly = true)
    public ProductInventory getProduct(Long productId) {
        return repository.findById(productId).orElse(null);
    }
}
