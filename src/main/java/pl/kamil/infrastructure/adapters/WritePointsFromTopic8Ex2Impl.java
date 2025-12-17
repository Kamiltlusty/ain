package pl.kamil.infrastructure.adapters;

import pl.kamil.application.ports.WritePointsFromTopic8Ex2;
import pl.kamil.domain.model.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WritePointsFromTopic8Ex2Impl implements WritePointsFromTopic8Ex2 {
    @Override
    public List<Point> write(String fileName) {
        List<Point> points = new ArrayList<>();
        String name = "src/main/resources/" + fileName + ".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(name))) {
            String line = br.readLine();
            while (line != null) {
                String[] arr = line.split("\t");
                List<Double> coords = List.of(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
                Point point = new Point();
                point.setCoords(coords);

                points.add(point);
                line = br.readLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return points;
    }
}
