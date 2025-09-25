package at.ac.tuwien.refop.adapters.out.ris.network;

import at.ac.tuwien.refop.application.port.in.ris.network.SyncExternalFundingsUseCase;
import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
@JBossLog
@IfBuildProperty(name="fundify.external-fundings.sync-enabled", stringValue = "true")
public class DatabaseSyncScheduler {

    private final SyncExternalFundingsUseCase syncExternalFundingsUseCase;

    @ConfigProperty(name = "fundify.external-fundings.run-sync-on-startup", defaultValue = "false")
    Boolean runSyncOnStartup;

    public DatabaseSyncScheduler(SyncExternalFundingsUseCase syncDatabaseUseCase) {
        this.syncExternalFundingsUseCase = syncDatabaseUseCase;
    }

    // Trigger synchronization once at application startup
    void onStart(@Observes StartupEvent ev) {
      if (runSyncOnStartup) {
        log.info("Triggering initial synchronization on startup.");
        synchronizeExternalFundingData();
      }
    }

    /**
     * Synchronized funding data from ris network according to the time specified as cron expression.
     * Synchronization is done in two steps:
     *   1. Synchronize external programs
     *   2. Synchronize external calls
     * It is configurable in application.properties.
     * By default, the syntax used for cron expressions is based on Quartz.
     */
    @Scheduled(cron = "{cron.expr.sync.funding}")
    public void synchronizeExternalFundingData() {
        log.info("Starting job: Update funding data from external sources");
        log.info("Synchronizing external programs...");
        syncExternalFundingsUseCase.synchronizeExternalPrograms();
        log.info("... finished.");
        log.info("Synchronizing external calls...");
        syncExternalFundingsUseCase.synchronizeExternalCalls();
        log.info("... finished.");
        log.info("Job finished: Update funding data from external sources");
    }
}