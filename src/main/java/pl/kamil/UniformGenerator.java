package pl.kamil;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class UniformGenerator implements DataGenerator {
    private final Map<String, Integer> data;
    private final Random rd = new Random();

    public UniformGenerator() {
        this.data = new TreeMap<>();
    }

    @Override
    public Map<String, Integer> generate(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be greater than 0");
        }

        for (int i = 0; i < N; i++) {
            Double val = rd.nextDouble(0, 10);
            setNumberInMap(val);
        }
        return data;
    }

    @Override
    public void setNumberInMap(Double val) {
        double binWidth = 1.0;
        double start = Math.floor(val / binWidth) * binWidth;
        String binLabel = String.format("%.1f:%.1f", start, start + binWidth);
        data.put(binLabel, data.getOrDefault(binLabel, 0) + 1);
    }
}
