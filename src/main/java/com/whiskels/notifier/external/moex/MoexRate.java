package com.whiskels.notifier.external.moex;

import lombok.Value;

@Value
public class MoexRate {
    Currency currency;
    double rate;
}
