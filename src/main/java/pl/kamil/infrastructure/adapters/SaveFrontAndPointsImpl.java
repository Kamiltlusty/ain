package pl.kamil.infrastructure.adapters;

import pl.kamil.application.ports.SaveFrontAndPoints;
import pl.kamil.domain.algorithm.Topic8Ex2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SaveFrontAndPointsImpl implements SaveFrontAndPoints {
    @Override
    public void save(List<Topic8Ex2.FrontAndPoint> fap, String filename) {
        String name = filename + ".txt";
        try (FileWriter fw = new FileWriter(name)) {
            for (int i = 0; i < fap.size(); i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(fap.get(i).getFront());
                sb.append(" ");
                for (Double dim : fap.get(i).getPoint().getCoords()) {
                    sb.append(dim);
                    sb.append(" ");
                }
                sb.append("\n");
                fw.append(sb.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}