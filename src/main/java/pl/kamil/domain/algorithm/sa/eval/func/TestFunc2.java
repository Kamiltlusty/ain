package pl.kamil.domain.algorithm.sa.eval.func;

import pl.kamil.domain.model.Point;

public class TestFunc2 implements EvalFunc {
    private static final double a = 20;
    private static final double b = 0.2;
    private static final double c = 2*Math.PI;

    @Override
    public double evalFunc(Point p) {
        double d = p.getCoords().size();
        double sumOfSeries1 = 0;
        // obliczenie sumy szeregu E i = 1...n xi^2
        var sum = p.getCoords().stream()
                .mapToDouble(x -> x * x)
                .reduce(Double::sum);
        if (sum.isPresent()) {sumOfSeries1 = sum.getAsDouble();}
        else throw new RuntimeException("Stream should return reduced sum!");

        double sumOfSeries2 = 0;
        // obliczenie sumy szeregu E i = 1...n cos(cxi)
        var sum2 = p.getCoords().stream()
                .mapToDouble(x -> Math.cos(c*x))
                .reduce(Double::sum);
        if (sum2.isPresent()) {
            sumOfSeries2 = sum2.getAsDouble();}
        else throw new RuntimeException("Stream should return reduced sum!");

        double expression1 = -a*Math.exp(-b* Math.sqrt(1/d*sumOfSeries1));
        double expression2 = Math.exp(1/d*sumOfSeries2);

        return expression1 - expression2 + a + Math.exp(1);
    }
}
