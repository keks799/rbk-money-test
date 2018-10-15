package money.rbk.test.model;

import lombok.Getter;
import lombok.Setter;
import money.rbk.test.entity.OuterDataEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Reconciliation result model for report
 */
@Getter
@Setter
public class ReconciliationResult {

    private List<OuterDataEntity> conformityList = new ArrayList<>();   // transactions which are matched correctly
    private List<OuterDataEntity> discrepancyList = new ArrayList<>();  // transactions which doesn't matched
    private List<OuterDataEntity> notFoundList = new ArrayList<>();     // transactions which don't found in transaction table
//    private Set<Long> notUniqueDbRecordsIdList = new HashSet<>();

    private void logResult(StringBuilder builder, List<OuterDataEntity> list) {
        if (list.size() == 0) {
            builder.append("0");
        } else {
            for (OuterDataEntity outerDataEntity : list) {
                builder.append(outerDataEntity.getTransactionId());
                builder.append(" ");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nconformity list: ");
        logResult(builder, conformityList);

        builder.append("\ndiscrepancy list: ");
        logResult(builder, discrepancyList);

        builder.append("\nnot found list: ");
        logResult(builder, notFoundList);

        return builder.toString();
    }
}
