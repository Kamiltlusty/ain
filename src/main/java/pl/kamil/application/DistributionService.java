package pl.kamil.application;

import pl.kamil.domain.service.DataGenerator;
import pl.kamil.domain.service.GaussianGenerator;
import pl.kamil.domain.service.UniformGenerator;
import pl.kamil.application.ports.DataExport;
import pl.kamil.infrastructure.adapters.ExcelExport;
import pl.kamil.infrastructure.adapters.TXTExport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DistributionService {
    private final DataGenerator gaussianGenerator;
    private final DataGenerator uniformGenerator;
//    private final DataExport excelExport;
    private final DataExport txtExport;

    public DistributionService(GaussianGenerator gg, UniformGenerator ug, ExcelExport ee, TXTExport txtExport) {
        this.gaussianGenerator = gg;
        this.uniformGenerator = ug;
//        this.excelExport = ee;
        this.txtExport = txtExport;
    }

    public void createHistogramUniform(int N1, int N2, int N3) {
//        Map<String, Integer> data = uniformGenerator.generate(N);
//        excelExport.save(data, "UNIFORM-DISTRIBUTION");
        List<Double> doubles = uniformGenerator.generateTXT(N1);
        List<Double> doubles1 = uniformGenerator.generateTXT(N2);
        List<Double> doubles2 = uniformGenerator.generateTXT(N3);
        txtExport.save(doubles, doubles1, doubles2, "UNIFORM-DISTRIBUTION");
    }

    public void createHistogramGaussian(int N1, int N2, int N3) {
//        Map<String, Integer> data = gaussianGenerator.generate(N);
//        excelExport.save(data, "GAUSSIAN-DISTRIBUTION");
        List<Double> doubles = gaussianGenerator.generateTXT(N1);
        List<Double> doubles1 = gaussianGenerator.generateTXT(N2);
        List<Double> doubles2 = gaussianGenerator.generateTXT(N3);
        txtExport.save(doubles, doubles1, doubles2, "GAUSSIAN-DISTRIBUTION");
    }

    public double randomNumber (double min, double max) {
        double r = Math.random() * (max - min) + min;
        return r;
    }

    public void monteCarlo(double R, int n, String file) {
        int n_inside = 0;
        double R2 = R * R;
        double x;
        double y;
        double approximate_S;
        double S;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            for(int i = 0; i < n; ++i) {
                x = randomNumber((-1) * R, R);
                y = randomNumber((-1) * R, R);
                if(x * x + y * y <= R2) {
                    ++n_inside;
                }
                writer.write(x + " ");
                writer.write(y + " ");
                writer.newLine();
            }

            approximate_S = 4.0 * R2 * n_inside / (double)n;
            S = Math.PI * R * R;

            System.out.println("Pole " + S);
            System.out.println("Przyblizone pole " + approximate_S);

            writer.write(R + " ");
            writer.write(S + " ");
            writer.write(approximate_S + " ");
            writer.write(n + " ");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
