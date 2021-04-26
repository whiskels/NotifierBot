package com.whiskels.notifier.external.moex;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Service
@Slf4j
@ConfigurationProperties("moex")
@Setter
public class MoexService {
    private String url;
    private String usd;
    private String eur;

    @Getter
    private double usdRate;
    @Getter
    private double eurRate;

    @PostConstruct
    @Scheduled(cron = "${moex.cron:0 0 0 * * *}", zone = "${common.timezone}")
    public void update() {
        log.info("Updating exchange rates");
        // Getting moex exchange rates string
        try {
            String moexContent = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
            // Removing HTML tags
            moexContent = moexContent.replaceAll("\\<.*?\\>", "");

            // Converting data to string array
            String[] moexArray = moexContent.split("\n");

            // Mapping data
            final int moexDataLength = (moexArray.length - 1) / 2;
            HashMap<String, String> ratesMap = new HashMap<>(moexDataLength);
            for (int i = 0; i < moexDataLength; i++) {
                ratesMap.put(moexArray[i + 1], moexArray[i + 1 + moexDataLength]);
            }

            if (!ratesMap.isEmpty()) {
                usdRate = Double.parseDouble(ratesMap.get(usd));
                eurRate = Double.parseDouble(ratesMap.get(eur));
            }
        } catch (IOException e) {
            log.error("Exception while trying to get MOEX data: {}", e.toString());
        } catch (NumberFormatException e) {
            log.error("Exception while trying to update exchange rate: {}", e.toString());
        }
    }
}
