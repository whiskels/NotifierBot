package com.whiskels.notifier.external.moex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Currency {
    USD_RUB(70.19d), EUR_RUB(71.17d);

    private final Double defaultValue;
}
