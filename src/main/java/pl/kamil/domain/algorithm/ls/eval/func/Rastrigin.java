package pl.kamil.domain.algorithm.ls.eval.func;

import pl.kamil.domain.model.Point;

public class Rastrigin implements LSEvalFunc {
    @Override
    public double evalFunc(Point p) {
        int n = p.getCoords().size();
        return 10 * n + p.getCoords().stream()
                .map(v -> v*v - 10 * Math.cos(2 * Math.PI * v))
                .reduce(0.0, Double::sum);
    }
}
