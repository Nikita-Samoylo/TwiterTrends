package Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StatesParser {
    public static List<State> parse(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filename));
        List<State> states = new ArrayList<>();

        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String stateName = field.getKey(); // Получаем название штата
            List<Polygon> polygons = parsePolygons(field.getValue());
            states.add(new State(stateName, polygons));

            // Выводим название штата в консоль
            System.out.println("Парсинг штата: " + stateName);
            System.out.println("Количество полигонов: " + polygons.size());
            for (Polygon polygon : polygons) {
                System.out.println("Количество точек в полигоне: " + polygon.getPoints().size());
            }
        }

        return states;
    }

    private static List<Polygon> parsePolygons(JsonNode node) {
        List<Polygon> polygons = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode polygonNode : node) {
                if (polygonNode.isArray()) {
                    // Если это массив, проверяем, содержит ли он точки или вложенные полигоны
                    if (isPointArray(polygonNode)) {
                        // Если это массив точек, создаем полигон
                        List<Point> points = parsePoints(polygonNode);
                        if (!points.isEmpty()) {
                            polygons.add(new Polygon(points));
                            System.out.println("Добавлен полигон с " + points.size() + " точками");
                        }
                    } else {
                        // Если это вложенный массив, рекурсивно обрабатываем его
                        List<Polygon> nestedPolygons = parsePolygons(polygonNode);
                        polygons.addAll(nestedPolygons);
                        System.out.println("Добавлено " + nestedPolygons.size() + " вложенных полигонов");
                    }
                }
            }
        }
        return polygons;
    }

    private static boolean isPointArray(JsonNode node) {
        if (node.isArray() && node.size() > 0) {
            JsonNode firstElement = node.get(0);
            // Проверяем, является ли первый элемент массивом из двух чисел
            return firstElement.isArray() && firstElement.size() == 2 && firstElement.get(0).isNumber() && firstElement.get(1).isNumber();
        }
        return false;
    }

    private static List<Point> parsePoints(JsonNode node) {
        List<Point> points = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode pointNode : node) {
                if (pointNode.isArray() && pointNode.size() == 2) {
                    double x = pointNode.get(0).asDouble();
                    double y = pointNode.get(1).asDouble();
                    points.add(new Point(x, y));
                }
            }
        }
        return points;
    }
}