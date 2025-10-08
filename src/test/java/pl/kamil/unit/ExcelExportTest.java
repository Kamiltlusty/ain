package pl.kamil.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kamil.ExcelExport;

import java.util.List;

public class ExcelExportTest {
    private ExcelExport ees;

    @BeforeEach
    public void setUp() {
        ees = new ExcelExport();
    }

    @Test
    void givenData_whenSaveInvoked_thenSaveDataToXlsx() {
        // given
        List<Double> data = List.of(0.12351, 0.42656, 0.56376, 0.55263, 0.68595, 0.87978);
        // when
//        ees.save(data);
        // then

    }
}
