package pl.kamil;

import java.util.Map;

public interface DataExport {
    void save(Map<String, Integer> data, String fileName);
}
