package money.rbk.test.repository;

import money.rbk.test.entity.OuterDataEntity;
import money.rbk.test.entity.OuterDataEntity.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Store and get outer transactions information
 */

@Repository
public interface OuterDataRepository extends CrudRepository<OuterDataEntity, Long> {

    List<OuterDataEntity> findByStatus(Status status);
}
