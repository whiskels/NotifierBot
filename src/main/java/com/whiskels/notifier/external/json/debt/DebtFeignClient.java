package com.whiskels.notifier.external.json.debt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.whiskels.notifier.external.json.debt._DebtConfig.DEBT_URL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@FeignClient(name = "debtClient", url = "${" + DEBT_URL + "}")
@ConditionalOnProperty(DEBT_URL)
interface DebtFeignClient {
    @RequestMapping(method = GET)
    DebtData get();
}
