package pl.kamil.domain.algorithm.sa.calc.control;

public class CoshSchemeControl implements CalculateControl {
    @Override
    public double calculateControl(double T1, double TN, int N, int k) {
        return (T1 - TN) / Math.cosh(10 * ((double) k / N)) + TN;
    }
}
