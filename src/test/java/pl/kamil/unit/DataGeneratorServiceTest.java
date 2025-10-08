package pl.kamil.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataGeneratorServiceTest {
    private DataGeneratorService dg;

    @BeforeEach
    void setUp() {
        dg = new DataGeneratorService();
    }

//    @Test
//    void givenN_whenGenerateInvoked_shouldGenerate10binsUniformDistribution() {
//        // given
//        int N = 10;
//        // when
//        Map<String, Integer> data = dg.generate(1_000_000);
//        // then
//        assertEquals(N, data.size());
//    }

    @Test
    void givenNegativeOr0N_whenGenerateUniformInvoked_shouldThrowIllegalArgumentException() {
        // given
        int N = -1;
        // when, then
        assertThrows(IllegalArgumentException.class, () -> dg.generateUniform(N));
    }
}