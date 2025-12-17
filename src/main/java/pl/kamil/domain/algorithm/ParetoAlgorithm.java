package pl.kamil.domain.algorithm;

import pl.kamil.domain.model.Point;
import java.util.List;

public interface ParetoAlgorithm {
    List<Point> runExperiment(List<Point> points);
}