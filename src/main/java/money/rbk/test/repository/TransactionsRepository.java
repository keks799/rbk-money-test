package money.rbk.test.repository;

import money.rbk.test.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interaction with stored transactions information
 */

@Repository
public interface TransactionsRepository extends CrudRepository<TransactionEntity, Long> {

}
