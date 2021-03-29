package com.whiskels.notifier.external.receivable.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.whiskels.notifier.AbstractBaseEntity;
import com.whiskels.notifier.telegram.repository.LocalDatePersistenceConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;

import static java.time.LocalDate.now;

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

@Entity
@Table(name = "receivable")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Receivable extends AbstractBaseEntity implements Comparable<Receivable> {
    @JsonProperty("id")
    int crmId;
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
    @Convert(converter = LocalDatePersistenceConverter.class)
    LocalDate loadDate = now(ZoneId.of("Europe/Moscow"));

    @Override
    public int compareTo(@NotNull Receivable o) {
        return Comparator.comparing(Receivable::getAmount)
                .thenComparing(Receivable::getContractor)
                .reversed()
                .compare(this, o);
    }
}
