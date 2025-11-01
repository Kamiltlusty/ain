package pl.kamil.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kamil.domain.algorithm.ls.LocalSearch;
import pl.kamil.domain.algorithm.ls.eval.func.LSEvalFunc;
import pl.kamil.domain.model.Point;
import pl.kamil.application.ports.DataExport;
import pl.kamil.infrastructure.services.DataProcessor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalSearchServiceTest {
    @Mock
    DataProcessor dp;
    @Mock
    DataExport txtExp;
    @Mock
    LSEvalFunc ef;
    @Mock
    LocalSearch ls;
    @Mock
    Point rightCornerPoint;

    @InjectMocks
    LocalSearchService lss;

    final int DIM = 2;
    final int EXEC_NUM = 100;
    final int SINGLE_LIST_ADD_INVOKE = 1;
    final double RIGHT_DOMAIN_CORNER = 10.0;
    final double EVAL_VALUE = 5.0;

    @Test
    @DisplayName("sprawdza dzialanie metody koordynującej wykonanie algorytmu i zapis do plikow")
    @SuppressWarnings("unchecked")
    void shouldExecuteFullLocalSearchAlgorithm() {
        // given
        when(ef.evalFunc(eq(rightCornerPoint))).thenReturn(EVAL_VALUE);
        var lfsListCaptor = ArgumentCaptor.forClass(List.class);
//        var mapCaptor = ArgumentCaptor.forClass(Map.class);
//        var saveListCaptor = ArgumentCaptor.forClass(List.class);
        // when
        lss.executeAlgorithm(DIM,
                EXEC_NUM,
                RIGHT_DOMAIN_CORNER,
                rightCornerPoint);

        // then
        verifyPointCalls();
        verify(ef, times(EXEC_NUM))
                .evalFunc(eq(rightCornerPoint));
        verifyLocalSearch(lfsListCaptor);
    }

    private void verifyPointCalls() {
        // weryfikuje wywołanie metod Punktu
        verify(rightCornerPoint, times(EXEC_NUM))
                .fillCoords(eq(DIM), eq(RIGHT_DOMAIN_CORNER));
        verify(rightCornerPoint, times(EXEC_NUM))
                .fromAnyToDomain();
    }

    @SuppressWarnings("unchecked")
    private void verifyLocalSearch(ArgumentCaptor<List> lfsListCaptor) {
        // weryfikuje wywolanie metody localFirstSearch oraz sprawdza zawartosc przekazanej listy
        verify(ls, times(EXEC_NUM))
                .localFirstSearch(eq(rightCornerPoint), eq(EVAL_VALUE), lfsListCaptor.capture());

        assertEquals(SINGLE_LIST_ADD_INVOKE, lfsListCaptor.getValue().size());
    }

//    @SuppressWarnings("unchecked")
//    private void verifySave(ArgumentCaptor<Map> mapCaptor, ArgumentCaptor<List> saveListCaptor) {
//        // sprawdza zapis 100 kolumn, wywołania uśrednienia oraz zapis uśrednionej 1 kolumny
//        verify(txtExp).save(mapCaptor.capture(),  anyString());
//
//        Map<Integer, List<Double>> resultMap = mapCaptor.getValue();
//        assertEquals(EXEC_NUM, mapCaptor.getValue().size());
//        assertEquals(EVAL_VALUE, resultMap.get(0).getFirst());
//
//        verify(dp).average100InvokesTo1(eq(resultMap));
//
//        verify(txtExp).save(saveListCaptor.capture(),  anyString());
//        assertEquals(1, saveListCaptor.getValue().size());
//    }
}