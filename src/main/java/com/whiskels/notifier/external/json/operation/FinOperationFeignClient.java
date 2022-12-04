package com.whiskels.notifier.external.json.operation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.whiskels.notifier.external.json.operation._FinOperationBeanConfig.FIN_OPERATION_URL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@FeignClient(name = "finOperationClient", configuration = FinOperationRequestInterceptorConfig.class, url = "https://")
@ConditionalOnProperty(FIN_OPERATION_URL)
interface FinOperationFeignClient {
    @RequestMapping(method = GET)
    List<FinancialOperation> get();
}
