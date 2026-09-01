-- ============================================================================
-- SCENARIO 2: READ COMMITTED (Non-Repeatable Read Demonstration)
-- ============================================================================
-- Concept:
-- READ COMMITTED prevents Dirty Reads (transactions only see committed data).
-- However, it allows "Non-Repeatable Reads" (also known as Fuzzy Reads):
-- If Transaction A reads a row, and Transaction B updates and commits that row,
-- Transaction A re-reading that row will see the new updated value.
-- ============================================================================

-- [Preparation] Reset data before starting:
-- CALL reset_lab_data();

-- ----------------------------------------------------------------------------
-- STEP 1: Set Isolation Level to READ COMMITTED in both sessions
-- ----------------------------------------------------------------------------
-- Run in Session A:
USE isolation_lab;
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
SELECT @@transaction_isolation;

-- Run in Session B:
USE isolation_lab;
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
SELECT @@transaction_isolation;


-- ----------------------------------------------------------------------------
-- STEP 2: Session A starts transaction and reads initial balance
-- ----------------------------------------------------------------------------
-- Run in Session A:
START TRANSACTION;
SELECT balance FROM accounts WHERE id = 1;
-- OBSERVE: Session A reads Alice balance = 1000.00


-- ----------------------------------------------------------------------------
-- STEP 3: Session B starts transaction, updates balance, and checks uncommitted read
-- ----------------------------------------------------------------------------
-- Run in Session B:
START TRANSACTION;
UPDATE accounts SET balance = 1500.00 WHERE id = 1;

-- Now, before Session B commits, run this in Session A:
-- Run in Session A:
SELECT balance FROM accounts WHERE id = 1;
-- OBSERVE: Session A still sees 1000.00 (No Dirty Read! READ COMMITTED prevents it).


-- ----------------------------------------------------------------------------
-- STEP 4: Session B COMMITS
-- ----------------------------------------------------------------------------
-- Run in Session B:
COMMIT;


-- ----------------------------------------------------------------------------
-- STEP 5: Session A re-reads within the same transaction (NON-REPEATABLE READ)
-- ----------------------------------------------------------------------------
-- Run in Session A:
SELECT balance FROM accounts WHERE id = 1;
-- OBSERVE: Session A now sees 1500.00!
-- Within the exact same transaction (without Session A committing), reading the
-- same row returned two different values (1000.00 vs 1500.00).
-- This is a NON-REPEATABLE READ.

COMMIT;
