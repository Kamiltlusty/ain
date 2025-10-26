package pl.kamil.domain.algorithm.ls.eval.func;

import pl.kamil.domain.model.Point;

import java.util.List;

public class Ackley implements LSEvalFunc{
    @Override
    public double evalFunc(Point p) {
        double sum = 0.0;
        double prod = 1.0;
        List<Double> coords = p.getCoords();
        for (int i = 0; i < coords.size(); i++) {
            double xi = coords.get(i);
            sum += xi * xi / 4000.0;
            prod *= Math.cos(xi / Math.sqrt(i + 1));
        }
        return sum - prod + 1;
    }
}
