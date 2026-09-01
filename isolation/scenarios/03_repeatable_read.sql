-- ============================================================================
-- SCENARIO 3: REPEATABLE READ (MySQL InnoDB Default Level & MVCC / Phantom Read)
-- ============================================================================
-- Concept:
-- In REPEATABLE READ, InnoDB uses Multi-Version Concurrency Control (MVCC)
-- snapshot reads. When a transaction starts, the first SELECT creates a Read View.
-- Any subsequent plain SELECT within the same transaction reads from that snapshot.
--
-- This guarantees repeatable reads and prevents standard phantom reads for plain SELECTs.
-- However, "Current Reads" (Locking reads like SELECT ... FOR UPDATE or UPDATE statements)
-- read the latest committed version and use Next-Key Locks to prevent phantom insertions.
-- ============================================================================

-- [Preparation] Reset data before starting:
-- CALL reset_lab_data();

-- ============================================================================
-- PART A: Solving Non-Repeatable Read with MVCC Snapshot
-- ============================================================================

-- ----------------------------------------------------------------------------
-- STEP 1: Set Isolation Level to REPEATABLE READ
-- ----------------------------------------------------------------------------
-- Run in Session A:
USE isolation_lab;
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
SELECT @@transaction_isolation;

-- Run in Session B:
USE isolation_lab;
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
SELECT @@transaction_isolation;


-- ----------------------------------------------------------------------------
-- STEP 2: Session A reads Alice's balance (creates Read View)
-- ----------------------------------------------------------------------------
-- Run in Session A:
START TRANSACTION;
SELECT balance FROM accounts WHERE id = 1; 
-- OBSERVE: balance = 1000.00


-- ----------------------------------------------------------------------------
-- STEP 3: Session B modifies Alice's balance and COMMITS
-- ----------------------------------------------------------------------------
-- Run in Session B:
START TRANSACTION;
UPDATE accounts SET balance = 3000.00 WHERE id = 1;
COMMIT;


-- ----------------------------------------------------------------------------
-- STEP 4: Session A reads Alice's balance again
-- ----------------------------------------------------------------------------
-- Run in Session A:
SELECT balance FROM accounts WHERE id = 1;
-- OBSERVE: balance is STILL 1000.00!
-- InnoDB MVCC provides a consistent snapshot. Non-Repeatable Read is PREVENTED.
COMMIT;


-- ============================================================================
-- PART B: Phantom Read & MVCC Snapshot vs Current Read
-- ============================================================================
-- CALL reset_lab_data();

-- ----------------------------------------------------------------------------
-- STEP 5: Session A starts transaction and reads electronics inventory
-- ----------------------------------------------------------------------------
-- Run in Session A:
START TRANSACTION;
SELECT * FROM inventory WHERE price > 500.00;
-- OBSERVE: Returns 2 rows (Laptop: $1200, Smartphone: $800)


-- ----------------------------------------------------------------------------
-- STEP 6: Session B inserts a new high-priced product and COMMITS
-- ----------------------------------------------------------------------------
-- Run in Session B:
START TRANSACTION;
INSERT INTO inventory (id, item_name, category, quantity, price) 
VALUES (10, '4K Monitor', 'Electronics', 5, 750.00);
COMMIT;


-- ----------------------------------------------------------------------------
-- STEP 7: Session A performs plain SELECT (Consistent Snapshot Read)
-- ----------------------------------------------------------------------------
-- Run in Session A:
SELECT * FROM inventory WHERE price > 500.00;
-- OBSERVE: Still returns 2 rows! MVCC snapshot hides the phantom row.


-- ----------------------------------------------------------------------------
-- STEP 8: Session A performs a write (UPDATE) that touches the phantom row
-- ----------------------------------------------------------------------------
-- Run in Session A:
-- An UPDATE is a "Current Read" operation in InnoDB:
UPDATE inventory SET quantity = quantity + 1 WHERE price > 500.00;
-- OBSERVE: "Query OK, 3 rows affected" (Session A updated the newly inserted row too!)

-- Now Session A selects again:
SELECT * FROM inventory WHERE price > 500.00;
-- OBSERVE: 3 rows now appear (including '4K Monitor') because Session A created
-- its own new version of that row!
COMMIT;


-- ============================================================================
-- PART C: Preventing Phantom Inserts using Locking Reads (Next-Key Lock)
-- ============================================================================
-- CALL reset_lab_data();

-- Run in Session A:
START TRANSACTION;
-- Locking Read (FOR UPDATE) places Next-Key Lock (Record Lock + Gap Lock) on the range price > 500:
SELECT * FROM inventory WHERE price > 500.00 FOR UPDATE;

-- Run in Session B:
START TRANSACTION;
-- Try inserting an item that falls in the locked gap:
INSERT INTO inventory (id, item_name, category, quantity, price) 
VALUES (11, 'Gaming Console', 'Electronics', 7, 650.00);
-- OBSERVE: Session B is BLOCKED waiting for the lock held by Session A!

-- Run in Session A:
COMMIT;

-- Now Session B unblocks and completes its INSERT:
-- Run in Session B:
COMMIT;
