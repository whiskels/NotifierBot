package model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {

    @JsonProperty("contractor")
    private String contractor;
    @JsonProperty("finance_subject")
    private String financeSubject;
    @JsonProperty("wop")
    private String wayOfPayment;
    @JsonProperty("account_manager")
    private String accountManager;
    @JsonProperty("currency")
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

    /*
     * Calculates overall customer debt with delay more than 0 days
     */
    public void calculateOverallDebt() {
        totalDebt = delay0 + delay30 + delay60 + delay90 + delay180;
    }

    public double getOverallDebt() {
        return totalDebt;
    }

    public String getAccountManager() {
        return accountManager;
}

    /*
     * Formats total debt to easy-to-read format
     */
    private String formatValue(Number value) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat("###,###,###,###.#", formatSymbols);
        return formatter.format(value);
    }

    @Override
    public String toString() {
        return String.format("*%s*\n  %s\n  %s\n  %s\n  *%s %s*\n%s",
                contractor, financeSubject, wayOfPayment, accountManager,
                formatValue(totalDebt), currency, debtComment);
    }

}
