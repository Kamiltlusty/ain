package pl.kamil.infrastructure.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataProcessor {
    public List<Double> average100InvokesTo1(Map<Integer, List<Double>> fxResults) {
        int mapSize = fxResults.size();
        if (mapSize != 100) {
            throw new IllegalStateException(
                    "Niepoprawna liczba przejsc programu oczekiwano 100, otrzymano: "
                            + mapSize);
        }

        List<Double> list = fxResults.get(0);
        int listSize = list.size();
        if (listSize != 10_000) {
            throw new IllegalStateException(
                    "Niepoprawna liczba wywolan F(x): oczekiwano 10 000, otrzymano: "
                            + listSize);
        }

        List<Double> avgResults = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            double sum = 0;

            for (int j = 0; j < mapSize; j++) {
                sum += fxResults.get(j).get(i);
            }
            avgResults.add(sum / mapSize);
        }
        return avgResults;
    }
}
