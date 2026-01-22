package pl.kamil.domain.algorithm2.ZDT;

import pl.kamil.domain.algorithm2.ParetoEvalFunc;
import pl.kamil.domain.model.Point;

import java.util.ArrayList;
import java.util.List;

public class ZDT2 implements ParetoEvalFunc {
    public void evalFunc(Point decisionVector) {
        List<Double> objectives = new ArrayList<>();
        double f1 = decisionVector.getCoords().get(0); // Pierwsza funkcja celu
        List<Double> gInput =  decisionVector.getCoords()
                .subList(1, decisionVector.getCoords().size()); // Pozostałe zmienne decyzyjne
        double g = countG(gInput);
        double h = 1 - Math.pow(f1/g, 2);
        double f2 = g * h; // Druga funkcja celu
        objectives.add(f1);
        objectives.add(f2);
        decisionVector.setObjectives(objectives);
    }

    private Double countG(List<Double> gInput) {
        return 1 + 9*gInput.stream()
                .map((a) -> a/(gInput.size()))
                .reduce(0.0, Double::sum);
    }
}
