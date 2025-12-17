package pl.kamil.application.ports;

import pl.kamil.domain.model.Point;

import java.util.List;

public interface WritePointsFromTopic8Ex2 {
    List<Point> write(String fileName);
}
