package com.stripeapi.stripe.util;

import com.stripe.exception.StripeException;
import com.stripe.model.Token;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerRetrieveParams;

import java.util.HashMap;
import java.util.Map;

public class StripeParamUtil {

    public static CustomerRetrieveParams buildCustomerRetrieveParams() {
        return CustomerRetrieveParams.builder().addExpand("sources").build();
    }

    public static CustomerListParams buildCustomerListByEmailParams(final String email) {
        return CustomerListParams.builder()
                .putExtraParam("email", email)
                .addExpand("data.sources")
                .putExtraParam("limit", 1).build();
    }

    public static CustomerCreateParams buildCustomerCreateParams(final String email) {
        return CustomerCreateParams.builder()
                .setEmail(email)
                .setDescription("Custom descriptor")
                .addExpand("sources")
                .build();
    }

    public static Token getTestTokenVisa() throws StripeException {
        final Map<String, Object> map = new HashMap<>();
        map.put("number", "4242424242424222");
        map.put("exp_month", "10");
        map.put("exp_year", "2024");
        map.put("cvc", "170");
        Map<String, Object> cardMap = new HashMap<>();
        cardMap.put("card", map);
        try {
            return Token.create(cardMap);
        } catch (StripeException e) {
            System.out.println(e);
            throw e;
        }
    }

    public static Token getTestTokenMaster() throws StripeException {
        final Map<String, Object> map = new HashMap<>();
        map.put("number", "4000056655665556");
        map.put("exp_month", "10");
        map.put("exp_year", "2024");
        map.put("cvc", "170");
        Map<String, Object> cardMap = new HashMap<>();
        cardMap.put("card", map);
        return Token.create(cardMap);
    }

    public static ChargeCreateParams buildChargeByParams(final String customerId, final long cent, final String card) {
        return ChargeCreateParams.builder()
                .setAmount(cent)
                .setCurrency("USD")
                .setDescription("Customer charge")
                .setStatementDescriptor("Custom descriptor")
                .setCustomer(customerId)
                .setSource(card)
                .build();
    }

    public static Map<String, Object> buildCardRetrieveParams() {
        final Map<String, Object> params = new HashMap<>();
        params.put("object", "card");
        params.put("limit", 10);
        return params;
    }

    public static Map<String, Object> buildChargeListRetrieveParams(final String customerId, final int limit) {
        final Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);
        params.put("customer", customerId);
        return params;
    }

    public static Map<String, Object> buildChargeListRetrieveParamsWithPeriod(final String customerId, final int limit, final Long start, final Long end) {
        final Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);
        params.put("starting_after", start);
        params.put("ending_before", end);
        params.put("customer", customerId);
        return params;
    }


}
