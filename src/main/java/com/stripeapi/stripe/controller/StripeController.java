package com.stripeapi.stripe.controller;


import com.stripe.exception.StripeException;
import com.stripeapi.stripe.dto.CardDTO;
import com.stripeapi.stripe.dto.ChargeCollectionDTO;
import com.stripeapi.stripe.dto.CustomerDTO;
import com.stripeapi.stripe.service.StripeService;
import com.stripeapi.stripe.util.StripeParamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
public class StripeController {

    @Autowired
    private StripeService stripeService;

    private CustomerDTO customerDTO = null;


    //CREATES CUSTOMER
    @PostMapping("/customer")
    private void createCustomer() throws StripeException {
        CustomerDTO customer = stripeService.createCustomer(new Random().nextInt(3) + "@gmail.com");
        customerDTO = customer;
        System.out.println(customer.getCustomerId());
    }

    //CREATES 2 CARDS FOR CUSTOMER
    @PostMapping("/card")
    private void createCard() throws StripeException {
        CardDTO cardDTO = stripeService.addCardToCustomer(customerDTO.getCustomerId(), StripeParamUtil.getTestTokenVisa());
        CardDTO cardDTO1 = stripeService.addCardToCustomer(customerDTO.getCustomerId(), StripeParamUtil.getTestTokenMaster());
        System.out.println(cardDTO.getId());
        System.out.println(cardDTO1.getId());
    }

    //DELETES CUSTOMER CARD
    @DeleteMapping("/card/{cardId}/{customerId}")
    private void deleteCard(@PathVariable String customerId, @PathVariable String cardId) throws StripeException {
        stripeService.deleteCard(customerId, cardId);
    }

    //LISTS CARDS
    @GetMapping("/cards")
    private void listCards() {
        List<CardDTO> cards = stripeService.listCards(customerDTO.getCustomerId());
        cards.forEach(e -> System.out.println(e.getId()));
    }

    //CHARGES FROM CARD
    @PostMapping("/charge/{amount}/{customerId}/{card}")
    private void charge(@PathVariable Long amount, @PathVariable String customerId, @PathVariable String card) throws StripeException {
        stripeService.chargeFromCustomerCard(customerId, amount, card);
    }

    //CHARGES 555 FROM CUSTOMER DEFAULT
    @PostMapping("/charge/{customerId}")
    private void charge(@PathVariable String customerId) throws StripeException {
        stripeService.chargeFromCustomer(customerId, 555L);
    }

    //LIST OF ALL PAYMENTS
    //LIST OF ALL PAYMENTS WITH FILTER
    @GetMapping("/payments/{customerId}/{limit}")
    private void getPayments(@PathVariable String customerId, @PathVariable int limit) throws StripeException {
        ChargeCollectionDTO chargeCollectionDTO = stripeService.listPayments(customerId, limit);
        System.out.println(chargeCollectionDTO);
    }

    @GetMapping("/payments/{customerId}/{start}/{end}/{limit}")
    private void getPayments(@PathVariable String customerId, @PathVariable Long start, @PathVariable Long end, @PathVariable int limit) throws StripeException {
        ChargeCollectionDTO chargeCollectionDTO = stripeService.listPayments(customerId, limit, start, end);
        System.out.println(chargeCollectionDTO);
    }
}
