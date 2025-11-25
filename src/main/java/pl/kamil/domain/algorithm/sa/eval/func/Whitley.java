package pl.kamil.domain.algorithm.sa.eval.func;

import pl.kamil.domain.model.Point;

public class Whitley implements EvalFunc {
    @Override
    public double evalFunc(Point p) {
        double fx = 0.0;
        int D = p.getCoords().size();
        for(int k = 0; k < D; ++k) {
            for (int j = 0; j < D; ++j) {
                double pom1 = (p.getCoords().get(k) - Math.pow(p.getCoords().get(j), 2));
                double pom2 = (1 - p.getCoords().get(j));
                double y = 100.0 * Math.pow(pom1, 2) + Math.pow(pom2, 2);
                fx += Math.pow(y, 2) / 4000.0 - Math.cos(y) + 1;
            }
        }

        return fx;
    }
}
