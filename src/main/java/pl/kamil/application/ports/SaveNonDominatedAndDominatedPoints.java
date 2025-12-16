package pl.kamil.application.ports;

import pl.kamil.domain.model.Point;

import java.util.List;

public interface SaveNonDominatedAndDominatedPoints {
    void save(List<Point> nDom,
              List<Point> dom,
              String filename,
              boolean append);
}
