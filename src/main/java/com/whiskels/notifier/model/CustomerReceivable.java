package com.whiskels.notifier.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
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

@Data
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerReceivable implements Comparable<CustomerReceivable> {
    public static final String CATEGORY_REVENUE = "Revenue";

    private double id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd")
    private Date date;
    private String currency;
    private double amount;
    @JsonProperty("amount_usd")
    private double amountUsd;
    @JsonProperty("amount_rub")
    private double amountRub;
    private String bank;
    @JsonProperty("bank_account")
    private String bankAccount;
    @JsonProperty("legal_name")
    private String legalName;
    private String contractor;
    private String type;
    @JsonProperty("contractor_account")
    private String contractorAccount;
    @JsonProperty("contractor_legal_name")
    private String contractorLegalName;
    private String category;
    private String subcategory;
    private String project;
    private String office;
    private String description;

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
