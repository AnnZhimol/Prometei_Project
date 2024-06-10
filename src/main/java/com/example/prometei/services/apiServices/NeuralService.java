package com.example.prometei.services.apiServices;

import com.example.prometei.api.NeuralApi;
import com.example.prometei.api.enums.MoodType;
import com.example.prometei.api.response.PlacesByNeural;
import com.example.prometei.api.request.ClassificationParams;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NeuralService {
    private final NeuralApi neuralApi;
    private final Logger log = LoggerFactory.getLogger(NeuralService.class);

    public NeuralService(NeuralApi neuralApi) {
        this.neuralApi = neuralApi;
    }

    /**
     * Возвращает классификацию запроса на основе параметров классификации.
     *
     * @param classificationParams Параметры классификации, содержащие тип настроения, тип модели, email и сам вопрос.
     * @return Классификация вопроса, полученная от нейронной сети.
     */
    public String getClassification(ClassificationParams classificationParams) {
        log.info("Message from neural network received.");
        return neuralApi.getClassOfQuestion(classificationParams.getMoodType(), classificationParams.getModelType(), classificationParams.getEmail(), classificationParams.getQuestion()).getAnswer();
    }

    /**
     * Возвращает список топовых мест на основе настроения и рубрики.
     *
     * @param mood Тип настроения пользователя.
     * @param rubric Рубрика для поиска мест.
     * @return Список объектов PlacesByNeural, представляющих топовые места.
     */
    public List<PlacesByNeural> getTopPlaces(@NotNull MoodType mood,
                                             @NotNull String rubric) {
        log.info("Message with places from neural network received.");
        return neuralApi.getTopPlaces(mood, rubric);
    }

    /**
     * Возвращает список негативных классов от нейронной сети.
     *
     * @return Список строк, представляющих негативные классы.
     */
    public List<String> getNegative() {
        log.info("Message with class of negative from neural network received.");
        return neuralApi.getNegative();
    }

    /**
     * Возвращает список позитивных классов от нейронной сети.
     *
     * @return Список строк, представляющих позитивные классы.
     */
    public List<String> getPositive() {
        log.info("Message with class of positive from neural network received.");
        return neuralApi.getPositive();
    }
}
