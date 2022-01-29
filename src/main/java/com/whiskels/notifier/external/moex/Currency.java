package com.whiskels.notifier.external.moex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Currency {
    USD_RUB(78.19d), EUR_RUB(87.17d);

    private final Double defaultValue;
}
