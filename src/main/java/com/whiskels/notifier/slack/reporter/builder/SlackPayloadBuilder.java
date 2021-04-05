package com.whiskels.notifier.slack.reporter.builder;

import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.composition.TextObject;
import com.slack.api.webhook.Payload;
import com.whiskels.notifier.slack.SlackPayload;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_NEW_LINE;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SlackPayloadBuilder {
    private static final String REPORT_HEADER = "%s report on: %s";

    private List<LayoutBlock> blocks = new ArrayList<>();
    private String webhook;
    private String notificationMessage;
    private String noData = "Nothing";
    private int blockNum = 1;
    private Collector<CharSequence, ?, String> activeCollector = COLLECTOR_NEW_LINE;
    private boolean isHeaderAdded = false;

    public static SlackPayloadBuilder builder() {
        return new SlackPayloadBuilder();
    }

    public SlackPayloadBuilder hook(String webhook) {
        this.webhook = webhook;
        return this;
    }

    public SlackPayloadBuilder noData(String noData) {
        this.noData = noData;
        return this;
    }

    public SlackPayloadBuilder collector(Collector<CharSequence, ?, String> activeCollector) {
        this.activeCollector = activeCollector;
        return this;
    }

    public SlackPayloadBuilder notificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
        return this;
    }

    public SlackPayloadBuilder header(String name, LocalDate date) {
        return header(name, date, true);
    }

    public SlackPayloadBuilder header(String name, LocalDate date, boolean notificationMessage) {
        if (!isHeaderAdded) {
            String headerText = String.format(REPORT_HEADER, name, date);
            if (notificationMessage) {
                this.notificationMessage = headerText;
            }
            blocks.add(0, new HeaderBlock("0", new PlainTextObject(headerText, false)));
            isHeaderAdded = true;
        }
        return this;
    }

    public SlackPayloadBuilder notifyChannel() {
        return block("@channel");
    }

    public SlackPayloadBuilder block(String text) {
        MarkdownTextObject content = new MarkdownTextObject();
        content.setText(text);
        createSectionBlock(content);

        return this;
    }

    public <T> SlackPayloadBuilder block(List<T> data) {
        return block(data, activeCollector);
    }

    public <T> SlackPayloadBuilder block(List<T> data, Collector<CharSequence, ?, String> collector) {
        return block(formatList(data, collector));
    }

    public <T> SlackPayloadBuilder block(List<T> data, String accessory) {
        return block(data, accessory, activeCollector);
    }

    public <T> SlackPayloadBuilder block(List<T> data, String accessory, Collector<CharSequence, ?, String> collector) {
        MarkdownTextObject content = new MarkdownTextObject();
        content.setText(formatList(data, collector));

        createSectionBlock(content, new AccessoryBlock(accessory));
        return this;
    }

    private <T> String formatList(List<T> data, Collector<CharSequence, ?, String> collector) {
        return data.isEmpty() ? noData : data.stream()
                .map(T::toString)
                .collect(collector);
    }

    private void createSectionBlock(TextObject text) {
        blocks.add(new SectionBlock(text, blockNum(), null, null));
    }

    private void createSectionBlock(TextObject text, AccessoryBlock accessoryBlock) {
        blocks.add(new SectionBlock(text, blockNum(), null, accessoryBlock));
    }

    private String blockNum() {
        return String.valueOf(blockNum++);
    }

    public SlackPayload build() {
        return new SlackPayload(webhook, Payload.builder()
                .text(notificationMessage)
                .blocks(blocks)
                .build());
    }
}
