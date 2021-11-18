package com.whiskels.notifier.external.moex;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.telegram.TelegramLabeled;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.whiskels.notifier.external.moex.Currency.EUR_RUB;
import static com.whiskels.notifier.external.moex.Currency.USD_RUB;

@Component
@Slf4j
@ConfigurationProperties("moex")
@Setter
@RequiredArgsConstructor
public class MoexLoader implements Loader<MoexRate>, TelegramLabeled {
    private String url;
    private String usd;
    private String eur;

    @Override
    @Retryable
    @PostConstruct
    public List<MoexRate> load() {
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
                return List.of(new MoexRate(USD_RUB, Double.parseDouble(ratesMap.get(usd))),
                        new MoexRate(EUR_RUB, Double.parseDouble(ratesMap.get(eur))));
            }
        } catch (IOException e) {
            log.error("Exception while trying to get MOEX data: {}", e.toString());
        } catch (NumberFormatException e) {
            log.error("Exception while trying to update exchange rate: {}", e.toString());
        }
        return Collections.emptyList();
    }

    @Override
    public String getLabel() {
        return "MOEX data";
    }
}
