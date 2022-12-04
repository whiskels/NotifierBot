package com.whiskels.notifier.external.json.debt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@EqualsAndHashCode
class DebtData {
    private List<Debt> content;
}
