package pl.kamil.domain.algorithm2.ZDT;

import pl.kamil.domain.algorithm2.ParetoEvalFunc;
import pl.kamil.domain.model.Point;

import java.util.ArrayList;
import java.util.List;

public class ZDT1 implements ParetoEvalFunc {
    public void evalFunc(Point decisionVector) {
        List<Double> objectives = new ArrayList<>();
        double f1 = decisionVector.getCoords().get(0); // Pierwsza funkcja celu f1 = x1
        List<Double> gInput = decisionVector.getCoords()
                .subList(1, decisionVector.getCoords().size()); // Pozostałe zmienne decyzyjne funkcji g
        double g = countG(gInput); // Funkcja g
        double h = 1 - Math.sqrt(f1 / g); // Funkcja h
        double f2 = g * h; // Druga funkcja celu: f2 = g * h
        objectives.add(f1);
        objectives.add(f2);
        decisionVector.setObjectives(objectives);
    }

    private Double countG(List<Double> gInput) {
        double sum = 0.0;
        for (Double d : gInput) {
            sum += d;
        }
        return 1 + 9 * (sum / gInput.size());
    }
}
