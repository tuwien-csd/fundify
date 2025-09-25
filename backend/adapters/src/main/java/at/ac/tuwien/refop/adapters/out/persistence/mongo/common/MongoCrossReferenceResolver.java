package at.ac.tuwien.refop.adapters.out.persistence.mongo.common;

import at.ac.tuwien.refop.adapters.out.persistence.mongo.annotating.*;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.funding.*;
import at.ac.tuwien.refop.domain.annotating.AnnotatingFunderReference;
import at.ac.tuwien.refop.domain.annotating.AnnotatingProgramReference;
import at.ac.tuwien.refop.domain.annotating.University;
import at.ac.tuwien.refop.domain.annotating.UniversityReference;
import at.ac.tuwien.refop.domain.dto.CallDTO;
import at.ac.tuwien.refop.domain.funding.Funder;
import at.ac.tuwien.refop.domain.funding.FunderReference;
import at.ac.tuwien.refop.domain.funding.ProgramReference;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class MongoCrossReferenceResolver {

    private static final String ID = "_id";
    private static final String ID_IN = "_id in ?1";

    public CallDTO resolveCall(ObjectId callId) {
        if (callId == null) {
            return null;
        }
        CallMongoEntity callMongoEntity = CallMongoEntity.findById(callId);
        return CallMongoEntityMapper.INSTANCE.toDTO(callMongoEntity, this);
    }

    public FunderReference resolveFunderReference(ObjectId funderId) {
        if (funderId == null) {
            return null;
        }
        FunderReferenceProjection funderReferenceProjection = FunderMongoEntity.find(ID, funderId)
                .project(FunderReferenceProjection.class).firstResult();

        return FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(funderReferenceProjection);
    }

    public Funder resolveFunder(ObjectId funderId) {
        if (funderId == null) {
            return null;
        }
        FunderMongoEntity funderMongoEntity = FunderMongoEntity
                .find(ID, funderId)
                .firstResult();

        return FunderMongoEntityMapper.INSTANCE.toDomain(funderMongoEntity);
    }

    public List<FunderReference> resolveFunderReferences(List<ObjectId> funderIds) {
        if (funderIds == null) {
            return Collections.emptyList();
        }
        List<FunderReferenceProjection> funderReferenceProjections = FunderMongoEntity.find(ID_IN, funderIds)
                .project(FunderReferenceProjection.class).list();

        return FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(funderReferenceProjections);
    }

    public AnnotatingFunderReference resolveAnnotatingFunderReference(ObjectId funderId) {
        if (funderId == null) {
            return null;
        }
        AnnotatingFunderReferenceProjection funderReferenceProjection = FunderMongoEntity.find(ID, funderId)
                .project(AnnotatingFunderReferenceProjection.class).firstResult();

        return AnnotatingReferenceProjectionMapper.INSTANCE.toAnnotatingFunderReference(funderReferenceProjection);
    }

    public ProgramReference resolveProgramReference(ObjectId programId) {
        if (programId == null) {
            return null;
        }
        ProgramReferenceProjection programReferenceProjection = ProgramMongoEntity.find(ID, programId)
                .project(ProgramReferenceProjection.class).firstResult();

        return ProgramReferenceProjectionMapper.INSTANCE.toFundingDomainUnresolved(programReferenceProjection);
    }

    public AnnotatingProgramReference resolveAnnotatingProgramReference(ObjectId programId) {
        if (programId == null) {
            return null;
        }
        AnnotatingProgramReferenceProjection progReferenceProjection = ProgramMongoEntity.find(ID, programId)
                .project(AnnotatingProgramReferenceProjection.class).firstResult();

        return AnnotatingReferenceProjectionMapper.INSTANCE.toAnnotatingProgramReference(progReferenceProjection);
    }

    public UniversityReference resolveUniversityReference(ObjectId universityId) {
        if (universityId == null) {
            return null;
        }
        UniversityReferenceProjection universityReferenceProjection = UniversityMongoEntity
                .find(ID, universityId)
                .project(UniversityReferenceProjection.class)
                .firstResult();

        return UniversityReferenceProjectionMapper.INSTANCE.toDomain(universityReferenceProjection);
    }

    public University resolveUniversity(ObjectId universityId) {
        if (universityId == null) {
            return null;
        }
        UniversityMongoEntity universityMongoEntity = UniversityMongoEntity
                .find(ID, universityId)
                .firstResult();

        return UniversityMongoEntityMapper.INSTANCE.toDomain(universityMongoEntity);
    }

}