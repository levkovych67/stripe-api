package com.stripeapi.stripe.dto;

import com.stripe.model.ChargeCollection;

import java.util.List;
import java.util.stream.Collectors;

public class ChargeCollectionDTO {

    private List<ChargeDTO> charges;

    public ChargeCollectionDTO(final ChargeCollection charges) {
        this.charges = charges.getData().stream().map(ChargeDTO::new).collect(Collectors.toList());
    }

    public List<ChargeDTO> getCharges() {
        return charges;
    }
}
