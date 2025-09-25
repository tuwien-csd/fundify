package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;


import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.bson.Document;

public class ProgramMongoEntityQueryBuilder {
    private ETargetGroup targetGroup;
    private EAustrianState region;
    private FunderId funderId;
    private RisId risId;
    private ERegionalScope applicantsScope;
    private EPublicationStatus status;

    public ProgramMongoEntityQueryBuilder withTargetGroup(ETargetGroup targetGroup) {
        this.targetGroup = targetGroup;
        return this;
    }

    public ProgramMongoEntityQueryBuilder withRegion(EAustrianState region) {
        this.region = region;
        return this;
    }

    public ProgramMongoEntityQueryBuilder withFunderId(FunderId funderId) {
        this.funderId = funderId;
        return this;
    }

    public ProgramMongoEntityQueryBuilder withRisId(RisId risId) {
        this.risId = risId;
        return this;
    }

    public ProgramMongoEntityQueryBuilder withApplicantsScope(ERegionalScope applicantsScope) {
        this.applicantsScope = applicantsScope;
        return this;
    }

    public ProgramMongoEntityQueryBuilder withStatus(EPublicationStatus status) {
        this.status = status;
        return this;
    }

    public Document build() {
        List<Document> conditions = Stream.of(
                        toTargetGroupCondition(),
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

    private Optional<Document> toTargetGroupCondition() {
        return Optional.ofNullable(targetGroup)
                .map(group -> createInCondition("targetGroups", group));
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
}