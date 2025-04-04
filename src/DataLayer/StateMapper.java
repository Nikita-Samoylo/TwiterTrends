package DataLayer;

import Map.State;
import Map.Point;
import java.math.RoundingMode;
import java.util.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StateMapper {
    private final List<State> states;
    private static final DecimalFormat DF;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DF = new DecimalFormat("#.######", symbols);
        DF.setRoundingMode(RoundingMode.HALF_UP);
    }

    public StateMapper(List<State> states) {
        this.states = Objects.requireNonNull(states, "Список штатов не может быть null");
    }

    // преобразование словаря (координаты + значение) в словарь (штаты + значение)
    public Map<String, Double> mapToStates(Map<String, Double> coordinateSentiments) {
        Map<String, Double> stateSentimentSums = new HashMap<>();
        Map<String, Integer> stateCounts = new HashMap<>();

        for (Map.Entry<String, Double> entry : coordinateSentiments.entrySet()) {
            String coordinates = entry.getKey();
            Double sentiment = entry.getValue();
            if (sentiment == null) {
                continue;
            }

            Point point = parseCoordinates(coordinates);
            if (point == null) continue;

            String stateCode = findStateForPoint(point);
            if (stateCode == null) continue;

            stateSentimentSums.merge(stateCode, sentiment, Double::sum);
            stateCounts.merge(stateCode, 1, Integer::sum);
        }

        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : stateSentimentSums.entrySet()) {
            // подчет среднего сентимента
            String state = entry.getKey();
            double sum = entry.getValue();
            int count = stateCounts.get(state);
            double average = sum / count;
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