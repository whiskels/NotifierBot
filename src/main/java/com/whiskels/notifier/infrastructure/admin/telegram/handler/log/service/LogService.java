package com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;


@RequiredArgsConstructor
public class LogService {
    private final PapertrailClient papertrailClient;

    @SneakyThrows
    public byte[] getLogsAsByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        writer.write(String.join("\n", papertrailClient.getLogs()));
        writer.flush();
        return baos.toByteArray();
    }
}
