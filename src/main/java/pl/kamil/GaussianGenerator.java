package pl.kamil;

import java.util.*;

public class GaussianGenerator implements DataGenerator {
    private final Map<String, Integer> data;
    private final Random rd = new Random();

    public GaussianGenerator() {
        // -0,2:0,4 => -0.2
        this.data = new TreeMap<>(Comparator.comparing(
                key -> Double.parseDouble(
                        key.split(":")[0].replace(",", ".")
                )
        ));
    }

    @Override
    public Map<String, Integer> generate(int N) {
        if (N <= 0) {throw new IllegalArgumentException("N must be greater than 0");}

        for (int i = 0; i < N; i++) {
            Double val = rd.nextGaussian();
            setNumberInMap(val);
        }
        return data;
    }

    @Override
    public List<Double> generateTXT(int N) {
        return List.of();
    }

    private void setNumberInMap(Double val) {
        double binWidth = 0.2;
        double scaledBinWidth = 10 * binWidth;
        double scaledValue = 10 *  val;
        double scaledStart = Math.floor(scaledValue / scaledBinWidth) *  scaledBinWidth;
        double start = scaledStart/10;
        String binLabel = String.format("%.1f:%.1f", start, start + binWidth);
        data.put(binLabel, data.getOrDefault(binLabel, 0) + 1);
    }
}
