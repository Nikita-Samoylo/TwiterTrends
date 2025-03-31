package Map;

import java.util.List;

public class State {
    private final String name;
    private final List<Polygon> polygons;

    public State(String name, List<Polygon> polygons) {
        this.name = name;
        this.polygons = polygons;
    }

    public String getName() {
        return name;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    /**
     * Проверяет, принадлежит ли точка к этому штату
     * @param point Точка для проверки
     * @return true если точка находится внутри любого полигона штата
     */
    public boolean contains(Point point) {
        if (point == null || polygons == null) {
            return false;
        }

        for (Polygon polygon : polygons) {
            if (polygon != null && polygon.contains(point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает двухбуквенный код штата
     * @return Код штата (например, "CA" для Калифорнии)
     */


    @Override
    public String toString() {
        return "State{" +
                "name='" + name + '\'' +
                ", polygons=" + polygons +
                '}';
    }
}