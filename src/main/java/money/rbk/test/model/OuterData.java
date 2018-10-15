package money.rbk.test.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Outer data model to parse it with fasterxml. Not need after xcode implementation
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OuterData {

    @JsonProperty("PID")
    private Long id;

    @JsonProperty("PAMOUNT")
    private BigDecimal amount;

    @JsonProperty("PDATA")
    private String data;
}
