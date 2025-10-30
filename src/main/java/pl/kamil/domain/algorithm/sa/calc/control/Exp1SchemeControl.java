package pl.kamil.domain.algorithm.sa.calc.control;

public class Exp1SchemeControl implements CalculateControl{
    @Override
    public double calculateControl(double T1, double TN, int N, int k) {
        return (T1 - TN) / (1 + Math.exp(0.3 * (k - N / 2.0))) + TN;
    }
}
