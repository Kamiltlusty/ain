package pl.kamil.domain.algorithm;

import pl.kamil.domain.model.Point;

import java.util.ArrayList;
import java.util.List;

public class Naive implements ParetoAlgorithm {
    public List<Point> runExperiment(List<Point> points) {
        List<Point> nondominated = new ArrayList<>();
        for (int m = 0; m < points.size(); m++) {
            Point current = points.get(m);
            boolean dominated = false;

            for (int n = 0; n < points.size(); n++) {
                if (n == m) continue;

                Point other = points.get(n);
                if (dominates(other, current)) {
                    dominated = true;
                    break;
                }
            }
            if (!dominated) {
                nondominated.add(current);
            }
        }
        return nondominated;
    }

    private boolean dominates(Point pointA, Point pointB) {
        boolean allBetterOrEqual = true;
        boolean atLeastOneBetter = false;

        for (int i = 0; i < pointA.getObjectives().size(); i++) {
            double aValue = pointA.getObjectives().get(i);
            double bValue = pointB.getObjectives().get(i);

            if (aValue > bValue) {
                allBetterOrEqual = false;
                break;
            } else if (aValue < bValue) {
                atLeastOneBetter = true;
            }
        }

        return allBetterOrEqual && atLeastOneBetter;
    }
}
