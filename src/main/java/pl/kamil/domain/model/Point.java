package pl.kamil.domain.model;

import pl.kamil.domain.service.RepresentationConversionService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

public class Point {
    private List<Double> coords;
    private List<UInt16> coords16;
    private final RepresentationConversionService rcs;

    public Point(List<Double> coords, RepresentationConversionService rcs) {
        this.coords = coords;
        this.rcs = rcs;
    }

    public Point(RepresentationConversionService rcs) {
//        this.coords = fillCords(dimSize, rightDomainCorner);
        this.rcs = rcs;
    }

    public void fillCoords(Integer dimSize, double rightDomainCorner) {
        this.coords = DoubleStream
                .generate(() -> rightDomainCorner)
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

        Point copy = new Point(newCoords, rcs);
        copy.setCoords16(newCoords16);

        return copy;
    }

    public void fromUInt16toDomain() {
        setCoords(getCoords16().stream()
                .map(rcs::toDomain)
                .toList());
    }

    public void fromAnyToDomain() {
        setCoords(getCoords().stream()
                .map(rcs::toDomain).toList());
    }

    public void toUInt16() {
        setCoords16(new ArrayList<>(getCoords().stream()
                .map(rcs::toUInt16)
                .toList()));
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
