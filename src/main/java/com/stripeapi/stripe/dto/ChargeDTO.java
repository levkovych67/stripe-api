package com.stripeapi.stripe.dto;

import com.stripe.model.Charge;

import java.sql.Timestamp;

public class ChargeDTO {

    private String id;
    private String status;
    private Long amount;
    private Timestamp createTs;

    public ChargeDTO(final Charge charge) {
        this.id = charge.getId();
        this.status = charge.getStatus();
        this.amount = charge.getAmount();
        this.createTs = new Timestamp(charge.getCreated());

    }

    public String getStatus() {
        return status;
    }

    public Long getAmount() {
        return amount;
    }

    public Timestamp getCreateTs() {
        return createTs;
    }

    public String getId() {
        return id;
    }
}
