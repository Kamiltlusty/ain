package pl.kamil.domain.algorithm2.ZDT;

import pl.kamil.domain.algorithm2.ParetoEvalFunc;
import pl.kamil.domain.model.Point;

import java.util.ArrayList;
import java.util.List;

public class ZDT4 implements ParetoEvalFunc {
    public void evalFunc(Point decisionVector) {
        List<Double> objectives = new ArrayList<>();
        double f1 = decisionVector.getCoords().get(0);
        List<Double> gInput =  decisionVector.getCoords()
                .subList(1, decisionVector.getCoords().size());
        double g = countG(gInput);
        double h = 1 - Math.sqrt(f1/g);
        double f2 = g*h;
        objectives.add(f1);
        objectives.add(f2);
        decisionVector.setObjectives(objectives);
    }

    private Double countG(List<Double> gInput) {
        return 1 + 10 * (gInput.size()) + gInput.stream()
                .map((a) -> Math.pow(a, 2) - 10 * Math.cos(4 * Math.PI * a))
                .reduce(0.0, Double::sum);
    }
}
