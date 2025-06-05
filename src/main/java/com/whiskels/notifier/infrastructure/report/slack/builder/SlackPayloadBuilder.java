package com.whiskels.notifier.infrastructure.report.slack.builder;

import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.composition.TextObject;
import com.slack.api.webhook.Payload;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SlackPayloadBuilder {
    private final List<LayoutBlock> blocks = new ArrayList<>();
    private String notificationMessage;
    private int blockNum = 1;
    private boolean isHeaderAdded = false;

    public static SlackPayloadBuilder builder() {
        return new SlackPayloadBuilder();
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
            blocks.addFirst(new HeaderBlock("0", new PlainTextObject(header, false)));
            isHeaderAdded = true;
        }
        return notificationMessage ? notificationMessage(header) : this;
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

    public SlackPayloadBuilder block(String text, String picUrl) {
        MarkdownTextObject content = new MarkdownTextObject();
        content.setText(text);

        createSectionBlock(content, new AccessoryBlock(picUrl));
        return this;
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

    public Payload build() {
        return Payload.builder()
                .text(notificationMessage)
                .blocks(blocks)
                .build();
    }
}
