package com.shadrin.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Присваивает по дефолту сумму пи создании счета и комиссию за перевод
 */
@Component
public class AccountProperties {

    private final BigDecimal defaultAmount;

    private final BigDecimal transferCommission;

    public AccountProperties(
            @Value("${account.default-amount:1000.0}") BigDecimal defaultAmount,
            @Value("${account.transfer-commission:0.015}") BigDecimal transferCommission) {
        this.defaultAmount = defaultAmount;
        this.transferCommission = transferCommission;
    }


    public BigDecimal getDefaultAmount() {
        return defaultAmount;
    }

    public BigDecimal getTransferCommission() {
        return transferCommission;
    }
}
