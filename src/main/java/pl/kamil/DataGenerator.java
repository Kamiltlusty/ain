package pl.kamil;

import java.util.Map;

public interface DataGenerator {
    Map<String, Integer> generate(int N);
    void setNumberInMap(Double val);
}
