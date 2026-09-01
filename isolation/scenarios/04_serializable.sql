-- ============================================================================
-- SCENARIO 4: SERIALIZABLE (Highest Isolation & Shared Locking)
-- ============================================================================
-- Concept:
-- SERIALIZABLE is the strictest isolation level.
-- In MySQL InnoDB, when autocommit = 0 (or inside a transaction), plain SELECT
-- statements are implicitly converted to:
--   SELECT ... FOR SHARE (MySQL 8+) / SELECT ... LOCK IN SHARE MODE
--
-- This places Shared Locks (S-Locks) and Range/Gap Locks on all read rows,
-- preventing any other transaction from modifying or inserting records
-- that would affect the result set until the reading transaction finishes.
-- ============================================================================

-- [Preparation] Reset data before starting:
-- CALL reset_lab_data();

-- ----------------------------------------------------------------------------
-- STEP 1: Set Isolation Level to SERIALIZABLE in both sessions
-- ----------------------------------------------------------------------------
-- Run in Session A:
USE isolation_lab;
SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE;
SELECT @@transaction_isolation;

-- Run in Session B:
USE isolation_lab;
SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE;
SELECT @@transaction_isolation;


-- ----------------------------------------------------------------------------
-- STEP 2: Session A starts transaction and reads Alice's record
-- ----------------------------------------------------------------------------
-- Run in Session A:
START TRANSACTION;
SELECT * FROM accounts WHERE id = 1;
-- Under SERIALIZABLE, this SELECT automatically acquires a Shared Lock (S-Lock) on id = 1.


-- ----------------------------------------------------------------------------
-- STEP 3: Session B tries to UPDATE Alice's record
-- ----------------------------------------------------------------------------
-- Run in Session B:
START TRANSACTION;
UPDATE accounts SET balance = balance + 500 WHERE id = 1;
-- OBSERVE: Session B is immediately BLOCKED!
-- Session B requires an Exclusive Lock (X-Lock), which conflicts with Session A's Shared Lock.


-- ----------------------------------------------------------------------------
-- STEP 4: Session A commits, releasing its Shared Lock
-- ----------------------------------------------------------------------------
-- Run in Session A:
COMMIT;

-- OBSERVE: As soon as Session A commits, Session B finishes its UPDATE.


-- ----------------------------------------------------------------------------
-- STEP 5: Session B commits its update
-- ----------------------------------------------------------------------------
-- Run in Session B:
COMMIT;
SELECT * FROM accounts WHERE id = 1;


-- ============================================================================
-- PART B: Range Locking & Phantom Prevention in SERIALIZABLE
-- ============================================================================
-- CALL reset_lab_data();

-- ----------------------------------------------------------------------------
-- STEP 6: Session A reads a range of rows
-- ----------------------------------------------------------------------------
-- Run in Session A:
START TRANSACTION;
SELECT * FROM inventory WHERE category = 'Electronics';
-- Acquires Shared Gap / Next-Key Locks on the 'Electronics' index range.


-- ----------------------------------------------------------------------------
-- STEP 7: Session B tries to INSERT a new Electronics item
-- ----------------------------------------------------------------------------
-- Run in Session B:
START TRANSACTION;
INSERT INTO inventory (item_name, category, quantity, price) 
VALUES ('Wireless Headphones', 'Electronics', 12, 199.99);
-- OBSERVE: Session B is BLOCKED because the insertion falls in Session A's locked range!


-- ----------------------------------------------------------------------------
-- STEP 8: Session A finishes, Session B proceeds
-- ----------------------------------------------------------------------------
-- Run in Session A:
COMMIT;

-- Run in Session B:
-- Session B is now unblocked and completes the INSERT:
COMMIT;

-- Verify the final inventory state:
SELECT * FROM inventory;
