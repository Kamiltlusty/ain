package pl.kamil;

public class Main {
    public static void main(String[] args) {
        DistributionService uds = new DistributionService(
                new GaussianGenerator(),
                new UniformGenerator(),
                new ExcelExport());

        uds.createHistogramUniform(1_000_000);
        uds.createHistogramGaussian(1_000_000);
    }
}