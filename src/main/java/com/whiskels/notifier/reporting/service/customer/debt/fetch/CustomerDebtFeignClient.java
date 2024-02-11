package com.whiskels.notifier.reporting.service.customer.debt.fetch;

import com.whiskels.notifier.infrastructure.config.feign.FeignProxyConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.whiskels.notifier.reporting.service.customer.debt.fetch.CustomerDebtFeignClient.DEBT_URL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@ConditionalOnProperty(DEBT_URL)
@FeignClient(name = "debtClient", url = "${" + DEBT_URL + "}", configuration = FeignProxyConfig.class)
public interface CustomerDebtFeignClient {
    String DEBT_URL = "report.parameters.customer-debt.url";

    @RequestMapping(method = GET)
    CustomerDebtData get();
}
