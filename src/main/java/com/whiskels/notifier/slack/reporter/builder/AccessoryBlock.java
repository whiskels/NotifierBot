package com.whiskels.notifier.slack.reporter.builder;

import com.slack.api.model.block.element.BlockElement;

public class AccessoryBlock extends BlockElement {
    private String type = "image";
    private String image_url;
    private String alt_text = "Funny pic";

    public AccessoryBlock(String image_url) {
        this.image_url = image_url;
    }
}
