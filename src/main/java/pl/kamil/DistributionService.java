package pl.kamil;

import java.util.List;
import java.util.Map;

public class DistributionService {
    private final DataGenerator gaussianGenerator;
    private final DataGenerator uniformGenerator;
    private final DataExport excelExport;
    private final DataExport txtExport;

    public DistributionService(GaussianGenerator gg, UniformGenerator ug, ExcelExport ee, TXTExport txtExport) {
        this.gaussianGenerator = gg;
        this.uniformGenerator = ug;
        this.excelExport = ee;
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
}
