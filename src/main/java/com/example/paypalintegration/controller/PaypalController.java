package com.example.paypalintegration.controller;

import com.example.paypalintegration.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

//because we will use thymeleaf
@Controller
@Slf4j
public class PaypalController {
    final PaypalService paypalService;

    public PaypalController(PaypalService paypalService) {
        this.paypalService = paypalService;
    }


    @GetMapping("/")
    public String home() {
//        it returns the index page .html
        return "index";
    }

    @PostMapping("/payment/create")
    public RedirectView createPayment(@RequestParam("method") String method, @RequestParam("amount") String amount, @RequestParam("currency") String currency, @RequestParam(value = "description", required = false) String description) {
        String cancelUrl = "http://localhost:8080/payment/cancel";
        String successUrl = "http://localhost:8080/payment/success";
        Payment payment = paypalService.createPayment(Double.valueOf(amount), currency != null ? currency : "USD", method, "sale", description != null ? description : "", cancelUrl, successUrl);
        for (Links links : payment.getLinks()) {
            //self ,approval_url , execute
            if (links.getRel().equals("approval_url")) {
                return new RedirectView(links.getHref());
            }
        }
        return new RedirectView("/payment/error");
    }

    @GetMapping("payment/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        Payment payment = paypalService.executePayment(paymentId, payerId);
        if (payment.getState().equals("approved")) {
            return "paymentSuccess";//page on html
        }
        return "paymentError";
    }

    @GetMapping("payment/cancel")
    public String paymentCancel() {
        return "paymentCancel";//page on html
    }

    @GetMapping("payment/error")
    public String paymentError() {
        return "paymentError";//page on html
    }

}
