package pl.kamil.domain.algorithm.sa.calc.control;

public class Exp2SchemeControl implements CalculateControl{
    @Override
    public double calculateControl(double T1, double TN, int N, int k) {
        return T1 * Math.exp(-((double) k / N) * Math.log(T1 / TN));
    }
}
