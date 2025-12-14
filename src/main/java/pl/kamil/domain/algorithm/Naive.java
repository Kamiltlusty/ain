package pl.kamil.domain.algorithm;

import pl.kamil.domain.model.Point;

import java.util.ArrayList;
import java.util.List;

public class Naive {

    public List<Point> runExperiment(List<Point> points) {
        List<Point> nondominated = new ArrayList<>();
        for (int m = 0; m < points.size(); m++) {
            Point i = points.get(m);
            boolean dominated = false;

            for (int n = 0; n < points.size(); n++) {
                if (n == m) continue;

                Point j = points.get(n);
                if (dominates(i, j)) {
                    dominated = true;
                    break;
                }
            }
            if (!dominated) {
                nondominated.add(i);
            }
        }
        return nondominated;
    }

    private boolean dominates(Point checked, Point dominator) {
        boolean isAnyBetter = false;
        for (int i = 0; i < checked.getCoords().size(); i++) {
            // jesli jakikolwiek wymiar jest lepszy w sprawdzanym to zwracamy false - nie jest zdominowany
            if (checked.getCoords().get(i) > dominator.getCoords().get(i)) {
                return false;
            } else if (checked.getCoords().get(i) < dominator.getCoords().get(i)) {
                isAnyBetter = true;
            }
        }
        return isAnyBetter;
    }
}
