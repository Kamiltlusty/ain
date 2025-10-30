package pl.kamil.domain.algorithm.sa.calc.control;

public class TanhSchemeControl implements CalculateControl{
    @Override
    public double calculateControl(double T1, double TN, int N, int k) {
        return 0.5 * (T1 - TN) * (1 - Math.tanh(10 * ((double) k / N) - 5)) + TN;
    }
}
