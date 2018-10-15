package money.rbk.test.repository;

import money.rbk.test.entity.OuterDataEntity;
import money.rbk.test.entity.OuterDataEntity.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Store and get outer transactions information
 */

@Repository
public interface OuterDataRepository extends CrudRepository<OuterDataEntity, Long> {

    List<OuterDataEntity> findAllByStatus(Status status);

    List<OuterDataEntity> findAllByTransactionIdAndAmount(Long transactionId, BigDecimal amount);
}
