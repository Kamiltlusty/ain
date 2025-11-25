package pl.kamil.domain.algorithm.sa.eval.func;

import pl.kamil.domain.model.Point;

// A.1.3 Generalized Rosenbrock
public class GeneralizedRosenbrock implements EvalFunc {
    @Override
    public double evalFunc(Point p) {
        double fx = 0.0;
        int D = p.getCoords().size();
        for (int j = 0; j < D - 1; ++j) {
            double pom1 = p.getCoords().get(j + 1) - p.getCoords().get(j) * p.getCoords().get(j);
            pom1 = 100.0 * pom1 * pom1;
            double pom2 = p.getCoords().get(j) - 1;
            pom2 = pom2 * pom2;
            fx += pom1 + pom2;
        }
        return fx;
    }
}
