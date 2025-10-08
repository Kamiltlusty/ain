package pl.kamil;

public class Main {
    public static void main(String[] args) {
        var rn = new RandomlyGeneratedNumbers();
        var uds = new DistributionService(
                new GaussianGenerator(),
                new UniformGenerator(rn),
                new ExcelExport());

        uds.createHistogramUniform(1_000_000);
        uds.createHistogramGaussian(1_000_000);
    }
}