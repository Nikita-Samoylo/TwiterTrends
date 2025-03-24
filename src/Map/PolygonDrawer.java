package Map;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PolygonDrawer extends JPanel {
    private final List<State> states;
    private double minX = Double.MAX_VALUE;
    private double maxX = Double.MIN_VALUE;
    private double minY = Double.MAX_VALUE;
    private double maxY = Double.MIN_VALUE;

    public PolygonDrawer(List<State> states) {
        this.states = states;
        calculateBounds(); // Вычисляем границы всех полигонов
    }

    // Метод для вычисления границ всех полигонов
    private void calculateBounds() {
        for (State state : states) {
            for (Polygon polygon : state.getPolygons()) {
                for (Point point : polygon.getPoints()) {
                    if (point.getX() < minX) minX = point.getX();
                    if (point.getX() > maxX) maxX = point.getX();
                    if (point.getY() < minY) minY = point.getY();
                    if (point.getY() > maxY) maxY = point.getY();
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Устанавливаем цвет рисования на черный
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2)); // Увеличиваем толщину линий

        // Вычисляем масштаб и смещение для отображения полигонов
        int width = getWidth();
        int height = getHeight();
        double scaleX = width / (maxX - minX);
        double scaleY = height / (maxY - minY);
        double scale = Math.min(scaleX, scaleY) * 0.9; // Оставляем немного места по краям

        // Смещение влево на 100 пикселей
        double shiftX = -600; // Уменьшите или увеличьте это значение для регулировки сдвига
        double offsetX = -minX * scale + (width - (maxX - minX) * scale) / 2 + shiftX;
        double offsetY = -minY * scale + (height - (maxY - minY) * scale) / 2;

        // Отрисовываем все полигоны для всех штатов
        for (State state : states) {
            // Отрисовка полигонов штата
            for (Polygon polygon : state.getPolygons()) {
                List<Point> points = polygon.getPoints();
                int[] xPoints = new int[points.size()];
                int[] yPoints = new int[points.size()];

                // Масштабируем и смещаем координаты
                for (int i = 0; i < points.size(); i++) {
                    xPoints[i] = (int) ((points.get(i).getX() - minX) * scale + offsetX);
                    yPoints[i] = (int) ((maxY - points.get(i).getY()) * scale + offsetY); // Инвертируем Y
                }

                // Рисуем полигон
                g2d.drawPolygon(xPoints, yPoints, points.size());
            }

            // Вычисляем центр штата и добавляем текст с названием
            double totalX = 0;
            double totalY = 0;
            int totalPoints = 0;

            for (Polygon polygon : state.getPolygons()) {
                for (Point point : polygon.getPoints()) {
                    totalX += point.getX();
                    totalY += point.getY();
                    totalPoints++;
                }
            }

            double centerX = totalX / totalPoints;
            double centerY = totalY / totalPoints;

            int labelX = (int) ((centerX - minX) * scale + offsetX);
            int labelY = (int) ((maxY - centerY) * scale + offsetY); // Инвертируем Y

            // Рисуем название штата
            g2d.setColor(Color.RED);
            g2d.drawString(state.getName(), labelX, labelY);
        }
    }
}
