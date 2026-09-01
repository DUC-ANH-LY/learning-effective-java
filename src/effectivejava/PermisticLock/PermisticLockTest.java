package effectivejava.PermisticLock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(classes = PermisticLockApplication.class)
public class PermisticLockTest {

    @Autowired
    private BankAccountService accountService;

    @Test
    @DisplayName("Test Pessimistic Lock prevents race conditions & negative balance on concurrent withdrawals")
    void testConcurrentWithdrawalsWithPessimisticLock() throws InterruptedException {
        // Initial balance: $100
        BankAccount account = accountService.createAccount("Bob", new BigDecimal("100.00"));
        Long accountId = account.getId();

        int numberOfThreads = 5;
        BigDecimal withdrawalAmount = new BigDecimal("30.00");
        // Total requested = 5 * $30 = $150 > $100 available.
        // Exactly 3 withdrawals should succeed (3 * $30 = $90), leaving $10.
        // 2 withdrawals must fail due to insufficient funds.

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startGate.await();
                    boolean success = accountService.withdrawWithPessimisticLock(accountId, withdrawalAmount);
                    if (success) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    endGate.countDown();
                }
            });
        }

        // Trigger all threads concurrently
        startGate.countDown();
        endGate.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        BigDecimal finalBalance = accountService.getBalance(accountId);

        System.out.println("Successful withdrawals: " + successCount.get());
        System.out.println("Failed withdrawals: " + failCount.get());
        System.out.println("Final Balance: " + finalBalance);

        // Assertions
        Assertions.assertEquals(3, successCount.get(), "Exactly 3 withdrawals should succeed");
        Assertions.assertEquals(2, failCount.get(), "Exactly 2 withdrawals should fail");
        Assertions.assertEquals(new BigDecimal("10.00"), finalBalance, "Final balance should be exactly $10.00");
    }
}
