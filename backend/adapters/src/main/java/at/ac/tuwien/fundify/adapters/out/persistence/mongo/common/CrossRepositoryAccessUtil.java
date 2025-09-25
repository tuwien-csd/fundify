package at.ac.tuwien.fundify.adapters.out.persistence.mongo.common;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.AnnotatingFunderReferenceProjection;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.AnnotatingProgramReferenceProjection;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.CallPreviewProjection;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.UniversityMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.UniversityReferenceProjection;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.CallMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.CallReferenceProjection;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.FunderMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.FunderReferenceProjection;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.ProgramMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.ProgramReferenceProjection;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.*;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.*;
import org.bson.types.ObjectId;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CrossRepositoryAccessUtil {

    private static final String ID_IN_SET = "_id in ?1";
    private static final String ID = "_id";

    private CrossRepositoryAccessUtil() {
    }

    public static CallPreviewProjection findCallPreviewProjection(ObjectId callId) {
        return callId == null ? null : CallMongoEntity.find(ID, callId).project(CallPreviewProjection.class).firstResult();
    }

    public static FunderReferenceProjection findFunderEntityProjection(ObjectId funderId) {
        return funderId == null ? null : FunderMongoEntity.find(ID, funderId).project(FunderReferenceProjection.class).firstResult();
    }

    public static UniversityReferenceProjection findUniversityReferenceProjection(ObjectId universityId) {
        if (universityId == null) {
            return null;
        }
        return UniversityMongoEntity
                .find(ID, universityId)
                .project(UniversityReferenceProjection.class)
                .firstResult();
    }

    public static Map<ObjectId, UniversityReferenceProjection> findUniversityReferenceProjections(Set<ObjectId> universityIds) {
        return UniversityMongoEntity.find(ID_IN_SET, universityIds)
                .project(UniversityReferenceProjection.class)
                .stream()
                .collect(Collectors.toMap(UniversityReferenceProjection::_id, Function.identity()));
    }

    public static Map<ObjectId, FunderReferenceProjection> findFunderEntityProjections(Set<ObjectId> funderIds) {
        return FunderMongoEntity.find(ID_IN_SET, funderIds)
                .project(FunderReferenceProjection.class)
                .stream()
                .collect(Collectors.toMap(FunderReferenceProjection::_id, Function.identity()));
    }

    public static Map<ObjectId, CallReferenceProjection> findCallEntityProjections(Set<ObjectId> callIds) {
        return CallMongoEntity.find(ID_IN_SET, callIds)
                .project(CallReferenceProjection.class)
                .stream()
                .collect(Collectors.toMap(CallReferenceProjection::_id, Function.identity()));
    }

    public static CallReferenceProjection findCallEntityProjection(ObjectId callId) {
        return callId == null ? null : CallMongoEntity.find(ID, callId).project(CallReferenceProjection.class).firstResult();
    }

    public static ProgramReferenceProjection findProgramEntityProjection(ObjectId programId) {
        return programId == null ? null : ProgramMongoEntity.find(ID, programId).project(ProgramReferenceProjection.class).firstResult();
    }

    public static Map<ObjectId, ProgramReferenceProjection> findProgramEntityProjections(Set<ObjectId> programIds) {
        return ProgramMongoEntity.find(ID_IN_SET, programIds)
                .project(ProgramReferenceProjection.class)
                .stream()
                .collect(Collectors.toMap(ProgramReferenceProjection::_id, Function.identity()));
    }

    public static AnnotatingFunderReferenceProjection findAnnotatingFunderEntityProjection(ObjectId funderId) {
        return funderId == null ? null : FunderMongoEntity.find(ID, funderId)
                .project(AnnotatingFunderReferenceProjection.class)
                .firstResult();
    }
    
    public static Map<ObjectId, AnnotatingFunderReferenceProjection> findAnnotatingFunderEntityProjections(Set<ObjectId> funderIds) {
        return FunderMongoEntity.find(ID_IN_SET, funderIds)
                .project(AnnotatingFunderReferenceProjection.class)
                .stream()
                .collect(Collectors.toMap(AnnotatingFunderReferenceProjection::_id, Function.identity()));
    }

    public static AnnotatingProgramReferenceProjection findAnnotatingProgramEntityProjection(ObjectId programId) {
        return programId == null ? null : ProgramMongoEntity.find(ID, programId)
                .project(AnnotatingProgramReferenceProjection.class)
                .firstResult();
    }

    public static Map<ObjectId, AnnotatingProgramReferenceProjection> findAnnotatingProgramEntityProjections(Set<ObjectId> programIds) {
        return ProgramMongoEntity.find(ID_IN_SET, programIds)
                .project(AnnotatingProgramReferenceProjection.class)
                .stream()
                .collect(Collectors.toMap(AnnotatingProgramReferenceProjection::_id, Function.identity()));
    }
}