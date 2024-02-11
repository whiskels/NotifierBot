package com.whiskels.notifier.reporting.service.customer.debt.fetch;

import com.whiskels.notifier.reporting.service.customer.debt.domain.CurrencyRate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


@FeignClient(
        name = "currencyRateClient",
        url = "${report.parameters.currency-rate.url:https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/rub.json}"
)
@ConditionalOnProperty(CustomerDebtFeignClient.DEBT_URL)
public
interface CurrencyRateFeignClient {
    @RequestMapping(method = GET)
    CurrencyRate get();
}
