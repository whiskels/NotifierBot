package com.whiskels.notifier.infrastructure.report.webhook;

import feign.Feign;
import feign.Target;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(FeignClientsConfiguration.class)
class FeignWebhookSinkConfig {
    @Bean
    FeignWebhookSink feignWebhookSink(Encoder encoder, Decoder decoder) {
        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .target(Target.EmptyTarget.create(FeignWebhookSink.class));
    }
}
