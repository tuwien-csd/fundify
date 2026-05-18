package at.ac.tuwien.fundify.adapters.out.eutender.network.client;

import at.ac.tuwien.fundify.adapters.common.eutender.mapper.EuTenderToRisMapper;
import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderResponse;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.fundify.adapters.out.ris.network.client.GenericRisFundingRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@JBossLog
@ApplicationScoped
public class EuTenderRestClient implements GenericRisFundingRestClient {

    private static final int PAGE_SIZE = 10;
    private static final int MAX_PARALLEL_REQUESTS = 10;

    @Inject
    @RestClient
    EuTenderHttpRestClient httpClient;

    @Override
    public List<RisFunding> getFundings(RisFundingType fundingType) {
        if (fundingType == RisFundingType.PROGRAMME) {
            return Collections.emptyList();
        }
        log.info("Fetching page 1 of EuTender results");
        EuTenderResponse first = httpClient.search(1, PAGE_SIZE, new EuTenderRequest());
        int totalResults = first.getTotalResults();
        int totalPages = (int) Math.ceil((double) totalResults / PAGE_SIZE);
        log.info("EuTender: " + totalResults + " total results, fetching " + totalPages + " pages in parallel");

        List<RisFunding> firstPage = first.getResults().stream()
            .map(EuTenderToRisMapper.INSTANCE::toRisCall)
            .toList();

        if (totalPages <= 1) {
            return new ArrayList<>(firstPage);
        }

        ExecutorService executor = Executors.newFixedThreadPool(MAX_PARALLEL_REQUESTS);
        List<CompletableFuture<List<RisFunding>>> futures = IntStream.rangeClosed(2, totalPages)
            .mapToObj(page -> CompletableFuture.supplyAsync(() -> {
                log.info("Fetching page " + page + " of EuTender results");
                return httpClient.search(page, PAGE_SIZE, new EuTenderRequest()).getResults().stream()
                    .map(EuTenderToRisMapper.INSTANCE::toRisCall)
                    .toList();
            }, executor))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        List<RisFunding> all = new ArrayList<>(firstPage);
        for (CompletableFuture<List<RisFunding>> future : futures) {
            try {
                all.addAll(future.get());
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch EuTender page", e);
            }
        }
        return all;
    }

    @Override
    public RisFunding getFunding(String id) {
        throw new UnsupportedOperationException("EuTender does not support fetching a single funding by ID");
    }

    @Override
    public String getMemberId() {
        return "eutender";
    }
}
