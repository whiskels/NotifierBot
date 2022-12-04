package com.whiskels.notifier.external.json.debt;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.whiskels.notifier.external.json.currency.CurrencyRate;
import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Comparator;

import static java.math.RoundingMode.HALF_UP;

/**
 * Customer debt data is received from JSON of the following syntax:
 * {"status":1,"content":
 * [{"c2fs_id":"",
 * "wop_id":"",
 * "contractor":"",
 * "finance_subject":""
 * ,"wop":"",
 * "account_manager":"Employee employee",
 * "currency":"RUB",
 * "debt_comment":null,
 * "debtor_delay_180":"0",
 * "debtor_delay_90":"0",
 * "debtor_delay_60":"0",
 * "debtor_delay_30":"0",
 * "debtor_delay_0":"0",
 * "debtor_delay_current":"0",
 * "not_realization":"",
 * "delay_90":"0",
 * "delay_60":"0",
 * "delay_30":"0"},...
 */
@Data
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
class Debt implements Comparable<Debt> {
    private static final Comparator<Debt> TOTAL_DEBT_COMPARATOR = Comparator.comparing(Debt::getTotalRouble)
            .thenComparing(Debt::getContractor)
            .reversed();

    private String contractor;
    @JsonProperty("finance_subject")
    private String financeSubject;
    @JsonProperty("wop")
    private String wayOfPayment;
    @JsonProperty("account_manager")
    private String accountManager;
    private String currency;
    @JsonProperty("debt_comment")
    private String comment;
    @JsonProperty("debtor_delay_180")
    private BigDecimal delay180;
    @JsonProperty("debtor_delay_90")
    private BigDecimal delay90;
    @JsonProperty("debtor_delay_60")
    private BigDecimal delay60;
    @JsonProperty("debtor_delay_30")
    private BigDecimal delay30;
    @JsonProperty("debtor_delay_0")
    private BigDecimal delay0;
    @JsonProperty("debtor_delay_current")
    private String delayCurrent;
    private BigDecimal total;
    private BigDecimal totalRouble;

    public void calculateTotal() {
        total = delay0.add(delay30).add(delay60).add(delay90).add(delay180);
    }

    public void calculateTotalRouble(@Nullable CurrencyRate rate) {
        if (rate == null) {
            totalRouble = total;
            return;
        }
        totalRouble = total.divide(rate.getRate(currency), HALF_UP);
    }

    @Override
    public int compareTo(@NotNull Debt o) {
        return TOTAL_DEBT_COMPARATOR
                .compare(this, o);
    }
}
