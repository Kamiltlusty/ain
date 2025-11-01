package pl.kamil.application.usecases;

import pl.kamil.domain.model.Point;
import pl.kamil.domain.service.LocalSearchService;
import pl.kamil.application.ports.DataExport;
import pl.kamil.infrastructure.services.DataProcessor;

import java.util.List;

public class LocalSearchUseCase {
    private final DataProcessor dp;
    private final DataExport de;
    private final LocalSearchService lss;

    public LocalSearchUseCase(DataProcessor dp, DataExport de, LocalSearchService lss) {
        this.dp = dp;
        this.de = de;
        this.lss = lss;
    }

    public void coordinateAlgorithmAndSave(Integer dim, int execNum, double rightDomainCorner, Point rightCornerPoint) {
        var fxResults = lss.executeAlgorithm(dim, execNum, rightDomainCorner, rightCornerPoint);
        de.save(fxResults, "LOCAL_SEARCH");

        List<Double> average = dp.average100InvokesTo1(fxResults);
        de.save(average, "LOCAL_SEARCH_AVERAGE");
    }
}
