# MySQL Transaction Isolation Levels Lab

A hands-on practice environment with **Docker Compose**, **MySQL (latest)**, **persistent volume storage**, and interactive step-by-step SQL scenarios to explore and understand the 4 ANSI SQL transaction isolation levels in MySQL InnoDB.

---

## 📊 Summary of Isolation Levels & Concurrency Phenomena

| Isolation Level | Dirty Read | Non-Repeatable Read | Phantom Read (ANSI) | MySQL InnoDB Behavior |
| :--- | :---: | :---: | :---: | :--- |
| **READ UNCOMMITTED** | ❌ Allowed | ❌ Allowed | ❌ Allowed | Reads uncommitted dirty pages directly from memory |
| **READ COMMITTED** | ✅ Prevented | ❌ Allowed | ❌ Allowed | Each `SELECT` generates a fresh MVCC Read View |
| **REPEATABLE READ** *(Default)* | ✅ Prevented | ✅ Prevented | ✅ Prevented (Snapshot)* | First `SELECT` creates Read View for the whole transaction; uses Next-Key Locks for locking reads |
| **SERIALIZABLE** | ✅ Prevented | ✅ Prevented | ✅ Prevented | Converts plain `SELECT`s to `SELECT ... FOR SHARE` (acquires shared locks, blocking concurrent writes) |

*\*Note: In MySQL InnoDB's `REPEATABLE READ`, MVCC snapshot reads prevent phantom reads for plain `SELECT`. For locking reads (`SELECT ... FOR UPDATE`), Next-Key Locks (record lock + gap lock) prevent phantom inserts.*

---

## 🚀 Quick Start

### 1. Start MySQL with Docker Compose
Navigate to the `isolation` directory in your terminal and run:

```bash
docker compose up -d
```

### 2. Check Container Status
```bash
docker compose ps
```

