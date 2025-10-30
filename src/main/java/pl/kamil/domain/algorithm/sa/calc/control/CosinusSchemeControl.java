package pl.kamil.domain.algorithm.sa.calc.control;

public class CosinusSchemeControl implements CalculateControl {
    @Override
    public double calculateControl(double T1, double TN, int N, int k) {
        return 0.5*(T1 - TN) * (1 + Math.cos(k*Math.PI/N)) + TN;
    }
}
