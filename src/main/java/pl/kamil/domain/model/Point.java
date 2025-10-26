package pl.kamil.domain.model;

import java.util.List;
import java.util.stream.DoubleStream;

public class Point {
    private List<Double> coords;
    private List<UInt16> coords16;

    public Point(List<Double> coords) {
        this.coords = coords;
    }

    public Point(Integer dimSize) {
        this.coords = fillCords(dimSize);
    }

    public List<Double> fillCords(Integer dimSize) {
        return DoubleStream
                .generate(() -> 10.0)
                .limit(dimSize)
                .boxed()
                .toList();
    }

    public Point copy() {
        List<Double> newCoords = coords == null ? null : coords.stream()
                .map(Double::valueOf)
                .toList();

        List<UInt16> newCoords16 = coords16 == null ? null : coords16.stream()
                .map(UInt16::copy)
                .toList();

        Point copy = new Point(newCoords);
        copy.setCoords16(newCoords16);

        return copy;
    }

    public List<Double> getCoords() {
        return coords;
    }

    public List<UInt16> getCoords16() {
        return coords16;
    }

    public void setCoords(List<Double> coords) {
        this.coords = coords;
    }

    public void setCoords16(List<UInt16> coords16) {
        this.coords16 = coords16;
    }

    @Override
    public String toString() {
        return "Point{" +
                "coords=" + coords16 +
                '}';
    }
}
