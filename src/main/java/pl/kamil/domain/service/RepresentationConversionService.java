package pl.kamil.domain.service;

import pl.kamil.domain.model.UInt16;

public class RepresentationConversionService {
    private final static double DOMAIN_X_START = -10.0;
    private final static double DOMAIN_X_END = 10.0;
    public RepresentationConversionService() {}

    // normalizacja z dowolnego zakresu na [0;1]
    public double normalize(double val, double min, double max) {
        return (val - min) / (max - min);
    }

    // skalowanie z [0;1] do dowolnego zakresu
    public double scaleToDomainRange(double norm, double min, double max) {
        return norm * (max - min) + min;
    }

    // skalowanie z [0;1] do -10 - 10
    public double scaleToDomainRange(double norm) {
        return norm * (DOMAIN_X_END - DOMAIN_X_START) + DOMAIN_X_START;
    }

    // dowolnej na zakres -10 10
    public double toDomain(double anyVal) {
        double normalized = normalize(anyVal, DOMAIN_X_START, DOMAIN_X_END);
        return scaleToDomainRange(normalized);
    }

    // z dowolnej na binarna16
    public UInt16 toUInt16(double anyVal) {
        return new UInt16 ((int) (normalize(anyVal, DOMAIN_X_START, DOMAIN_X_END) * 65535));
    }

    // z binarnej16 na zakres -10 10
    public double toDomain(UInt16 uint16) {
        return scaleToDomainRange((double) uint16.getVal() /65535);
    }
}
