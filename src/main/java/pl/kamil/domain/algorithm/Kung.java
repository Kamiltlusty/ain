package pl.kamil.domain.algorithm;

import pl.kamil.domain.model.Point;

import java.util.ArrayList;
import java.util.List;

public class Kung implements ParetoAlgorithm {
    public List<Point> runExperiment(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return new ArrayList<>();
        }

        // Tworzenie kopii
        List<Point> sortedPoints = new ArrayList<>(points);

        // Sortowanie malejąco według pierwszej współrzędnej
        sortedPoints.sort((p1, p2) ->
                Double.compare(p2.getCoords().get(0), p1.getCoords().get(0)));

        return kungAlgorithm(sortedPoints);
    }

    private List<Point> kungAlgorithm(List<Point> points) {
        if (points.size() <= 1) {
            return new ArrayList<>(points);
        }

        int mid = points.size() / 2;
        List<Point> leftPart = new ArrayList<>(points.subList(0, mid));
        List<Point> rightPart = new ArrayList<>(points.subList(mid, points.size()));

        List<Point> leftResult = kungAlgorithm(leftPart);
        List<Point> rightResult = kungAlgorithm(rightPart);

        // Filtrowanie prawej części
        List<Point> filteredRight = new ArrayList<>();
        for (Point r : rightResult) {
            boolean dominated = false;
            for (Point l : leftResult) {
                if (dominates(l, r)) {
                    dominated = true;
                    break;
                }
            }
            if (!dominated) {
                filteredRight.add(r);
            }
        }

        // laczenie wynikow
        List<Point> result = new ArrayList<>(leftResult);
        result.addAll(filteredRight);
        return result;
    }

    private boolean dominates(Point dominator, Point dominated) {
        boolean isAnyBetter = false;
        for (int i = 0; i < dominated.getCoords().size(); i++) {
            // jeśli jakikolwiek wymiar jest lepszy w sprawdzanym (dominated) to zwracamy false - nie jest zdominowany
            if (dominated.getCoords().get(i) > dominator.getCoords().get(i)) {
                return false;
            } else if (dominated.getCoords().get(i) < dominator.getCoords().get(i)) {
                isAnyBetter = true;
            }
        }
        return isAnyBetter;
    }
}