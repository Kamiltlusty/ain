package pl.kamil;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniformGeneratorTest {
    @Mock
    private RandomNumbers rn;

    @InjectMocks
    private UniformGenerator ug;

    @Test
    void givenValAndN_whenInvokeGenerate_shouldGroupAllResultsInSameBin() {
        // given
        int N = 3;
        double value = 5.5;
        when(rn.nextDouble(anyDouble()))
                .thenReturn(value);
        // when
        Map<String, Integer> actual = ug.generate(N);
        // then
        int expectedSize = 1;
        String expectedKey = "5,0:6,0";
        Integer expectedValue = 3;

        assertEquals(expectedSize, actual.size());
        assertTrue(actual.containsKey(expectedKey));
        assertEquals(actual.get(expectedKey), expectedValue);
    }
}