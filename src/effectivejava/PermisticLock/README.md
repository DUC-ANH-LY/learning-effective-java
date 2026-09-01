# Pessimistic Locking in Java & Spring Data JPA

## 1. Overview

**Pessimistic Locking** is a concurrency control mechanism based on the assumption that conflicting updates are *very likely* to occur. Instead of checking for conflicts at commit time (like Optimistic Locking), it **acquires an exclusive database-level lock on the row immediately when reading it**.

This prevents any concurrent transaction from modifying or reading the locked row until the current transaction commits or rolls back.

```
Transaction 1                               Database Row                             Transaction 2
      |                                           |                                        |
      |--- SELECT ... FOR UPDATE (Lock Acquired)->|                                        |
      |                                           |                                        |
      |                                           |<-- SELECT ... FOR UPDATE (BLOCKED) ----|
      |   (Processes business logic)              |    (Waits for Lock release...)         |
      |                                           |                                        |
      |--- UPDATE balance = $30 ----------------->|                                        |
      |--- COMMIT (Lock Released) --------------->|                                        |
      |                                           |--- Lock Acquired by Tx 2 ------------->|
      |                                           |    (Reads updated balance $30)         |
```

---

## 2. Pessimistic Locking vs. Optimistic Locking

| Feature | Pessimistic Locking | Optimistic Locking |
| :--- | :--- | :--- |
| **Strategy** | Locks the record in DB on read (`FOR UPDATE`) | No lock on read; checks `@Version` on update |
| **Conflict Handling** | Prevents concurrent conflicting transactions by blocking them | Detects conflict on commit and throws `OptimisticLockException` |
| **Best For** | High contention / High conflict probability (e.g. Bank transfers, Flash sales inventory) | Low contention / High read-to-write ratio |
| **Overhead** | DB lock contention, potential deadlocks, reduced throughput | Rollback & retry overhead when conflicts occur |

---

## 3. Spring Data JPA Lock Modes

| Lock Mode | SQL Generated | Description |
| :--- | :--- | :--- |
| `LockModeType.PESSIMISTIC_WRITE` | `SELECT ... FOR UPDATE` | **Exclusive lock**. Blocks other transactions from modifying, deleting, or acquiring read/write locks on the row. |
| `LockModeType.PESSIMISTIC_READ` | `SELECT ... FOR SHARE` / `LOCK IN SHARE MODE` | **Shared lock**. Allows other transactions to read, but blocks them from modifying or deleting the row. |
| `LockModeType.PESSIMISTIC_FORCE_INCREMENT` | `SELECT ... FOR UPDATE` | Exclusive lock that also increments the `@Version` attribute of the entity. |

---

## 4. Key Components in this Example

### 1. Entity: `BankAccount.java`
Represents an entity whose balance must not go negative or experience lost updates.

```java
@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountHolder;
    private BigDecimal balance;
    // Getters & Setters
}
```

### 2. Repository: `BankAccountRepository.java`
Uses `@Lock(LockModeType.PESSIMISTIC_WRITE)` and `@QueryHints` for timeout control:

```java
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("SELECT b FROM BankAccount b WHERE b.id = :id")
    Optional<BankAccount> findByIdWithPessimisticWrite(@Param("id") Long id);
}
```

### 3. Service: `BankAccountService.java`
Encapsulates transaction boundaries (`@Transactional`) ensuring the lock is held until the transaction finishes:

```java
@Transactional
public boolean withdrawWithPessimisticLock(Long accountId, BigDecimal amount) {
    // 1. Locks the database record
    BankAccount account = accountRepository.findByIdWithPessimisticWrite(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));

    // 2. Safely checks balance
    if (account.getBalance().compareTo(amount) < 0) {
        return false; // Insufficient funds
    }

    // 3. Deducts amount and saves
    account.setBalance(account.getBalance().subtract(amount));
    accountRepository.save(account);
    return true; // Lock is released upon commit
}
```

### 4. Runner: `PermisticLockRunner.java`
Runs two experiments back-to-back:
1. **Experiment 1 (Without Lock)**: Demonstrates the **Lost Update / Race Condition** problem.
2. **Experiment 2 (With Pessimistic Lock)**: Demonstrates how **`PESSIMISTIC_WRITE`** solves the problem.

---

## 5. Live Simulation Results

### ❌ Experiment 1: Without Lock (Lost Update / Overdraft Anomaly)

Both threads read `$100.00` simultaneously and overwrite each other's changes:

```text
[NoLock-Thread-1] [NO LOCK] -> Read Balance from DB: $100.00
[NoLock-Thread-2] [NO LOCK] -> Read Balance from DB: $100.00
[NoLock-Thread-1] [NO LOCK] -> Processing withdrawal of $70.00...
[NoLock-Thread-2] [NO LOCK] -> Processing withdrawal of $70.00...
[NoLock-Thread-1] [NO LOCK] -> Updated & Saved Balance: $30.00
[NoLock-Thread-2] [NO LOCK] -> Updated & Saved Balance: $30.00

--------------------------------------------------------------------------------
EXPERIMENT 1 RESULT (WITHOUT LOCK):
Initial Balance:      $100.00
Total Requested:      $140.00 (2 x $70.00)
Actual Final Balance: $30.00 (RACE CONDITION! $140 was withdrawn, but balance is $30!)
--------------------------------------------------------------------------------
```

---

###  Experiment 2: With Pessimistic Lock (`SELECT ... FOR UPDATE`)

Thread 2 is blocked by the database until Thread 1 commits. Thread 2 then reads the fresh balance and fails safely:

```text
[Lock-Thread-2] Requesting PESSIMISTIC_WRITE lock for account ID: 2
[Lock-Thread-1] Requesting PESSIMISTIC_WRITE lock for account ID: 2
Hibernate: select ... from bank_accounts where id=? for update

[Lock-Thread-2] -> Lock ACQUIRED. Current Balance: $100.00
[Lock-Thread-2] -> Processing withdrawal of $70.00...
[Lock-Thread-1] -> (Blocked by DB engine, waiting for lock release...)

[Lock-Thread-2] -> SUCCESS: New Balance: $30.00. Transaction committed & lock released.
[Lock-Thread-1] -> Lock ACQUIRED. Current Balance: $30.00
[Lock-Thread-1] -> INSUFFICIENT FUNDS: requested $70.00, available $30.00. Withdrawal aborted.

--------------------------------------------------------------------------------
EXPERIMENT 2 RESULT (WITH PESSIMISTIC LOCK):
Initial Balance:      $100.00
Total Requested:      $140.00 (2 x $70.00)
Actual Final Balance: $30.00 (SAFE! 1 withdrawal succeeded, 1 rejected safely)
--------------------------------------------------------------------------------
```

---

## 6. Best Practices & Caveats

1. **Keep Transactions Short**: Keep the critical section inside `@Transactional` as brief as possible to avoid holding DB locks and blocking other database transactions.
2. **Set a Lock Timeout**: Always set a lock timeout (`jakarta.persistence.lock.timeout`) to avoid transactions hanging indefinitely when waiting on locks.
3. **Avoid Deadlocks**: Ensure resources are locked in a consistent global order across all transactions (e.g. always sort account IDs ascending when transferring money between two accounts).
