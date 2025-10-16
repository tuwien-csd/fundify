package at.ac.tuwien.fundify.adapters.out.ris.network;

import at.ac.tuwien.fundify.adapters.common.ris.mapper.RisCallMapper;
import at.ac.tuwien.fundify.adapters.common.ris.mapper.RisProgramMapper;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisCall;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisProgramme;
import at.ac.tuwien.fundify.adapters.out.ris.network.client.GenericRisFundingRestClient;
import at.ac.tuwien.fundify.adapters.out.ris.network.config.RisClientConfiguration;
import at.ac.tuwien.fundify.application.port.out.ris.network.FundingRemoteRepository;
import at.ac.tuwien.fundify.application.port.out.ticketing.TicketingService;
import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.Program;
import at.ac.tuwien.fundify.domain.ticketing.AppendableTicketCreate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;

@JBossLog
@ApplicationScoped
public class RisSynergyNetworkAdapter implements FundingRemoteRepository {

  private final Map<String, GenericRisFundingRestClient> registeredRestClients;
  private static final String SYNC_ERROR_STANDARD_DESCRIPTION = "Could not process data provided by the RIS funding data provider.";
  private final static String SYNC_ERROR_KEY_TEMPLATE = "[SYNC_ERROR_%s] Invalid RIS Format";
  private final TicketingService ticketingService;

  @Inject
  RisSynergyNetworkAdapter(RisClientConfiguration config, TicketingService ticketingService) {
    this.registeredRestClients = config.getRegisteredRestClients();
    this.ticketingService = ticketingService;
  }


  @Override
  public List<Call> fetchAllCalls() {
    log.info("Fetching all calls");
    log.info("Registered REST clients: " + registeredRestClients.keySet());
    List<Call> calls = new ArrayList<>();
    for (Map.Entry<String, GenericRisFundingRestClient> entry : registeredRestClients.entrySet()) {
      log.info("Fetching calls from " + entry.getKey());
      calls.addAll(fetchCalls(entry.getValue(), entry.getKey()));
    }
    return calls;
  }

  @Override
  public List<Program> fetchAllPrograms() {
    log.info("Fetching all programs");
    log.info("Registered REST clients: " + registeredRestClients.keySet());
    List<Program> programs = new ArrayList<>();
    for (Map.Entry<String, GenericRisFundingRestClient> entry : registeredRestClients.entrySet()) {
      log.info("Fetching programs from " + entry.getKey());
      programs.addAll(fetchPrograms(entry.getValue(), entry.getKey()));
    }
    return programs;
  }


  @Retry(maxRetries = 3, delay = 5000)
  @Fallback(fallbackMethod = "fetchCallsFallback")
  List<Call> fetchCalls(GenericRisFundingRestClient client, String memberId) {
    try {
      List<RisFunding> calls = client.getFundings(RisFundingType.CALL);

      return calls.stream()
          .filter(it ->  RisFundingType.CALL == it.getType() ) //Some providers incorrectly return programs as calls
          .map(RisCall.class::cast)
          .map(f -> RisCallMapper.INSTANCE.toDomain(f, memberId))
          .toList();
    } catch (Exception e) {
      log.error(
          String.format("Error fetching calls from client '%s' (will retry)", client.getMemberId()),
          e);
      throw e;
    }
  }

  private List<Call> fetchCallsFallback(GenericRisFundingRestClient client, String memberId) {
    log.error(String.format(
        "All retries exhausted (or unrecoverable exception encountered) for fetchCalls for %s; returning empty list",
        client.getMemberId()));
    return new ArrayList<>();
  }

  // no test data available for ongoing calls from external API providers
  @Retry(maxRetries = 3, delay = 5000)
  @Fallback(fallbackMethod = "fetchOnGoingCallsFallback")
  List<Call> fetchOnGoingCalls(GenericRisFundingRestClient client, String memberId) {
    try {
      List<RisFunding> calls = client.getFundings(RisFundingType.ONGOING_CALL);

      return calls.stream()
          .filter(it ->  RisFundingType.CALL == it.getType() ) //Some providers incorrectly return programs as calls
          .map(RisCall.class::cast)
          .map(f -> RisCallMapper.INSTANCE.toDomain(f, memberId))
          .toList();
    } catch (Exception e) {
      log.error(String.format("Error fetching ongoing calls from client '%s' (will retry)",
          client.getMemberId()), e);
      throw e;
    }
  }

  private List<Call> fetchOnGoingCallsFallback(GenericRisFundingRestClient client,
      String memberId) {
    log.error(String.format(
        "All retries exhausted (or unrecoverable exception encountered) for fetchOnGoingCalls for client %s; returning empty list",
        client.getMemberId()));
    return new ArrayList<>();
  }

  @Retry(maxRetries = 3, delay = 5000, abortOn = ProcessingException.class)
  //no retries when we cannot parse the fetched data
  @Fallback(fallbackMethod = "fetchProgramsFallback")
  List<Program> fetchPrograms(GenericRisFundingRestClient client, String memberId) {
    try {
      List<RisFunding> programs = client.getFundings(RisFundingType.PROGRAMME);

      return programs.stream()
          .filter(it ->  RisFundingType.PROGRAMME == it.getType() ) //Some providers incorrectly return calls as programs
          .map(RisProgramme.class::cast)
          .map(f -> RisProgramMapper.INSTANCE.toDomain(f, memberId))
          .toList();
    } catch (ProcessingException e) {
      //Trigger notification
      createOrAppendSyncError(client, List.of(String.format("A new error occurred at: %s",
              OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
          "Detailed processing error: ", e.getMessage()));
      //Rethrow to let the fallback method handle it
      throw e;
    } catch (Exception e) {
      log.error(String.format("Error fetching programs from client %s (will retry)",
          client.getMemberId()), e);
      throw e;
    }
  }

  private List<Program> fetchProgramsFallback(GenericRisFundingRestClient client, String memberId) {
    log.error(
        String.format(
            "All retries exhausted (or unrecoverable exception encountered) for fetchPrograms for client %s; returning empty list",
            client.getMemberId()));
    return new ArrayList<>();
  }

  private void createOrAppendSyncError(GenericRisFundingRestClient client, List<String> texts) {
    var payload = new AppendableTicketCreate(
        String.format(SYNC_ERROR_KEY_TEMPLATE, client.getMemberId().toUpperCase()),
        SYNC_ERROR_STANDARD_DESCRIPTION, texts);
    ticketingService.createOrAppend(payload);
  }

  ;
}
