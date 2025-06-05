package com.whiskels.notifier.infrastructure.report.webhook;

import feign.Headers;
import feign.RequestLine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

public interface FeignWebhookSink {
    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    ResponseEntity<String> send(URI url, @RequestBody FeignWebhookSinkDto dto);
}
