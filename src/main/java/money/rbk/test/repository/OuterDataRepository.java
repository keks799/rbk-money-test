package money.rbk.test.repository;

import money.rbk.test.entity.OuterDataEntity;
import money.rbk.test.entity.OuterDataEntity.Status;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Store and get outer transactions information
 */

@Repository
public interface OuterDataRepository extends CrudRepository<OuterDataEntity, Long> {

    @Query("select d from OuterDataEntity d where d.status in :statuses")
    List<OuterDataEntity> findAllByStatuses(@Param("statuses") Status... statuses);

    OuterDataEntity findByTransactionId(Long transactionId);
}
