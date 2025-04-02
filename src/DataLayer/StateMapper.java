package DataLayer;

import Map.State;
import Map.StatesParser;
import Map.Point;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StateMapper {
    private final List<State> states;
    private static final DecimalFormat DF;

    static {
        // Устанавливаем DecimalFormat с локалью US (использует точку как разделитель)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DF = new DecimalFormat("#.######", symbols);
        DF.setRoundingMode(RoundingMode.HALF_UP);
    }

    public StateMapper(String statesJsonPath) throws IOException {
        this.states = StatesParser.parse(statesJsonPath);
    }

    /**
     * Преобразует Map<Координаты, Сентимент> в Map<Штат, Сентимент>
     */
    public Map<String, Double> mapToStates(Map<String, Double> coordinateSentiments) {
        Map<String, Double> stateSentimentSums = new HashMap<>();
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

            // Обновляем сумму и количество для штата
            stateSentimentSums.merge(stateCode, sentiment, Double::sum);
            stateCounts.merge(stateCode, 1, Integer::sum);
        }

        // Вычисляем средние значения
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : stateSentimentSums.entrySet()) {
            String state = entry.getKey();
            double sum = entry.getValue();
            int count = stateCounts.get(state);
            double average = sum / count;
            // Форматируем и парсим обратно, чтобы получить округленное значение
            result.put(state, Double.parseDouble(DF.format(average)));
        }

        return result;
    }

    private Point parseCoordinates(String coordinates) {
        try {
            String[] parts = coordinates.substring(1, coordinates.length()-1).split(", ");
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
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