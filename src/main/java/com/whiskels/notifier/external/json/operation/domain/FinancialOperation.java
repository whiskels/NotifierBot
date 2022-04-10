package com.whiskels.notifier.external.json.operation.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.whiskels.notifier.common.audit.domain.AbstractAuditedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Comparator;

import static java.lang.String.format;

@Entity
@Table(name = "financial_operation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialOperation extends AbstractAuditedEntity implements Comparable<FinancialOperation> {
    @JsonProperty("id")
    Integer crmId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate date;
    String currency;
    Double amount;
    @JsonProperty("amount_usd")
    Double amountUsd;
    @JsonProperty("amount_rub")
    Double amountRub;
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
    public int compareTo(@NotNull FinancialOperation o) {
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
