package money.rbk.test.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity class to store outer data (from .csv)
 */
@Entity
@Table(name = "outer_data")
@Getter
@Setter
public class OuterDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "data")
    private String data;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    public static OuterDataEntity createFromPlainData(Long transactionId,
                                                      BigDecimal amount,
                                                      String data,
                                                      Status status) {
        OuterDataEntity entity = new OuterDataEntity();
        entity.setTransactionId(transactionId);
        entity.setAmount(amount);
        entity.setData(data);
        entity.setStatus(status);

        return entity;
    }

    public enum Status {
        NEW,        // new records from file (outer source)
        NEED_CHECK, // if already processed record in the file (outer source) again. Probably there are any kind of changes (e.g. amount)
        DONE        // if reconciliation is done
    }
}
