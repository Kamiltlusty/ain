package pl.kamil.domain.service;

public interface RandomNumbers {
    int nextInt(int upperBoundExclusive);
    double nextDouble(double upperBoundExclusive);
    double nextGaussian();
}
