package at.ac.tuwien.fundify.adapters.out.eutender.network.client;

import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderMetadata;
import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderResponse;
import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderResult;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EuTenderRestClientTest {

    @Mock
    EuTenderHttpRestClient httpClient;

    @InjectMocks
    EuTenderRestClient client;

    private EuTenderResult minimalResult(String title) {
        EuTenderMetadata meta = new EuTenderMetadata();
        meta.setTitle(List.of(title));
        EuTenderResult result = new EuTenderResult();
        result.setMetadata(meta);
        return result;
    }

    private EuTenderResponse responseWith(int totalResults, List<EuTenderResult> results) {
        EuTenderResponse resp = new EuTenderResponse();
        resp.setTotalResults(totalResults);
        resp.setResults(results);
        return resp;
    }


    @Test
    void getMemberId_returnsEutender() {
        assertEquals("eutender", client.getMemberId());
    }

    @Test
    void getFundings_withProgrammeType_returnsEmptyWithoutCallingApi() {
        List<RisFunding> result = client.getFundings(RisFundingType.PROGRAMME);

        assertTrue(result.isEmpty());
        verifyNoInteractions(httpClient);
    }

    @Test
    void getFundings_withSinglePage_returnsMappedResults() {
        EuTenderResult item = minimalResult("Single Page Call");
        when(httpClient.search(eq(1), anyInt(), any(EuTenderRequest.class)))
            .thenReturn(responseWith(1, List.of(item)));

        List<RisFunding> result = client.getFundings(RisFundingType.CALL);

        assertEquals(1, result.size());
        verify(httpClient, times(1)).search(anyInt(), anyInt(), any(EuTenderRequest.class));
    }

    @Test
    void getFundings_withExactlyOnePage_doesNotFetchMorePages() {
        List<EuTenderResult> items = List.of(minimalResult("Call A"), minimalResult("Call B"));
        // 2 results with page size 50 → only 1 page
        when(httpClient.search(eq(1), anyInt(), any(EuTenderRequest.class)))
            .thenReturn(responseWith(2, items));

        List<RisFunding> result = client.getFundings(RisFundingType.CALL);

        assertEquals(2, result.size());
        verify(httpClient, times(1)).search(anyInt(), anyInt(), any(EuTenderRequest.class));
    }

    @Test
    void getFundings_withEmptyFirstPage_returnsEmptyList() {
        when(httpClient.search(eq(1), anyInt(), any(EuTenderRequest.class)))
            .thenReturn(responseWith(0, Collections.emptyList()));

        List<RisFunding> result = client.getFundings(RisFundingType.CALL);

        assertTrue(result.isEmpty());
        verify(httpClient, times(1)).search(anyInt(), anyInt(), any(EuTenderRequest.class));
    }


    @Test
    void getFundings_withMultiplePages_fetchesAllPagesAndCombines() {
        // 75 total results → 2 pages of 50
        when(httpClient.search(eq(1), anyInt(), any(EuTenderRequest.class)))
            .thenReturn(responseWith(75, List.of(minimalResult("Page1-Call"))));
        when(httpClient.search(eq(2), anyInt(), any(EuTenderRequest.class)))
            .thenReturn(responseWith(75, List.of(minimalResult("Page2-Call"))));

        List<RisFunding> result = client.getFundings(RisFundingType.CALL);

        assertEquals(2, result.size());
        verify(httpClient, times(1)).search(eq(1), anyInt(), any(EuTenderRequest.class));
        verify(httpClient, times(1)).search(eq(2), anyInt(), any(EuTenderRequest.class));
    }

    @Test
    void getFundings_withThreePages_fetchesAllThree() {
        // 101 total results → 3 pages of 50
        when(httpClient.search(eq(1), anyInt(), any(EuTenderRequest.class)))
            .thenReturn(responseWith(101, List.of(minimalResult("P1"))));
        when(httpClient.search(eq(2), anyInt(), any(EuTenderRequest.class)))
            .thenReturn(responseWith(101, List.of(minimalResult("P2"))));
        when(httpClient.search(eq(3), anyInt(), any(EuTenderRequest.class)))
            .thenReturn(responseWith(101, List.of(minimalResult("P3"))));

        List<RisFunding> result = client.getFundings(RisFundingType.CALL);

        assertEquals(3, result.size());
        verify(httpClient, times(1)).search(eq(1), anyInt(), any(EuTenderRequest.class));
        verify(httpClient, times(1)).search(eq(2), anyInt(), any(EuTenderRequest.class));
        verify(httpClient, times(1)).search(eq(3), anyInt(), any(EuTenderRequest.class));
    }

    @Test
    void getFundings_withOngoingCallType_alsoFetchesFromApi() {
        when(httpClient.search(eq(1), anyInt(), any(EuTenderRequest.class)))
            .thenReturn(responseWith(1, List.of(minimalResult("Ongoing"))));

        List<RisFunding> result = client.getFundings(RisFundingType.ONGOING_CALL);

        assertFalse(result.isEmpty());
        verify(httpClient, atLeastOnce()).search(anyInt(), anyInt(), any(EuTenderRequest.class));
    }
}
