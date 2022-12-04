package com.whiskels.notifier.external.json.currency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static java.math.BigDecimal.ONE;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRate {
    private LocalDate date;
    @JsonProperty("rub")
    private Map<String, BigDecimal> rubRates;

    public BigDecimal getRate(String currency) {
        return Optional.ofNullable(currency)
                .map(cur -> rubRates.get(cur.toLowerCase()))
                .orElse(ONE);
    }
}
