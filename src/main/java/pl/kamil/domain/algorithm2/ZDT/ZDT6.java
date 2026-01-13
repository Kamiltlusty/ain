package pl.kamil.domain.algorithm2.ZDT;

import pl.kamil.domain.algorithm2.ParetoEvalFunc;
import pl.kamil.domain.model.Point;

import java.util.ArrayList;
import java.util.List;

public class ZDT6 implements ParetoEvalFunc {
    public void evalFunc(Point decisionVector) {
        List<Double> objectives = new ArrayList<>();
        double x1 = decisionVector.getCoords().get(0);
        double f1 = 1 - Math.exp(-4*x1) * Math.pow(Math.sin(6 * Math.PI * x1), 6);
        List<Double> gInput =  decisionVector.getCoords()
                .subList(1, decisionVector.getCoords().size());
        double g = countG(gInput);
        double h = 1 - Math.pow(f1/g, 2);
        double f2 = g*h;
        objectives.add(f1);
        objectives.add(f2);
        decisionVector.setObjectives(objectives);
    }

    private Double countG(List<Double> gInput) {
        return 1 + 9*Math.pow(gInput.stream()
                .map((a) -> a/(gInput.size()))
                .reduce(0.0, Double::sum), 0.25);
    }
}
