package com.shadrin.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Присваивает по дефолту сумму пи создании счета и комиссию за перевод
 */
@Component
public class AccountProperties {

    private final double defaultAmount;

    private final double transferCommission;

    public AccountProperties(
            @Value("${account.default-amount:1000.0}") double defaultAmount,
            @Value("${account.transfer-commission:0.015}") double transferCommission) {
        this.defaultAmount = defaultAmount;
        this.transferCommission = transferCommission;
    }


    public double getDefaultAmount() {
        return defaultAmount;
    }

    public double getTransferCommission() {
        return transferCommission;
    }
}
