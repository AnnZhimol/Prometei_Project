package com.example.prometei.services.codeServices;

import com.example.prometei.models.ConfirmationCode;
import com.example.prometei.models.User;
import com.example.prometei.models.enums.CodeState;
import com.example.prometei.repositories.ConfirmationCodeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ConfirmationCodeService {
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final Logger log = LoggerFactory.getLogger(ConfirmationCodeService.class);
    private final Random random;

    @Autowired
    public ConfirmationCodeService(ConfirmationCodeRepository confirmationCodeRepository) {
        this.confirmationCodeRepository = confirmationCodeRepository;
        this.random = new Random();
    }

    /**
     * Генерирует уникальный хеш для кода подтверждения.
     *
     * @return уникальный хеш кода подтверждения
     */
    private String generateUniqueHash() {
        int length = 6;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        String hash = sb.toString();
        log.info("Random 6-digit code was created: " + hash);
        return hash;
    }

    /**
     * Создает новый код для обновления данных.
     *
     * @param user объект пользователя
     * @throws NullPointerException если покупка равна null
     */
    @Transactional
    public void createConfirmationCode(User user) {
        if (user == null) {
            log.error("Can't create confirmationCode. User = null.");
            throw new NullPointerException();
        }

        LocalDateTime moment = LocalDateTime.now();

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .state(CodeState.ACTIVE)
                .user(user)
                .createDate(moment)
                .deadline(moment.plusMinutes(3))
                .build();

        confirmationCodeRepository.save(confirmationCode);

        confirmationCode.setHash(generateUniqueHash());
        user.setConfirmationCode(confirmationCode);

        confirmationCodeRepository.save(confirmationCode);
        log.info("ConfirmationCode with hash = {} was created", confirmationCode.getHash());
    }

    /**
     * Деактивирует код по его уникальному хешу.
     *
     * @param confirmationCodeHash уникальный хеш кода
     * @throws NullPointerException если код с указанным хешем не найден
     */
    public void expiredConfirmationCode(String confirmationCodeHash) {
        ConfirmationCode confirmationCode = confirmationCodeRepository.findByHash(confirmationCodeHash);

        if (confirmationCode == null) {
            log.error("Can't deactivate confirmationCode with hash = {}. СonfirmationCode = null.", confirmationCodeHash);
            throw new NullPointerException();
        }

        User user = confirmationCode.getUser();

        confirmationCode.setState(CodeState.EXPIRED);
        user.setConfirmationCode(null);

        confirmationCodeRepository.save(confirmationCode);
        log.info("ConfirmationCode with hash = {} was expired.", confirmationCodeHash);
    }
}
