package pl.kamil.domain.service;

import java.util.random.RandomGenerator;

public class RandomlyGeneratedNumbers implements RandomNumbers {
    private final RandomGenerator rg = RandomGenerator.getDefault();

    @Override
    public int nextInt(int upperBoundExclusive) {
        return rg.nextInt(upperBoundExclusive);
    }

    @Override
    public double nextDouble(double upperBoundExclusive) {
        return rg.nextDouble(upperBoundExclusive);
    }

    @Override
    public double nextGaussian() {
        return rg.nextGaussian();
    }
}
