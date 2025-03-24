    package Map;

    import java.util.List;

    public class State {
        private String name;
        private List<Polygon> polygons;

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
    }