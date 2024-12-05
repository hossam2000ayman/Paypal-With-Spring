package com.example.paypalintegration.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PaypalService {
    final APIContext apiContext;

    public PaypalService(APIContext apiContext) {
        this.apiContext = apiContext;
    }

    //1- create the payment
    public Payment createPayment(Double total, String currency, String method, String intent, String description, String cancelUrl, String successUrl) {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        //what is the payment method that we want to do?
        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        //in case of success, cancelled, error
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);

        try {
            return payment.create(apiContext);
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }

        return payment;
    }

    //    2- we need to execute the payment
    public Payment executePayment(String paymentId, String payerId) {
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setState("approved");

        PaymentExecution execution = new PaymentExecution();
        execution.setPayerId(payerId);

        try {
            return payment.execute(apiContext, execution);
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return payment;
    }
}
