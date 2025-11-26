package pl.kamil.application.ports;

import java.util.List;
import java.util.Map;

public interface DataExport {
//    void save(Map<String, Integer> data, String fileName);
    void save(List<Double> data1, String fileName);
    void save(List<Double> data1, List<Double> data2, List<Double> data3, String fileName);
    void save (Map<Integer, List<Double>> data, String fileName);
    void save(int[][] ecdfValues, String wartosciDoWykresuEcdf);
}
