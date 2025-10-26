package pl.kamil.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UniformGenerator implements DataGenerator {
    private final Map<String, Integer> data;
    private final RandomNumbers rn;

    public UniformGenerator(RandomNumbers rn) {
        this.rn = rn;
        this.data = new TreeMap<>();
    }

    @Override
    public List<Double> generateTXT(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be greater than 0");
        }

        List<Double> data = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            data.add(rn.nextDouble( 1));
        }
        return data;
    }

    @Override
    public Map<String, Integer> generate(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be greater than 0");
        }

        for (int i = 0; i < N; i++) {
            Double val = rn.nextDouble( 10);
            System.out.println("generator: " + val);
            setNumberInMap(val);
        }
        return data;
    }

    private void setNumberInMap(Double val) {
        double binWidth = 1.0;
        double start = Math.floor(val / binWidth) * binWidth;
        String binLabel = String.format("%.1f:%.1f", start, start + binWidth);
        System.out.println("setNumberInMap: " + binLabel);
        data.put(binLabel, data.getOrDefault(binLabel, 0) + 1);
    }
}
