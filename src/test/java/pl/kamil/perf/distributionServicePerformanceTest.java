//package pl.kamil.perf;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//
//import java.util.logging.Logger;
//
//public class distributionServicePerformanceTest {
//    private DataGeneratorService dg;
//    private final static Logger logger = Logger.getLogger("UniformDistributionPerformanceTest.class");
//
//    @BeforeEach
//    public void setUp() {
//        dg = new DataGeneratorService();
//    }
//
//    @ParameterizedTest
//    @ValueSource(ints = {10, 100_000, 1_000_000})
//    public void testExecutionTimeForDifferentN(int N) {
//        long start = System.nanoTime();
//        dg.generateUniform(N);
//        long end = System.nanoTime();
//        logger.info("Execution time for N=" + N + ": " + (end - start) + " ns");
//    }
//}
