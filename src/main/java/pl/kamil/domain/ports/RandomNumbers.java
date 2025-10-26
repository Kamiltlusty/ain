package pl.kamil.domain.ports;

public interface RandomNumbers {
    double nextDouble(double upperBoundExclusive);
    double nextGaussian();
}
