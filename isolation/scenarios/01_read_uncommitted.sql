-- ============================================================================
-- SCENARIO 1: READ UNCOMMITTED (Dirty Read Demonstration)
-- ============================================================================
-- Concept: 
-- In READ UNCOMMITTED, transactions can see uncommitted modifications made by
-- other concurrent transactions. This leads to the "Dirty Read" anomaly.
-- ============================================================================

-- [Preparation] Reset data before starting:
-- CALL reset_lab_data();

-- ----------------------------------------------------------------------------
-- STEP 1: Configure Isolation Levels in both sessions
-- ----------------------------------------------------------------------------
-- Run in Session A:
USE isolation_lab;
SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
SELECT @@transaction_isolation;

-- Run in Session B:
USE isolation_lab;
SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
SELECT @@transaction_isolation;


-- ----------------------------------------------------------------------------
-- STEP 2: Session A starts a transaction and modifies data without committing
-- ----------------------------------------------------------------------------
-- Run in Session A:
START TRANSACTION;
SELECT * FROM accounts WHERE id = 1; -- Alice balance is 1000.00

-- Alice's balance is updated to 5000.00 (NOT YET COMMITTED)
UPDATE accounts SET balance = 5000.00 WHERE id = 1;
SELECT * FROM accounts WHERE id = 1; -- Session A sees 5000.00


-- ----------------------------------------------------------------------------
-- STEP 3: Session B queries Alice's balance (DIRTY READ occurs)
-- ----------------------------------------------------------------------------
-- Run in Session B:
START TRANSACTION;
-- Session B reads uncommitted data written by Session A:
SELECT * FROM accounts WHERE id = 1; 
-- OBSERVE: Session B sees balance = 5000.00, even though Session A never committed!


-- ----------------------------------------------------------------------------
-- STEP 4: Session A aborts (ROLLBACK)
-- ----------------------------------------------------------------------------
-- Run in Session A:
ROLLBACK;
SELECT * FROM accounts WHERE id = 1; -- Back to 1000.00


-- ----------------------------------------------------------------------------
-- STEP 5: Session B checks again
-- ----------------------------------------------------------------------------
-- Run in Session B:
SELECT * FROM accounts WHERE id = 1; -- Now balance is 1000.00
COMMIT;
-- CONCLUSION: Session B made business decisions based on 5000.00 which was never
-- actually committed to the database. This is a DIRTY READ.
