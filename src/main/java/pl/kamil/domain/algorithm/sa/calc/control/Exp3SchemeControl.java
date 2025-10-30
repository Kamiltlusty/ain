package pl.kamil.domain.algorithm.sa.calc.control;

public class Exp3SchemeControl implements CalculateControl{
    @Override
    public double calculateControl(double T1, double TN, int N, int k) {
        return T1 * Math.exp(-Math.pow((double) k / N, 2) * Math.log(T1 / TN));
    }
}
