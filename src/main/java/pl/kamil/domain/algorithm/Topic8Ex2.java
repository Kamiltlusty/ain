package pl.kamil.domain.algorithm;

import pl.kamil.domain.model.Point;

import java.util.ArrayList;
import java.util.List;

public class Topic8Ex2 {
    private final Naive naive;

    public Topic8Ex2(Naive naive) {
        this.naive = naive;
    }

    public class FrontAndPoint {
        private int front;
        private Point point;

        public FrontAndPoint(int front, Point point) {
            this.front = front;
            this.point = point;
        }

        public int getFront() {
            return front;
        }

        public Point getPoint() {
            return point;
        }
    }

    public List<FrontAndPoint> findFronts(List<Point> points) {
        List<FrontAndPoint> fronts = new ArrayList<>();
        int i = 1;
        while (!points.isEmpty()) {
            List<Point> nondominated = naive.runExperiment(points);
            points.removeAll(nondominated);

            for  (Point point : nondominated) {
                fronts.add(new FrontAndPoint(i, point));
            }
            i++;
        }
        return fronts;
    }
}
