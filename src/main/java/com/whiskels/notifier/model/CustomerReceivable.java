package com.whiskels.notifier.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

import static com.whiskels.notifier.util.CustomerReceivableUtil.AMOUNT_COMPARATOR;
import static com.whiskels.notifier.util.FormatUtil.formatDouble;

/**
 * Customer receivable data is received from JSON of the following syntax:
 * [{"id":111,
 * "date":"2020-11-19",
 * "currency":"",
 * "amount":"",
 * "amount_usd":"",
 * "amount_rub":"",
 * "bank":"",
 * "bank_account":"",
 * "legal_name":"",
 * "contractor":"",
 * "type":"",
 * "contractor_account":,
 * "contractor_legal_name":"",
 * "category":"",
 * "subcategory":"",
 * "project":"",
 * "office":"",
 * "description":""}
 */

@Value
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerReceivable implements Comparable<CustomerReceivable> {
    double id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd")
    Date date;
    String currency;
    double amount;
    @JsonProperty("amount_usd")
    double amountUsd;
    @JsonProperty("amount_rub")
    double amountRub;
    String bank;
    @JsonProperty("bank_account")
    String bankAccount;
    @JsonProperty("legal_name")
    String legalName;
    String contractor;
    String type;
    @JsonProperty("contractor_account")
    String contractorAccount;
    @JsonProperty("contractor_legal_name")
    String contractorLegalName;
    String category;
    String subcategory;
    String project;
    String office;
    String description;

    @Override
    public String toString() {
        return String.format("%s — %s %s",
                contractor, formatDouble(amount), currency);
    }

    @Override
    public int compareTo(@NotNull CustomerReceivable o) {
        return AMOUNT_COMPARATOR
                .compare(this, o);
    }
}
