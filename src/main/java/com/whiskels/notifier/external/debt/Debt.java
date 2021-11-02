package com.whiskels.notifier.external.debt;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import static com.whiskels.notifier.common.util.FormatUtil.formatDouble;
import static com.whiskels.notifier.external.debt.DebtUtil.TOTAL_DEBT_COMPARATOR;

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
public class Debt implements Comparable<Debt> {
    private String contractor;
    @JsonProperty("finance_subject")
    private String financeSubject;
    @JsonProperty("wop")
    private String wayOfPayment;
    @JsonProperty("account_manager")
    private String accountManager;
    private String currency;
    @JsonProperty("debt_comment")
    private String debtComment;
    @JsonProperty("debtor_delay_180")
    private double delay180;
    @JsonProperty("debtor_delay_90")
    private double delay90;
    @JsonProperty("debtor_delay_60")
    private double delay60;
    @JsonProperty("debtor_delay_30")
    private double delay30;
    @JsonProperty("debtor_delay_0")
    private double delay0;
    @JsonProperty("debtor_delay_current")
    private String debtorDelayCurrent;

    private double totalDebt;

    @Setter
    private double totalDebtRouble = Double.NaN;

    @Override
    public String toString() {
        return String.format("*%s*%n   %s%n   %s%n   %s%n   *%s %s*%n%s"
                , contractor
                , financeSubject
                , wayOfPayment
                , accountManager
                , formatDouble(totalDebt)
                , currency
                , debtComment != null ? debtComment : "No comment"
        );
    }

    @Override
    public int compareTo(@NotNull Debt o) {
        return TOTAL_DEBT_COMPARATOR
                .compare(this, o);
    }
}
