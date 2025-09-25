package at.ac.tuwien.refop.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.refop.domain.funding.vo.Identifier;
import at.ac.tuwien.refop.domain.funding.vo.enums.EEntryOrigin;
import at.ac.tuwien.refop.domain.funding.vo.enums.EIdentifierType;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.jbosslog.JBossLog;

import java.util.ArrayList;
import java.util.stream.Stream;

@ApplicationScoped
@JBossLog
public class CopyRisIdInIdentifiersInMongoEntities {


    /**
     * One time job scheduled to run on 25.11.2024 at 14h:29m:30s
     *
     * The job takes the existing value of 'risId' property in funders and in created via fundify GUI calls and programs,
     * and adds 'identifier' of ris type with this value into the 'identifiers' property.
     */
    @Scheduled(cron = "30 29 14 25 11 ? 2024")
    public void addRisIdIntoIdentifiersIfMissing() {
        log.info("[CopyRisIdInIdentifiersInMongoEntities] Starting job: addRisIdIntoIdentifiersIfMissing");

        Stream<CallMongoEntity> calls = CallMongoEntity.find("entryOrigin", EEntryOrigin.REFOP).stream();
        calls.forEach( call -> {
            if (call.risId != null) {
                call.identifiers = new ArrayList<>();
                call.identifiers.add(new Identifier(EIdentifierType.RIS_SYNERGY, call.risId));
                call.update();
            }
        });

        Stream<ProgramMongoEntity> programs = ProgramMongoEntity.find("entryOrigin", EEntryOrigin.REFOP).stream();
        programs.forEach( program -> {
            if (program.risId != null) {
                program.identifiers = new ArrayList<>();
                program.identifiers.add(new Identifier(EIdentifierType.RIS_SYNERGY, program.risId));
                program.update();
            }
        });

        Stream<FunderMongoEntity> funders = FunderMongoEntity.findAll().stream();
        funders.forEach( funder -> {
            if (funder.risId != null) {
                funder.identifiers = new ArrayList<>();
                funder.identifiers.add(new Identifier(EIdentifierType.RIS_SYNERGY, funder.risId));
                funder.update();
            }
        });

        log.info("[CopyRisIdInIdentifiersInMongoEntities] Job finished: addRisIdIntoIdentifiersIfMissing");
    }
}
