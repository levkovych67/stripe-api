package com.stripeapi.stripe.dto;

import com.stripe.model.Charge;

public class ChargeDTO {

    private String status;
    private Long amount;

    public ChargeDTO(final Charge charge) {
        this.status = charge.getStatus();
        this.amount = charge.getAmount();
    }

    public String getStatus() {
        return status;
    }

    public Long getAmount() {
        return amount;
    }
}
