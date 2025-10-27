package pl.kamil.domain.service;

import pl.kamil.domain.model.UInt16;

public class RepresentationConversionService {
    private final double domainXStart;
    private final double domainXEnd;

    public RepresentationConversionService(double domainXStart, double domainXEnd) {
        this.domainXStart = domainXStart;
        this.domainXEnd = domainXEnd;
    }

    // normalizacja z dowolnego zakresu na [0;1]
    public double normalize(double val, double min, double max) {
        return (val - min) / (max - min);
    }

    // skalowanie z [0;1] do dowolnego zakresu
    public double scaleToDomainRange(double norm, double min, double max) {
        return norm * (max - min) + min;
    }

    // skalowanie z [0;1] na zakres domeny funkcji
    public double scaleToDomainRange(double norm) {
        return norm * (domainXEnd - domainXStart) + domainXStart;
    }

    // z dowolnej na zakres domeny funkcji
    public double toDomain(double anyVal) {
        double normalized = normalize(anyVal, domainXStart, domainXEnd);
        return scaleToDomainRange(normalized);
    }

    // z dowolnej na binarna16
    public UInt16 toUInt16(double anyVal) {
        return new UInt16 ((int) (normalize(anyVal, domainXStart, domainXEnd) * 65535));
    }

    // z binarnej16 na zakres domeny funkcji
    public double toDomain(UInt16 uint16) {
        return scaleToDomainRange((double) uint16.getVal() /65535);
    }
}
