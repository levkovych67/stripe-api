package com.stripeapi.stripe.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerRetrieveParams;
import com.stripeapi.stripe.dto.CardDTO;
import com.stripeapi.stripe.dto.ChargeDTO;
import com.stripeapi.stripe.dto.CustomerDTO;
import com.stripeapi.stripe.dto.PaymentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.stripeapi.stripe.util.StripeParamUtil.*;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public CustomerDTO createCustomer(final String email) throws StripeException {
        if (customerExists(email)) {
            throw new RuntimeException("Customer with email" + email + "exists already");
        }
        return create(email);
    }

    private CustomerDTO create(final String email) throws StripeException {
        final CustomerCreateParams customerCreateParams = buildCustomerCreateParams(email);
        final Customer customer = Customer.create(customerCreateParams);
        return new CustomerDTO(customer);
    }

    private boolean customerExists(final String email) throws StripeException {
        CustomerListParams customerListParams = buildCustomerListByEmailParams(email);
        List<Customer> allCustomersByEmail = getAllCustomersByEmail(customerListParams);
        return allCustomersByEmail.size() > 0;
    }

    private List<Customer> getAllCustomersByEmail(final CustomerListParams allCustomersParams) throws StripeException {
        return Customer.list(allCustomersParams).getData();
    }

    public CardDTO addCardToCustomer(final String customerId, final Token token) throws StripeException {
        final Customer customer = getCustomerById(customerId);
        validateCard(customer, token.getCard());
        return create(token, customer);
    }

    private CardDTO create(final Token token, final Customer customer) throws StripeException {
        Map<String, Object> sourceUpdate = new HashMap<>();
        sourceUpdate.put("source", token.getId());
        Card paymentSource = (Card) customer.getSources().create(sourceUpdate);
        return new CardDTO(paymentSource);
    }

    private void validateCard(final Customer customer, final Card card) {
        checkIfCardIsDuplicate(customer, card);
    }

    private List<Card> getCustomerCards(final Customer customer) {
        final Map<String, Object> cardRetrieveParams = buildCardRetrieveParams();
        try {
            return (List<Card>) (List<?>) customer.getSources().list(cardRetrieveParams).getData();
        } catch (StripeException e) {
            throw new RuntimeException("Could not get cards for customer with email " + customer.getEmail());
        }
    }

    private void checkIfCardIsDuplicate(final Customer customer, final Card card) {
        final List<Card> customerCards = getCustomerCards(customer);
        if (customerCards.stream().anyMatch(e -> e.getFingerprint().equals(card.getFingerprint()))) {
            throw new RuntimeException("Card with fingerprint " + card.getFingerprint() + " already exists for " + customer.getEmail());
        }
    }

    private Customer getCustomerById(final String customerId) {
        try {
            final CustomerRetrieveParams customerRetrieveParams = buildCustomerRetrieveParams();
            return Customer.retrieve(customerId, customerRetrieveParams, null);
        } catch (StripeException e) {
            throw new RuntimeException("Stripe customer with id " + customerId + " not found");
        }
    }

    public void deleteCard(final String customerId, final String cardId) {
        final Customer customerById = getCustomerById(customerId);
        checkIfCustomerOwnsCard(customerById, cardId);
        try {
            Card retrieve = (Card) customerById.getSources().retrieve(cardId);
            retrieve.delete();
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    private void checkIfCustomerOwnsCard(final Customer customer, final String cardId) {
        final List<Card> customerCards = getCustomerCards(customer);
        if (customerCards.stream().noneMatch(e -> e.getId().equals(cardId))) {
            throw new RuntimeException("Delуting non existing card with id " + cardId + " for " + customer.getEmail());
        }
    }

    public List<CardDTO> listCards(final String customerId) {
        final Customer customerById = getCustomerById(customerId);
        final List<Card> customerCards = getCustomerCards(customerById);
        return customerCards.stream().map(CardDTO::new).collect(Collectors.toList());
    }

    public ChargeDTO chargeFromCustomerCard(final String customerId, final Long amount, final String cardId) throws StripeException {
        final Customer customerById = getCustomerById(customerId);
        checkIfCustomerOwnsCard(customerById, cardId);
        final ChargeCreateParams chargeCreateParams = buildChargeByParams(customerId, amount, cardId);
        final Charge charge = Charge.create(chargeCreateParams);
        return new ChargeDTO(charge);
    }

    public ChargeDTO chargeFromCustomer(final String customerId, final Long amount) throws StripeException {
        final Customer customerById = getCustomerById(customerId);
        final String defaultSource = customerById.getDefaultSource();
        final ChargeCreateParams chargeCreateParams = buildChargeByParams(customerId, amount, defaultSource);
        final Charge charge = Charge.create(chargeCreateParams);
        return new ChargeDTO(charge);
    }


    //TODO list all payments
    //TODO list all payments with filter
    public List<PaymentDTO> listPayments(final String customerId) throws StripeException {
        return null;
    }
}
