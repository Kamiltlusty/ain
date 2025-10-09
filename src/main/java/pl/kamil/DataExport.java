package pl.kamil;

import java.util.List;
import java.util.Map;

public interface DataExport {
    void save(Map<String, Integer> data, String fileName);
    void save(List<Double> data1, List<Double> data2, List<Double> data3, String fileName);
}
