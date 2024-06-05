package com.example.prometei.controllers;

import com.example.prometei.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Отменяет платеж по указанному хешу платежа.
     *
     * @param paymentHash хеш платежа, который необходимо отменить
     */
    @PatchMapping("/cancelPay")
    public void cancelPayment(@RequestParam String paymentHash) {
        paymentService.cancelPayment(paymentHash);
    }

    /**
     * Подтверждает платеж по указанному хешу платежа.
     *
     * @param paymentHash хеш платежа, который необходимо подтвердить
     * @return ответ с результатом подтверждения платежа
     */
    @PatchMapping("/confirmPay")
    public ResponseEntity<Boolean> confirmPayment(@RequestParam String paymentHash) {
        return new ResponseEntity<>(paymentService.payPayment(paymentHash), HttpStatus.OK);
    }
}
