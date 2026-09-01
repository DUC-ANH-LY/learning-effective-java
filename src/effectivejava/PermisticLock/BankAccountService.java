package effectivejava.PermisticLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BankAccountService {

    private static final Logger log = LoggerFactory.getLogger(BankAccountService.class);

    private final BankAccountRepository accountRepository;

    public BankAccountService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Initializes a new BankAccount.
     */
    @Transactional
    public BankAccount createAccount(String accountHolder, BigDecimal initialBalance) {
        BankAccount account = new BankAccount(accountHolder, initialBalance);
        return accountRepository.save(account);
    }

    /**
     * Withdraws money safely using PESSIMISTIC_WRITE lock.
     * The DB row is locked (SELECT ... FOR UPDATE) for the entire duration of this transaction.
     * Any other concurrent transaction attempting to read with lock or update this row will wait.
     */
    @Transactional
    public boolean withdrawWithPessimisticLock(Long accountId, BigDecimal amount) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] Requesting PESSIMISTIC_WRITE lock for account ID: {}", threadName, accountId);

        // 1. Acquire pessimistic lock on the database row
        BankAccount account = accountRepository.findByIdWithPessimisticWrite(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        log.info("[{}] -> Lock ACQUIRED. Current Balance: ${}", threadName, account.getBalance());

        // 2. Validate sufficient funds
        if (account.getBalance().compareTo(amount) < 0) {
            log.warn("[{}] -> INSUFFICIENT FUNDS: requested ${}, available ${}. Withdrawal aborted.",
                    threadName, amount, account.getBalance());
            return false;
        }

        // 3. Simulate business logic / external API delay (holding the DB lock)
        try {
            log.info("[{}] -> Processing withdrawal of ${} (simulating 500ms delay)...", threadName, amount);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 4. Update balance and save
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        log.info("[{}] -> SUCCESS: New Balance: ${}. Transaction committing & releasing lock.",
                threadName, newBalance);
        return true;
    }

    /**
     * Unsafe withdrawal WITHOUT locking (for comparison).
     * Subject to race conditions (Lost Update anomaly).
     */
    @Transactional
    public boolean withdrawWithoutLock(Long accountId, BigDecimal amount) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] [NO LOCK] Reading account ID: {}", threadName, accountId);

        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        log.info("[{}] [NO LOCK] -> Read Balance from DB: ${}", threadName, account.getBalance());

        if (account.getBalance().compareTo(amount) < 0) {
            log.warn("[{}] [NO LOCK] -> INSUFFICIENT FUNDS: requested ${}, available ${}. Withdrawal aborted.",
                    threadName, amount, account.getBalance());
            return false;
        }

        try {
            log.info("[{}] [NO LOCK] -> Processing withdrawal of ${} (simulating 500ms delay)...", threadName, amount);
            Thread.sleep(500); // Simulate processing time where race condition occurs
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        log.info("[{}] [NO LOCK] -> Updated & Saved Balance: ${}", threadName, newBalance);
        return true;
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long accountId) {
        return accountRepository.findById(accountId)
                .map(BankAccount::getBalance)
                .orElse(BigDecimal.ZERO);
    }
}
