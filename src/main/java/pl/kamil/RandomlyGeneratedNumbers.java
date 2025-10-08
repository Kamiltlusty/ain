package pl.kamil;

import java.util.random.RandomGenerator;

public class RandomlyGeneratedNumbers implements RandomNumbers {
    private final RandomGenerator rg = RandomGenerator.getDefault();

    @Override
    public double nextDouble(double upperBoundExclusive) {
        return rg.nextDouble(upperBoundExclusive);
    }
}
