package pl.kamil.domain.service;

public interface RandomNumbers {
    int nextInt(int upperBoundExclusive);
    int nextInt(int lowerBoundInclusive, int upperBoundExclusive);
    double nextDouble();
    double nextDouble(double upperBoundExclusive);
    double nextDouble(double lowerBoundInclusive, double upperBoundExclusive);
    double nextGaussian();
    double nextGaussian(double mean, double standardDeviation);
}
