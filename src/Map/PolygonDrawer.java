package Map;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolygonDrawer extends Pane {
    private final List<State> states;
    private final Map<String, Double> stateSentiments;
    private double minX = Double.MAX_VALUE;
    private double maxX = Double.MIN_VALUE;
    private double minY = Double.MAX_VALUE;
    private double maxY = Double.MIN_VALUE;
    private Canvas canvas;

    public PolygonDrawer(List<State> states, Map<String, Double> stateSentiments) {
        this.states = states;
        this.stateSentiments = stateSentiments != null ? stateSentiments : new HashMap<>();
        calculateBounds();
        initializeCanvas();

        // Установка белого фона для всего Pane
        this.setStyle("-fx-background-color: white; -fx-padding: 5 0 5 0; -fx-alignment: center-right;");
    }

    private void calculateBounds() {
        for (State state : states) {
            for (Polygon polygon : state.getPolygons()) {
                for (Point point : polygon.getPoints()) {
                    minX = Math.min(minX, point.getX());
                    maxX = Math.max(maxX, point.getX());
                    minY = Math.min(minY, point.getY());
                    maxY = Math.max(maxY, point.getY());
                }
            }
        }

        double paddingX = (maxX - minX) * 0.005;
        double paddingY = (maxY - minY) * 0.005;
        minX -= paddingX;
        maxX += paddingX;
        minY -= paddingY;
        maxY += paddingY;
    }

    private void initializeCanvas() {
        canvas = new Canvas(1600, 1000);
        getChildren().add(canvas);

        canvas.widthProperty().bind(widthProperty().subtract(10).multiply(1.4));
        canvas.heightProperty().bind(heightProperty().subtract(10).multiply(1.4));

        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());

        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Заливка всего canvas белым цветом
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        double scale = Math.min(
                width / (maxX - minX),
                height / (maxY - minY)
        ) * 1.6;

        double offsetX = (width - (maxX - minX) * scale) * 0.05;
        double offsetY = (height - (maxY - minY) * scale) / 1.5;

        // Более темные границы для лучшей видимости на белом фоне
        gc.setStroke(Color.rgb(70, 70, 70));
        gc.setLineWidth(1.2);

        for (State state : states) {
            Color fillColor = getStateColor(state.getName());

            for (Polygon polygon : state.getPolygons()) {
                List<Point> points = polygon.getPoints();
                if (points.size() < 2) continue;

                double[] xPoints = new double[points.size()];
                double[] yPoints = new double[points.size()];

                for (int i = 0; i < points.size(); i++) {
                    xPoints[i] = (points.get(i).getX() - minX) * scale + offsetX;
                    yPoints[i] = height - ((points.get(i).getY() - minY) * scale + offsetY);
                }

                gc.setFill(fillColor);
                gc.fillPolygon(xPoints, yPoints, points.size());
                gc.strokePolygon(xPoints, yPoints, points.size());
            }
        }

        // Отрисовка названий штатов
        drawStateNames(gc, scale, offsetX, offsetY, width, height);
    }

    private void drawStateNames(GraphicsContext gc, double scale, double offsetX, double offsetY, double width, double height) {
        gc.setFill(Color.BLACK); // Черный цвет текста
        gc.setFont(javafx.scene.text.Font.font("Arial", 10)); // Тонкий шрифт небольшого размера

        for (State state : states) {
            // Вычисляем центр штата для размещения названия
            double centerX = 0;
            double centerY = 0;
            int pointCount = 0;

            for (Polygon polygon : state.getPolygons()) {
                for (Point point : polygon.getPoints()) {
                    centerX += point.getX();
                    centerY += point.getY();
                    pointCount++;
                }
            }

            if (pointCount > 0) {
                centerX /= pointCount;
                centerY /= pointCount;

                // Преобразуем координаты центра для отрисовки
                double drawX = (centerX - minX) * scale + offsetX;
                double drawY = height - ((centerY - minY) * scale + offsetY);

                // Отрисовываем название штата
                gc.fillText(state.getName(), drawX, drawY);
            }
        }
    }

    private Color getStateColor(String stateName) {
        if (stateSentiments.containsKey(stateName)) {
            double sentiment = stateSentiments.get(stateName);

            // Нейтральное настроение (белый)
            if (sentiment >= -0.01 && sentiment <= 0.01) {
                return Color.WHITE;
            }
            // Позитивное настроение (желтый)
            else if (sentiment > 0.01) {
                double opacity = 0.7 + 0.3 * sentiment;
                return Color.rgb(
                        255,
                        (int)(255 * (1 - sentiment * 0.3)),
                        0,
                        opacity
                );
            }
            // Негативное настроение (синий)
            else {
                double opacity = 0.7 + 0.3 * (-sentiment);
                return Color.rgb(
                        (int)(150 * (1 + sentiment)),
                        (int)(150 * (1 + sentiment)),
                        255,
                        opacity
                );
            }
        }
        // Серый для штатов без данных (с небольшой прозрачностью)
        return Color.rgb(200, 200, 200, 0.5);
    }
}