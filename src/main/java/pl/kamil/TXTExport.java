package pl.kamil;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TXTExport implements DataExport {
    @Override
    public void save(Map<String, Integer> data, String fileName) {

    }

    @Override
    public void save(List<Double> data1, List<Double> data2, List<Double> data3, String fileName) {
        try (var writer = new BufferedWriter(new FileWriter(fileName + ".txt"))) {
            for (int i = 0; i < data1.size(); i++) {
                String formatted = String.format(Locale.US, "%s;%s;%s",
                        i < data1.size() ? String.format("%.10f", data1.get(i)) : "",
                        i < data2.size() ? String.format("%.10f", data2.get(i)) : "",
                        i < data3.size() ? String.format("%.10f", data3.get(i)) : "");
                writer.write(formatted);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
