package pl.kamil.application.ports;

import pl.kamil.domain.algorithm.Topic8Ex2;

import java.util.List;

public interface SaveFrontAndPoints {
    void save(List<Topic8Ex2.FrontAndPoint> fap, String filename);
}
