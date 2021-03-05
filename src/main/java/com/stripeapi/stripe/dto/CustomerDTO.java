package com.stripeapi.stripe.dto;

import com.stripe.model.Customer;

public class CustomerDTO {

    private String customerId;

    public CustomerDTO(Customer customer) {
        this.customerId = customer.getId();
    }

    public String getCustomerId() {
        return customerId;
    }
}
