package effectivejava.PermisticLock;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    /**
     * PESSIMISTIC_WRITE (Exclusive Lock):
     * Translates to 'SELECT ... FOR UPDATE' in SQL (e.g., PostgreSQL/MySQL/H2).
     * No other transaction can read (with lock) or modify this row until the current transaction completes.
     *
     * We also specify a lock timeout hint (3000ms = 3 seconds) to prevent infinite blocking.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("SELECT b FROM BankAccount b WHERE b.id = :id")
    Optional<BankAccount> findByIdWithPessimisticWrite(@Param("id") Long id);

    /**
     * PESSIMISTIC_READ (Shared Lock):
     * Translates to 'SELECT ... FOR SHARE' or 'LOCK IN SHARE MODE' in SQL.
     * Prevents other transactions from updating or deleting the record, but allows them to read.
     */
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT b FROM BankAccount b WHERE b.id = :id")
    Optional<BankAccount> findByIdWithPessimisticRead(@Param("id") Long id);
}
