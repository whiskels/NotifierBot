package com.whiskels.notifier.reporting.service.customer.debt.fetch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@EqualsAndHashCode
public class CustomerDebtData {
    private List<CustomerDebt> content;
}
