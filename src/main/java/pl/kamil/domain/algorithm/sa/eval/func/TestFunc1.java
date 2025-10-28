package pl.kamil.domain.algorithm.sa.eval.func;

import pl.kamil.domain.model.Point;

public class TestFunc1 implements SAEvalFunc{
    @Override
    public double evalFunc(Point p) {
        double fx = 0.0;
        double meter = -5.0;
        double denominator;

        // obliczenie sumy szeregu E i = 1...n xi^2
        var sum = p.getCoords().stream()
                .mapToDouble(x -> x * x)
                .reduce(Double::sum);
        if (sum.isPresent()) {denominator = 1.0 + sum.getAsDouble();}
        else throw new RuntimeException("Stream should return reduced sum!");

        // obliczenie wykladnika
        double exponent = meter / denominator;

        // caly wzor
        fx = exponent + Math.sin(1/Math.tan(Math.exp(exponent)));
        return fx;
    }
}
