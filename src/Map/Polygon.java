package Map;

import java.util.List;

public class Polygon {
    private final List<Point> points;

    public Polygon(List<Point> points) {
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }


    public boolean contains(Point testPoint) {
        if (testPoint == null || points == null || points.size() < 3) {
            return false;
        }

        boolean result = false;
        int n = points.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(j);

            if (p1 == null || p2 == null) {
                continue;
            }

            // координаты текущей точки
            double xi = p1.getX();
            double yi = p1.getY();
            double xj = p2.getX();
            double yj = p2.getY();

            // проверка пересечения луча с ребром полигона
            boolean intersect = ((yi > testPoint.getY()) != (yj > testPoint.getY()))
                    && (testPoint.getX() < (xj - xi) * (testPoint.getY() - yi) / (yj - yi) + xi);

            if (intersect) {
                result = !result;
            }
        }

        return result;
    }
}