The container `mysql_isolation_lab` will start and automatically run the initialization script [`init/01_init.sql`](file:///C:/Users/ducanh/Downloads/learning-effective-java/isolation/init/01_init.sql) to create the database `isolation_lab`, tables `accounts` and `inventory`, and seed data.

### 3. Open Two Concurrent MySQL Sessions
To practice concurrency anomalies, open **two separate terminal windows** (Session A and Session B):

#### Terminal 1 (Session A):
```bash
docker exec -it mysql_isolation_lab mysql -u lab_user -plab_password isolation_lab
```

#### Terminal 2 (Session B):
```bash
docker exec -it mysql_isolation_lab mysql -u lab_user -plab_password isolation_lab
```

*(Alternatively, connect using root: `mysql -u root -prootpassword isolation_lab` or via your favorite SQL client like DBeaver, IntelliJ/DataGrip, or MySQL Workbench on port `3306`)*.

---

## 🔄 Resetting Lab Data
At any time between scenarios, you can reset the tables back to their original state by executing:

```sql
CALL reset_lab_data();
```

---

## 🧪 Step-by-Step Practice Scenarios

Detailed standalone SQL scripts are located in the [`scenarios/`](file:///C:/Users/ducanh/Downloads/learning-effective-java/isolation/scenarios) directory:
- [`01_read_uncommitted.sql`](file:///C:/Users/ducanh/Downloads/learning-effective-java/isolation/scenarios/01_read_uncommitted.sql)
- [`02_read_committed.sql`](file:///C:/Users/ducanh/Downloads/learning-effective-java/isolation/scenarios/02_read_committed.sql)
- [`03_repeatable_read.sql`](file:///C:/Users/ducanh/Downloads/learning-effective-java/isolation/scenarios/03_repeatable_read.sql)
- [`04_serializable.sql`](file:///C:/Users/ducanh/Downloads/learning-effective-java/isolation/scenarios/04_serializable.sql)

---

### 🔬 Scenario 1: READ UNCOMMITTED (Dirty Read)

**Phenomenon**: A transaction reads data that has been modified by another concurrent transaction but **not yet committed**. If the other transaction rolls back, the first transaction has operated on invalid ("dirty") data.

| Step | Session A | Session B | Explanation |
|---|---|---|---|
| **1** | `SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;` | `SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;` | Set isolation level to Read Uncommitted in both sessions |
| **2** | `START TRANSACTION;`<br>`UPDATE accounts SET balance = 5000.00 WHERE id = 1;` | | Session A modifies Alice's balance to 5000 (DO NOT COMMIT YET) |
| **3** | | `START TRANSACTION;`<br>`SELECT * FROM accounts WHERE id = 1;` | **Dirty Read occurs!** Session B sees `5000.00` before Session A commits |
| **4** | `ROLLBACK;` | | Session A cancels the change |
| **5** | | `SELECT * FROM accounts WHERE id = 1;`<br>`COMMIT;` | Session B now sees `1000.00`. The `5000.00` was phantom data that never truly existed! |

---

### 🔬 Scenario 2: READ COMMITTED (Non-Repeatable Read)

**Phenomenon**: Prevents Dirty Read, but reading the **same row twice** inside the same transaction produces different values because another transaction modified and committed the row in between.

| Step | Session A | Session B | Explanation |
|---|---|---|---|
| **1** | `SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;` | `SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;` | Set isolation level to Read Committed in both sessions |
| **2** | `START TRANSACTION;`<br>`SELECT balance FROM accounts WHERE id = 1;` | | Session A reads Alice balance: **1000.00** |
| **3** | | `START TRANSACTION;`<br>`UPDATE accounts SET balance = 1500.00 WHERE id = 1;` | Session B modifies balance to 1500 (uncommitted) |
| **4** | `SELECT balance FROM accounts WHERE id = 1;` | | Session A still sees **1000.00** (Dirty read prevented!) |
| **5** | | `COMMIT;` | Session B commits its change |
| **6** | `SELECT balance FROM accounts WHERE id = 1;`<br>`COMMIT;` | | **Non-Repeatable Read occurs!** Session A re-reads within the same transaction and now sees **1500.00** |

---

### 🔬 Scenario 3: REPEATABLE READ (MySQL Default & MVCC / Phantom Read)

**Phenomenon**: MySQL InnoDB uses **Multi-Version Concurrency Control (MVCC)** snapshot reads. The first `SELECT` defines a Read View snapshot that remains consistent for the entire transaction.

#### Part A: Preventing Non-Repeatable Read with MVCC
| Step | Session A | Session B | Explanation |
|---|---|---|---|
| **1** | `SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;` | `SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;` | Set isolation level to Repeatable Read |
| **2** | `START TRANSACTION;`<br>`SELECT balance FROM accounts WHERE id = 1;` | | Session A reads Alice balance: **1000.00** (Read View created) |
| **3** | | `START TRANSACTION;`<br>`UPDATE accounts SET balance = 3000.00 WHERE id = 1;`<br>`COMMIT;` | Session B modifies balance and commits |
| **4** | `SELECT balance FROM accounts WHERE id = 1;`<br>`COMMIT;` | | Session A **still sees 1000.00** because it reads from its initial MVCC snapshot! |

#### Part B: Phantom Read & Current Read (`UPDATE`)
| Step | Session A | Session B | Explanation |
|---|---|---|---|
| **1** | `START TRANSACTION;`<br>`SELECT * FROM inventory WHERE price > 500.00;` | | Session A sees 2 items (Laptop, Smartphone) |
| **2** | | `START TRANSACTION;`<br>`INSERT INTO inventory (id, item_name, category, quantity, price) VALUES (10, '4K Monitor', 'Electronics', 5, 750.00);`<br>`COMMIT;` | Session B inserts a new item with price > 500 and commits |
| **3** | `SELECT * FROM inventory WHERE price > 500.00;` | | Session A snapshot read still shows 2 items |
| **4** | `UPDATE inventory SET quantity = quantity + 1 WHERE price > 500.00;` | | **Current Read:** `UPDATE` operates on the latest data. Notice `3 rows affected`! |
| **5** | `SELECT * FROM inventory WHERE price > 500.00;`<br>`COMMIT;` | | Session A now sees all 3 items because it updated the new row itself. |

#### Part C: Preventing Phantom Inserts with Locking Reads (Next-Key Locks)
| Step | Session A | Session B | Explanation |
|---|---|---|---|
| **1** | `START TRANSACTION;`<br>`SELECT * FROM inventory WHERE price > 500.00 FOR UPDATE;` | | Session A locks the index range (`price > 500`) with Next-Key / Gap Locks |
| **2** | | `START TRANSACTION;`<br>`INSERT INTO inventory (id, item_name, category, quantity, price) VALUES (11, 'Gaming Console', 'Electronics', 7, 650.00);` | **Session B is BLOCKED!** It cannot insert into the locked gap |
| **3** | `COMMIT;` | | Session A commits and releases lock |
| **4** | | *(Session B unblocks and finishes INSERT)*<br>`COMMIT;` | Session B finishes successfully |

---

### 🔬 Scenario 4: SERIALIZABLE (Strictest Isolation & Implicit Shared Locking)

**Phenomenon**: Plain `SELECT` queries implicitly acquire shared locks (`SELECT ... FOR SHARE`), converting concurrent read/write operations into strictly serialized execution.

| Step | Session A | Session B | Explanation |
|---|---|---|---|
| **1** | `SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE;` | `SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE;` | Set isolation level to Serializable |
| **2** | `START TRANSACTION;`<br>`SELECT * FROM accounts WHERE id = 1;` | | Session A reads Alice and automatically acquires a Shared Lock (S-Lock) |
| **3** | | `START TRANSACTION;`<br>`UPDATE accounts SET balance = balance + 500 WHERE id = 1;` | **Session B is BLOCKED!** Exclusive Lock (X-Lock) conflicts with Session A's S-Lock |
| **4** | `COMMIT;` | | Session A commits, releasing the Shared Lock |
| **5** | | *(Session B immediately unblocks and completes update)*<br>`COMMIT;` | Session B commits |

---

## 💾 Volume Persistence & Management

- Database data is stored in the Docker volume `mysql_data`, mounted to `/var/lib/mysql`.
- To stop the database without losing data:
  ```bash
  docker compose stop
  ```
- To start it again:
  ```bash
  docker compose start
  ```
- To completely destroy the container **and wipe the volume** (clean state):
  ```bash
  docker compose down -v
  ```


- read uncommited:
  - trxA can read all uncommited change from trxB (dirty read)
  - select a -> a = 1
  - trxA: a = 2 (not commit yet) || trxB: select a -> a = 2 
  - can use for update, for share
- 
  
















