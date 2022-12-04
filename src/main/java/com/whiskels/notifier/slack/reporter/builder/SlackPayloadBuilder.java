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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_NEW_LINE;
import static com.whiskels.notifier.common.util.StreamUtil.collectToString;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SlackPayloadBuilder {
    private final List<LayoutBlock> blocks = new ArrayList<>();
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

    public SlackPayloadBuilder header(String header) {
        return header(header, true);
    }

    public SlackPayloadBuilder header(String header, boolean notificationMessage) {
        if (!isHeaderAdded) {
            blocks.add(0, new HeaderBlock("0", new PlainTextObject(header, false)));
            isHeaderAdded = true;
        }
        return notificationMessage ? notificationMessage(header) : this;
    }

    public SlackPayloadBuilder notifyChannel() {
        return block("@channel");
    }

    public SlackPayloadBuilder block(String text) {
        MarkdownTextObject content = new MarkdownTextObject();
        if (text == null || text.isEmpty()) {
            content.setText(noData);
        } else {
            content.setText(text);
        }
        createSectionBlock(content);

        return this;
    }

    public <T> SlackPayloadBuilder block(Collection<T> data) {
        return block(data, T::toString);
    }

    public <T> SlackPayloadBuilder block(Collection<T> data, Function<T, String> toString) {
        return block(formatList(data, toString));
    }

    public <T> SlackPayloadBuilder block(Collection<T> data, String accessory) {
        MarkdownTextObject content = new MarkdownTextObject();
        content.setText(formatList(data, T::toString));

        createSectionBlock(content, new AccessoryBlock(accessory));
        return this;
    }

    public <T> SlackPayloadBuilder columnBlock(String leftBlock, String rightBlock) {
        TextObject left = MarkdownTextObject.builder().text(leftBlock).build();
        TextObject right = MarkdownTextObject.builder().text(rightBlock).build();
        blocks.add(new SectionBlock(null, blockNum(), List.of(left, right), null));
        return this;
    }

    public <T> SlackPayloadBuilder columnBlocks(List<T> data, Function<T, String> leftBlock, Function<T, String> rightBlock) {
        data.forEach(o -> {
            MarkdownTextObject left = new MarkdownTextObject();
            left.setText(leftBlock.apply(o));
            MarkdownTextObject right = new MarkdownTextObject();
            right.setText(leftBlock.apply(o));
            blocks.add(new SectionBlock(null, blockNum(), List.of(left, right), null));
        });
        return this;
    }

    private <T> String formatList(Collection<T> data, Function<T, String> toString) {
        return data.isEmpty() ? noData : collectToString(data, toString, activeCollector);
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
