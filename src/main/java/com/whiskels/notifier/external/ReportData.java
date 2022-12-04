package com.whiskels.notifier.external;

import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static com.whiskels.notifier.common.util.StreamUtil.map;

@Value
public class ReportData<T> {
    List<T> content;
    LocalDate reportDate;
    
    public <R> ReportData<R> remap(Function<T, R> remapper) {
        return new ReportData<>(map(content, remapper), reportDate);
    }
}
