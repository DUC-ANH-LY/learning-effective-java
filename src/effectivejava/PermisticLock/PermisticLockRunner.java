package effectivejava.PermisticLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Simulation runner demonstrating:
 * 1. WITHOUT LOCK: Concurrent withdrawals cause Race Condition (Lost Update anomaly).
 * 2. WITH PESSIMISTIC LOCK: Safe, serialized execution preventing Race Condition.
 */
@Component
public class PermisticLockRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PermisticLockRunner.class);

    private final BankAccountService accountService;

    public PermisticLockRunner(BankAccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) throws Exception {
        runWithoutLockSimulation();
        Thread.sleep(1000); // brief pause between experiments
        runWithPessimisticLockSimulation();
    }

    /**
     * EXPERIMENT 1: WITHOUT LOCK
     * - Initial balance: $100.00
     * - Thread 1 attempts to withdraw $70.00
     * - Thread 2 attempts to withdraw $70.00
     * Both threads read $100.00 concurrently before either commits.
     * Both calculate $100 - $70 = $30.00 and write $30.00.
     * RESULT: $140 was withdrawn in total, but remaining balance is $30! (Bank lost $70!)
     */
    private void runWithoutLockSimulation() throws InterruptedException {
        log.info("\n");
        log.info("################################################################################");
        log.info("  EXPERIMENT 1: WITHOUT LOCKING (Demonstrating Race Condition / Lost Update)    ");
        log.info("################################################################################");

        BankAccount account = accountService.createAccount("Victim_WithoutLock", new BigDecimal("100.00"));
        Long accountId = account.getId();
        log.info("Created Account ID [{}] with initial balance: $100.00", accountId);

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            final String threadName = "NoLock-Thread-" + i;
            executor.submit(() -> {
                try {
                    Thread.currentThread().setName(threadName);
                    startGate.await(); // wait so both threads fire at the exact same millisecond
                    accountService.withdrawWithoutLock(accountId, new BigDecimal("70.00"));
                } catch (Exception e) {
                    log.error("[{}] Error: {}", threadName, e.getMessage());
                } finally {
                    endGate.countDown();
                }
            });
        }

        log.info(">>> Firing 2 concurrent withdrawal requests of $70.00 each WITHOUT LOCK...");
        startGate.countDown();

        endGate.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        BigDecimal finalBalance = accountService.getBalance(accountId);
        log.info("--------------------------------------------------------------------------------");
        log.info("EXPERIMENT 1 RESULT (WITHOUT LOCK):");
        log.info("Initial Balance:      $100.00");
        log.info("Total Requested:      $140.00 (2 x $70.00)");
        log.info("Actual Final Balance: ${} (RACE CONDITION! Both withdrew $70, but balance is $30!)", finalBalance);
        log.info("--------------------------------------------------------------------------------\n");
    }

    /**
     * EXPERIMENT 2: WITH PESSIMISTIC LOCK
     * - Initial balance: $100.00
     * - Thread 1 acquires row lock (SELECT ... FOR UPDATE), withdraws $70 -> $30.00.
     * - Thread 2 is blocked until Thread 1 commits.
     * - Thread 2 acquires lock, reads fresh balance ($30.00), sees $30 < $70, safely aborted.
     * RESULT: Only 1 withdrawal succeeds. Final balance = $30.00. (Safe!).
     */
    private void runWithPessimisticLockSimulation() throws InterruptedException {
        log.info("\n");
        log.info("################################################################################");
        log.info("  EXPERIMENT 2: WITH PESSIMISTIC LOCK (Safe & Serialized Database Lock)         ");
        log.info("################################################################################");

        BankAccount account = accountService.createAccount("Protected_WithLock", new BigDecimal("100.00"));
        Long accountId = account.getId();
        log.info("Created Account ID [{}] with initial balance: $100.00", accountId);

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            final String threadName = "Lock-Thread-" + i;
            executor.submit(() -> {
                try {
                    Thread.currentThread().setName(threadName);
                    startGate.await(); // wait so both threads fire at the exact same millisecond
                    accountService.withdrawWithPessimisticLock(accountId, new BigDecimal("70.00"));
                } catch (Exception e) {
                    log.error("[{}] Error: {}", threadName, e.getMessage());
                } finally {
                    endGate.countDown();
                }
            });
        }

        log.info(">>> Firing 2 concurrent withdrawal requests of $70.00 each WITH PESSIMISTIC LOCK...");
        startGate.countDown();

        endGate.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        BigDecimal finalBalance = accountService.getBalance(accountId);
        log.info("--------------------------------------------------------------------------------");
        log.info("EXPERIMENT 2 RESULT (WITH PESSIMISTIC LOCK):");
        log.info("Initial Balance:      $100.00");
        log.info("Total Requested:      $140.00 (2 x $70.00)");
        log.info("Actual Final Balance: ${} (SAFE! 1 withdrawal succeeded, 1 rejected safely)", finalBalance);
        log.info("--------------------------------------------------------------------------------\n");
    }
}
