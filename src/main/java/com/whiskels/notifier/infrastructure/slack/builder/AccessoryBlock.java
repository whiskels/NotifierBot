package com.whiskels.notifier.infrastructure.slack.builder;

import com.slack.api.model.block.element.BlockElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccessoryBlock extends BlockElement {
    private final String type = "image";
    private final String image_url;
    private final String alt_text = "Funny pic";

    public AccessoryBlock(String image_url) {
        this.image_url = image_url;
    }
}
