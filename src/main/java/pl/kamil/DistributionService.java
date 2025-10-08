package pl.kamil;

import java.util.Map;

public class DistributionService {
    private final DataGenerator gaussianGenerator;
    private final DataGenerator uniformGenerator;
    private final DataExport excelExport;

    public DistributionService(GaussianGenerator gg, UniformGenerator ug, ExcelExport ee) {
        this.gaussianGenerator = gg;
        this.uniformGenerator = ug;
        this.excelExport = ee;
    }

    public void createHistogramUniform(int N) {
        Map<String, Integer> data = uniformGenerator.generate(N);
        excelExport.save(data, "UNIFORM-DISTRIBUTION");
    }

    public void createHistogramGaussian(int N) {
        Map<String, Integer> data = gaussianGenerator.generate(N);
        excelExport.save(data, "GAUSSIAN-DISTRIBUTION");
    }
}
