package com.whiskels.notifier.reporting.service.customer.payment.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.whiskels.notifier.reporting.domain.AbstractTimeStampedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;

import static java.lang.String.format;

@Entity
@Table(name = "financial_operation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialOperation extends AbstractTimeStampedEntity implements Comparable<FinancialOperation> {
    @JsonProperty("id")
    private Integer crmId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String currency;
    @JsonSetter(nulls = Nulls.SKIP)
    private BigDecimal amount = BigDecimal.ZERO;
    @JsonProperty("amount_usd")
    @JsonSetter(nulls = Nulls.SKIP)
    private BigDecimal amountUsd = BigDecimal.ZERO;
    @JsonProperty("amount_rub")
    @JsonSetter(nulls = Nulls.SKIP)
    private BigDecimal amountRub = BigDecimal.ZERO;
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
    public int compareTo(@Nonnull FinancialOperation o) {
        return Comparator.comparing(FinancialOperation::getAmountRub)
                .thenComparing(FinancialOperation::getContractor)
                .reversed()
                .compare(this, o);
    }

    @Override
    public String toString() {
        return super.toString() + format(" `%s %s %s`", crmId, category, contractor);
    }
}
