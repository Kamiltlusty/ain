package pl.kamil.domain.service;

import java.util.List;
import java.util.Map;

public interface DataGenerator {
    Map<String, Integer> generate(int N);
    List<Double> generateTXT(int N);
}
