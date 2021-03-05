package com.stripeapi.stripe.dto;

import com.stripe.model.Card;

public class CardDTO {


    private final String id;
    private final String fingerprint;
    private final String last4;
    private final String brand;

    public CardDTO(Card card) {
        this.id = card.getId();
        this.fingerprint = card.getFingerprint();
        this.last4 = card.getLast4();
        this.brand = card.getBrand();
    }

    public String getId() {
        return id;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getLast4() {
        return last4;
    }

    public String getBrand() {
        return brand;
    }
}
