package pl.kamil.domain.algorithm.ls.eval.func;

import pl.kamil.domain.model.Point;

public class Spherical implements LSEvalFunc {
    @Override
    public double evalFunc(Point p) {
        return p.getCoords().stream()
                .map(v -> v*v)
                .reduce(0.0, Double::sum);
    }
}
