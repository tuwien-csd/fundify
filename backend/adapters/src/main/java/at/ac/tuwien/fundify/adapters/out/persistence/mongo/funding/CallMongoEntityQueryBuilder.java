package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.bson.Document;

public class CallMongoEntityQueryBuilder {
    private ECallType callType;
    private ETargetGroup targetGroup;
    private Boolean runningCalls;
    private EAustrianState region;
    private FunderId funderId;
    private RisId risId;
    private ERegionalScope applicantsScope;
    private EPublicationStatus status;

    public CallMongoEntityQueryBuilder withCallType(ECallType callType) {
        this.callType = callType;
        return this;
    }

    public CallMongoEntityQueryBuilder withTargetGroup(ETargetGroup targetGroup) {
        this.targetGroup = targetGroup;
        return this;
    }

    public CallMongoEntityQueryBuilder withRunningCalls(Boolean runningCalls) {
        this.runningCalls = runningCalls;
        return this;
    }

    public CallMongoEntityQueryBuilder withRegion(EAustrianState region) {
        this.region = region;
        return this;
    }

    public CallMongoEntityQueryBuilder withFunderId(FunderId funderId) {
        this.funderId = funderId;
        return this;
    }

    public CallMongoEntityQueryBuilder withRisId(RisId risId) {
        this.risId = risId;
        return this;
    }

    public CallMongoEntityQueryBuilder withApplicantsScope(ERegionalScope applicantsScope) {
        this.applicantsScope = applicantsScope;
        return this;
    }

    public CallMongoEntityQueryBuilder withStatus(EPublicationStatus status) {
        this.status = status;
        return this;
    }

    public Document build() {
        List<Document> conditions = Stream.of(
                        toCallTypeCondition(),
                        toTargetGroupCondition(),
                        toRunningCallsCondition(),
                        toRegionCondition(),
                        toFunderCondition(),
                        toRisIdCondition(),
                        toApplicantsScopeCondition(),
                        toStatusCondition()
                )
                .flatMap(Optional::stream)
                .toList();

        return conditions.isEmpty()
                ? new Document()
                : new Document("$and", conditions);
    }

    private Optional<Document> toCallTypeCondition() {
        return Optional.ofNullable(callType)
                .map(type -> createSimpleCondition("fundingType", type));
    }

    private Optional<Document> toTargetGroupCondition() {
        return Optional.ofNullable(targetGroup)
                .map(group -> createInCondition("targetGroups", group));
    }

    private Optional<Document> toRunningCallsCondition() {
        return Optional.ofNullable(runningCalls)
                .filter(Boolean::booleanValue)
                .map(isRunning -> {
                    Document ongoingCallCondition = createSimpleCondition("fundingType", ECallType.ONGOING_CALL);
                    Document activeStageCondition = createActiveCallStageCondition();
                    return new Document("$or", Arrays.asList(ongoingCallCondition, activeStageCondition));
                });
    }

    private Optional<Document> toRegionCondition() {
        return Optional.ofNullable(region)
                .map(region -> createInCondition("eligibleApplicantsRegions", region));
    }

    private Optional<Document> toFunderCondition() {
        return Optional.ofNullable(funderId)
                .map(id -> createSimpleCondition("funderId", ObjectIdUtils.toObjectId(id.value())));
    }

    private Optional<Document> toRisIdCondition() {
        return Optional.ofNullable(risId)
                .map(id -> createSimpleCondition("risId", risId.toString()));
    }

    private Optional<Document> toApplicantsScopeCondition() {
        return Optional.ofNullable(applicantsScope)
                .map(scope -> createSimpleCondition("eligibleApplicantsScope", scope));
    }

    private Optional<Document> toStatusCondition() {
        return Optional.ofNullable(status)
                .map(status -> createSimpleCondition("status", status));
    }

    private Document createSimpleCondition(String field, Object value) {
        return new Document(field, value);
    }

    private Document createInCondition(String field, Object value) {
        return new Document(field, new Document("$in", Collections.singletonList(value)));
    }

    private Document createActiveCallStageCondition() {
        LocalDateTime now = LocalDateTime.now();
        return new Document("$and", Arrays.asList(
                new Document("callStages.0.duration.start", new Document("$lt", now)),
                new Document("callStages.0.duration.end", new Document("$gt", now))
        ));
    }
}