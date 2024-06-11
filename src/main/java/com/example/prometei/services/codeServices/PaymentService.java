package com.example.prometei.services.codeServices;

import com.example.prometei.models.Flight;
import com.example.prometei.models.Payment;
import com.example.prometei.models.Purchase;
import com.example.prometei.models.Ticket;
import com.example.prometei.models.enums.PaymentState;
import com.example.prometei.repositories.PaymentRepository;
import com.example.prometei.services.baseServices.FlightService;
import com.example.prometei.services.baseServices.TicketService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TicketService ticketService;
    private final ScheduledExecutorService scheduler;
    private final FlightService flightService;
    private final Logger log = LoggerFactory.getLogger(PaymentService.class);
    @Value("${hash.salt}")
    private String salt;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, TicketService ticketService, FlightService flightService) {
        this.paymentRepository = paymentRepository;
        this.ticketService = ticketService;
        this.flightService = flightService;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @PostConstruct
    public void startScheduledTask() {
        scheduler.scheduleAtFixedRate(this::updatePayments, 0, 5, TimeUnit.MINUTES);
    }

    public void stopScheduledTask() {
        scheduler.shutdown();
    }

    private void updatePayments() {
        List<Payment> payments = paymentRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        payments.forEach(payment -> {
            if (payment.getDeadline().isBefore(now) && payment.getState() == PaymentState.PROCESSING) {
                cancelPayment(payment.getHash());
                log.info("Updated payment status to Cancel for payment id: {}", payment.getId());
            }
        });
    }

    /**
     * Генерирует уникальный хеш для платежа.
     *
     * @param payment объект платежа
     * @return уникальный хеш платежа
     */
    private String generateUniqueHash(Payment payment) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String text = payment.getId() + payment.getCreateDate().toString() + salt;
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            log.info("Hash was created.");
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating hash {}", e.getMessage());
            throw new RuntimeException("Error generating hash", e);
        }
    }

    /**
     * Создает новый платеж для покупки.
     *
     * @param purchase объект покупки
     * @throws NullPointerException если покупка равна null
     */
    @Transactional
    public void createPayment(Purchase purchase) {
        if (purchase == null) {
            log.error("Can't create payment. Purchase = null.");
            throw new NullPointerException();
        }

        LocalDateTime moment = LocalDateTime.now();

        Payment payment = Payment.builder()
                .state(PaymentState.PROCESSING)
                .method(purchase.getPaymentMethod())
                .purchase(purchase)
                .createDate(moment)
                .deadline(moment.plusMinutes(10))
                .build();

        paymentRepository.save(payment);

        payment.setHash(generateUniqueHash(payment));
        purchase.setPayment(payment);

        paymentRepository.save(payment);
        log.info("Payment with hash = {} was created", payment.getHash());
    }

    /**
     * Отменяет платеж по его уникальному хешу.
     *
     * @param paymentHash уникальный хеш платежа
     * @throws NullPointerException если платеж с указанным хешем не найден
     */
    public void cancelPayment(String paymentHash) {
        Payment payment = paymentRepository.findByHash(paymentHash);

        if (payment == null) {
            log.error("Can't cancel payment with hash = {}. Payment = null.", paymentHash);
            throw new NullPointerException();
        }

        Purchase purchase = payment.getPurchase();

        payment.setState(PaymentState.CANCELED);
        payment.setPaymentDate(LocalDateTime.now());
        ticketService.returnTickets(purchase.getTickets());

        List<Flight> flights = purchase.getTickets().stream().map(Ticket::getFlight).toList();

        for(Flight flight : flights) {
            flightService.updateSeatsCount(flight.getId());
        }

        paymentRepository.save(payment);
        log.info("Payment with hash = {} was canceled.", paymentHash);
    }

    /**
     * Осуществляет оплату платежа по его уникальному хешу.
     *
     * @param paymentHash уникальный хеш платежа
     * @return true, если платеж был успешно оплачен, иначе - false
     * @throws NullPointerException если платеж с указанным хешем не найден
     */
    @Transactional
    public Boolean payPayment(String paymentHash) {
        LocalDateTime moment = LocalDateTime.now();
        Payment payment = paymentRepository.findByHash(paymentHash);

        if (payment == null) {
            log.error("Can't pay payment with hash = {}. Payment = null.", paymentHash);
            throw new NullPointerException();
        }

        if (payment.getDeadline().isAfter(moment)) {
            payment.setState(PaymentState.PAID);
            payment.setPaymentDate(LocalDateTime.now());

            paymentRepository.save(payment);
            log.info("Purchase with id = {} was paid.", payment.getPurchase().getId());
            return true;
        } else {
            cancelPayment(paymentHash);
            log.error("Time is up. You can't paid.");
            return false;
        }
    }
}
