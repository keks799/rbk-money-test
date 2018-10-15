package money.rbk.test.model;

import lombok.Getter;
import lombok.Setter;
import money.rbk.test.entity.OuterDataEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReconciliationResult {

    private List<OuterDataEntity> conformityList = new ArrayList<>();
    private List<OuterDataEntity> discrepancyList = new ArrayList<>();
    private List<OuterDataEntity> notFoundList = new ArrayList<>();
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
