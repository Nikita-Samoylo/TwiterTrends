package DataLayer;

import Map.State;
import Map.StatesParser;
import Map.Point;
import java.io.IOException;
import java.util.*;

public class StateMapper {
    private final List<State> states;

    public StateMapper(String statesJsonPath) throws IOException {
        this.states = StatesParser.parse(statesJsonPath);
    }

    /**
     * Преобразует Map<Координаты, Сентимент> в Map<Штат, Сентимент>
     */
    public Map<String, Double> mapToStates(Map<String, Double> coordinateSentiments) {
        Map<String, Double> stateSentiments = new HashMap<>();
        Map<String, Integer> stateCounts = new HashMap<>();

        for (Map.Entry<String, Double> entry : coordinateSentiments.entrySet()) {
            String coordinates = entry.getKey();
            Double sentiment = entry.getValue();

            // Пропускаем записи с null сентиментом
            if (sentiment == null) {
                continue;
            }

            // Парсим координаты
            Point point = parseCoordinates(coordinates);
            if (point == null) continue;

            // Находим штат
            String stateCode = findStateForPoint(point);
            if (stateCode == null) continue;

            // Обновляем сентимент для штата
            if (stateSentiments.containsKey(stateCode)) {
                // Вычисляем среднее значение, если штат уже есть в мапе
                double currentSentiment = stateSentiments.get(stateCode);
                int count = stateCounts.get(stateCode);
                double newAverage = (currentSentiment * count + sentiment) / (count + 1);
                stateSentiments.put(stateCode, newAverage);
                stateCounts.put(stateCode, count + 1);
            } else {
                // Добавляем новую запись
                stateSentiments.put(stateCode, sentiment);
                stateCounts.put(stateCode, 1);
            }
        }

        return stateSentiments;
    }

    private Point parseCoordinates(String coordinates) {
        try {
            String[] parts = coordinates.substring(1, coordinates.length()-1).split(", ");
            double longitude = Double.parseDouble(parts[1]);
            double latitude = Double.parseDouble(parts[0]);
            return new Point(longitude, latitude);
        } catch (Exception e) {
            return null;
        }
    }

    private String findStateForPoint(Point point) {
        for (State state : states) {
            if (state.contains(point)) {
                return state.getName();
            }
        }
        return null;
    }
}