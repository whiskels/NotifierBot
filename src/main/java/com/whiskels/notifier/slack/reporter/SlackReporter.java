package com.whiskels.notifier.slack.reporter;

import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.webhook.Payload;
import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackPayloadCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_NEW_LINE;
import static java.time.LocalDate.now;

@Slf4j
@AllArgsConstructor
public abstract class SlackReporter<T> {
    protected static final String REPORT_HEADER = "%s report on: %s";
    protected static final String NO_DATA = "Nothing";
    protected static final String SAD_PIC_URL = "https://i.imgur.com/Os451Qo.jpeg";

    protected final String webHook;
    protected final DataProvider<T> provider;
    protected final Clock clock;
    protected final ApplicationEventPublisher publisher;

    @PostConstruct
    private void logReporter() {
        log.info("Slack reporter active: {}", this.getClass().getSimpleName());
    }

    protected abstract void report();

    protected Payload payload(String name, Collector<CharSequence, ?, String> collector) {
        return Payload.builder()
                .text(String.format(REPORT_HEADER, name, now(clock)))
                .blocks(List.of(headerBlock(name), contentBlock(collector)))
                .build();
    }

    protected final void publish(SlackPayload payload) {
        this.publisher.publishEvent(new SlackPayloadCreatedEvent(payload));
    }

    protected final LayoutBlock headerBlock(String name) {
        String headerText = String.format(REPORT_HEADER, name, now(clock));
        return new HeaderBlock("0", new PlainTextObject(headerText, false));
    }

    protected final LayoutBlock contentBlock() {
        return contentBlock(COLLECTOR_NEW_LINE);
    }

    protected final LayoutBlock contentBlock(Collector<CharSequence, ?, String> textCollector) {
        return contentBlock(textCollector, null, null);
    }

    protected final LayoutBlock contentBlock(Collector<CharSequence, ?, String> textCollector,
                                             Function<List<T>, Double> condition,
                                             Map<Double, String> accessories) {
        List<T> data = provider.get();

        MarkdownTextObject content = new MarkdownTextObject();
        BlockElement accessory = null;

        if (data.isEmpty()) {
            content.setText(NO_DATA);
            accessory = new AccessoryBlock(SAD_PIC_URL);
        } else {
            content.setText(
                    data.stream()
                            .map(T::toString)
                            .collect(textCollector));
        }

        if (accessories != null && !accessories.isEmpty()) {
            final double conditionResult = condition.apply(data);
            String matchingAccessory = accessories.entrySet().stream()
                    .min(Comparator.comparingDouble(entry -> Math.abs(entry.getKey() - conditionResult)))
                    .orElseThrow(() ->
                            new IllegalArgumentException("Exception while trying to find match for condition " + condition))
                    .getValue();
            accessory = new AccessoryBlock(matchingAccessory);
        }

        return new SectionBlock(content, "1", null, accessory);
    }
}