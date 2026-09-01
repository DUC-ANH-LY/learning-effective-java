# Optimistic Locking in Java & Spring Data JPA

## 1. Overview

**Optimistic Locking** assumes that multiple concurrent transactions rarely conflict with each other. Therefore, **no database-level lock is acquired when reading the record**.

Instead, JPA/Hibernate detects conflicting updates at commit time by using a **`@Version`** field.

```
Transaction 1                               Database Row                             Transaction 2
      |                                (stock=10, version=0)                               |
      |--- 1. Reads (stock=10, v=0) ------------->|                                        |
      |                                           |<--- 2. Reads (stock=10, v=0) ----------|
      |                                           |                                        |
      |--- 3. UPDATE ... WHERE v=0 -------------->|                                        |
      |    (stock=8, version=1 in DB)             |                                        |
      |--- 4. COMMIT SUCCESS -------------------->|                                        |
      |                                           |                                        |
      |                                           |<--- 5. UPDATE ... WHERE v=0 -----------|
      |                                           |    (0 rows affected! version is now 1) |
      |                                           |--- 6. OptimisticLockException -------->|
      |                                           |    (Catches conflict & retries)        |
```

---

## 2. Pessimistic vs. Optimistic Locking

| Feature | Optimistic Locking | Pessimistic Locking |
| :--- | :--- | :--- |
| **Locking Mechanism** | Software level (`@Version` field checking) | Database level (`SELECT ... FOR UPDATE`) |
| **Concurrency / Blocking** | Non-blocking. Other threads read and write freely. | Blocking. Other threads wait until lock release. |
| **Conflict Resolution** | Throws `OptimisticLockException` on commit | Prevents conflicts before they happen |
| **Best Used When** | Read-heavy workloads, low conflict frequency | High write contention (banking, ticketing) |

---

## 3. How It Works Under the Hood

When an entity with `@Version` is saved:

```java
@Entity
public class ProductInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer stock;

    @Version
    private Long version;
}
```

Hibernate generates the following SQL when updating:

```sql
UPDATE product_inventory 
SET stock = ?, version = version + 1 
WHERE id = ? AND version = ?
```

If another transaction already updated the row, `version = ?` does not match, so `0` rows are updated. Hibernate detects this and throws `jakarta.persistence.OptimisticLockException` (wrapped by Spring as `ObjectOptimisticLockingFailureException`).

---

## 4. Retrying on Optimistic Lock Conflicts

When an `ObjectOptimisticLockingFailureException` occurs, the application can re-fetch the latest state and retry the operation:

```java
public boolean decreaseStockWithRetry(Long productId, int quantity, int maxRetries) {
    int attempts = 0;
    while (attempts < maxRetries) {
        attempts++;
        try {
            return decreaseStock(productId, quantity);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Conflict detected on attempt {}. Retrying...", attempts);
            Thread.sleep(100); // Backoff before retry
        }
    }
    return false;
}
```

---

## 5. How to Run

### Windows PowerShell:
```powershell
.\src\effectivejava\OptimisticLock\run.ps1
```

### Windows Command Prompt (CMD):
```cmd
src\effectivejava\OptimisticLock\run.bat
```

### In IntelliJ IDEA:
Run `public static void main` in [`OptimisticLockApplication.java`](file:///C:/Users/ducanh/Downloads/learning-effective-java/src/effectivejava/OptimisticLock/OptimisticLockApplication.java).
