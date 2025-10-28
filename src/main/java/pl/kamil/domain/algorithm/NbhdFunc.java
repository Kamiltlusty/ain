package pl.kamil.domain.algorithm;

import pl.kamil.domain.model.Point;
import pl.kamil.domain.model.UInt16;
import pl.kamil.domain.service.RandomlyGeneratedNumbers;

public class NbhdFunc {
    private final RandomlyGeneratedNumbers rn;

    public NbhdFunc(RandomlyGeneratedNumbers rn) {
        this.rn = rn;
    }

    // nbhd -> neighborhood
    public Point nbhdFunc(Point p, int m) {
        Point neighbor = p.copy();

        // zamiana z zakresu [-10 - 10] na uint16
        neighbor.toUInt16();

        // obliczenie losowego wymiaru i pobranie wymiaru z punktu
        int dimCount = neighbor.getCoords16().size();
        int randomDim = rn.nextInt(dimCount);
        UInt16 originalUInt16 = neighbor.getCoords16().get(randomDim);

        // perturbacja
        UInt16 perturbed = perturb(originalUInt16, m);

        // wstawienie do sasiada
        neighbor.getCoords16().set(randomDim, perturbed);
        return neighbor;
    }

    public UInt16 perturb(UInt16 uInt16, int m) {
        int value = uInt16.getVal();
        for (int j = 0; j < 16; j++) {
            if (rn.nextDouble(1) < ((double) m / 16)) {
                value ^= (1 << j); // XOR, odwraca bit j
            }
        }
        return new UInt16(value);
    }
}
