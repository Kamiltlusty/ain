package pl.kamil.infrastructure.adapters;

import pl.kamil.application.ports.SaveNonDominatedAndDominatedPoints;
import pl.kamil.domain.model.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SaveNonDominatedAndDominatedPointsImpl implements SaveNonDominatedAndDominatedPoints {
    @Override
    public void save(List<Point> nDom,
                     List<Point> dom,
                     String filename,
                     boolean append) {

        String name = filename + ".txt";
        try (FileWriter fw = new FileWriter(name, append)) {
            // zapis niezdominowanych
            for (int j = 0; j < nDom.get(0).getCoords().size(); j++) {
                fw.write("ndom:wymiar:" + (j+1) + " ");
            }
            fw.write("\n");
            for (int i = 0; i < nDom.size(); i++) {
                StringBuilder sb = new StringBuilder();
                for (Double dim : nDom.get(i).getCoords()) {
                    sb.append(dim);
                    sb.append(" ");
                }
                sb.append("\n");
                fw.append(sb.toString());
            }
            // zapis zdominowanych
            for (int j = 0; j < nDom.get(0).getCoords().size(); j++) {
                fw.write("dom:wymiar:" + (j+1) + " ");
            }
            fw.write("\n");
            for (int i = 0; i < dom.size(); i++) {
                StringBuilder sb = new StringBuilder();
                for (Double dim : dom.get(i).getCoords()) {
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
