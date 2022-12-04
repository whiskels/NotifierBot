package com.whiskels.notifier.external.json.currency;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


@FeignClient(
        name = "currencyRateClient",
        url = "${external.currency.url:https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/rub.json}"
)
interface CurrencyRateFeignClient {
    @RequestMapping(method = GET)
    CurrencyRate get();
}
