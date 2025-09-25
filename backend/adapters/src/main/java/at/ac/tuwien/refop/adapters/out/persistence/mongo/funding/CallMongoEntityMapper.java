package at.ac.tuwien.refop.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.refop.adapters.in.rest.mapper.CommonIdMapper;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.CommonReferenceMapper;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.refop.domain.common.CallOwner;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.dto.CallDTO;
import at.ac.tuwien.refop.domain.funding.Call;
import at.ac.tuwien.refop.domain.funding.CallUpdate;
import at.ac.tuwien.refop.domain.funding.FunderReference;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CommonIdMapper.class,
        MongoEntityIdMapper.class,
        CommonReferenceMapper.class,
        FunderReferenceProjectionMapper.class
})
public interface CallMongoEntityMapper {

    CallMongoEntityMapper INSTANCE = Mappers.getMapper(CallMongoEntityMapper.class);

    @Mapping(target = "externalIdentifier.risId", source = "risId")
    @Mapping(target = "externalIdentifier.identifiers", source = "identifiers")
    @Mapping(target = "funder", source = "funderId")
    @Mapping(target = "partOf", source = "partOfId")
    @Mapping(target = "jointCallPartner", source = "otherJointCallpartner")
    @Mapping(target = "callOwner", source = "." , qualifiedByName = "resolveCallOwner")
    Call toDomain(CallMongoEntity callMongoEntity, @Context MongoCrossReferenceResolver resolver);
    List<Call> toDomain(List<CallMongoEntity> callMongoEntity, @Context MongoCrossReferenceResolver resolver);

    @AfterMapping
    default void postProcess(
            CallMongoEntity callMongoEntity,
            @MappingTarget Call call
    ) {
        if (callMongoEntity.registeredJointCallPartnerIds == null || callMongoEntity.registeredJointCallPartnerIds.isEmpty()) {
            return;
        }
        List<FunderReference> jointCallPartner = call.getJointCallPartner();
        callMongoEntity.registeredJointCallPartnerIds.forEach(
                registeredJointCallPartnerId -> {
                    FunderReference funderReference = new FunderReference(
                            new FunderId(registeredJointCallPartnerId.toHexString()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                    );
                    jointCallPartner.add(funderReference);
                });
        call.setJointCallPartner(jointCallPartner);
    }


    @Mapping(target = "funder", source ="funderId")
    @Mapping(target = "partOf", source = "partOfId")
    @Mapping(target = "jointCallPartner", source = ".", qualifiedByName = "resolveJointCallPartners")
    CallDTO toDTO(CallMongoEntity callMongoEntity, @Context MongoCrossReferenceResolver resolver);
    List<CallDTO> toDTOs(List<CallMongoEntity> callMongoEntity, @Context MongoCrossReferenceResolver resolver);


    @Named("resolveJointCallPartners")
    default List<FunderReference> resolveJointCallPartners(CallMongoEntity callMongoEntity, @Context MongoCrossReferenceResolver resolver) {

        List<FunderReference> jointCallPartners = new ArrayList<>();

        if (callMongoEntity.registeredJointCallPartnerIds != null) {
            jointCallPartners.addAll(resolver.resolveFunderReferences(callMongoEntity.registeredJointCallPartnerIds));
        }

        if (callMongoEntity.otherJointCallpartner != null) {
            jointCallPartners.addAll(callMongoEntity.otherJointCallpartner.stream().map(funderReferenceProjection
                            -> FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(funderReferenceProjection))
                    .toList());
        }

        return jointCallPartners;

    }

    @Named("resolveCallOwner")
    default CallOwner resolveCallOwner(CallMongoEntity callMongoEntity, @Context MongoCrossReferenceResolver resolver) {
        String universityOwner = CallOwner.CallOwnerKind.UNIVERSITY.toString();
        String funderOwner = CallOwner.CallOwnerKind.FUNDER.toString();

        callMongoEntity.ownerKind = String.valueOf(callMongoEntity.ownerKind);
        if (universityOwner.equalsIgnoreCase(callMongoEntity.ownerKind)) {
            return resolver.resolveUniversity(ObjectIdUtils.toObjectId(callMongoEntity.ownerId));
        } else if (funderOwner.equalsIgnoreCase(callMongoEntity.ownerKind)) {
            return resolver.resolveFunder(ObjectIdUtils.toObjectId(callMongoEntity.ownerId));
        }
        return null;
    }

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "risId", source = "externalIdentifier.risId")
    @Mapping(target = "identifiers", source = "externalIdentifier.identifiers")
    @Mapping(target = "funderId", source = "funder.id.value")
    @Mapping(target = "partOfId", source = "partOf.id.value")
    @Mapping(target = "registeredJointCallPartnerIds", ignore = true) // set before mapping
    @Mapping(target = "otherJointCallpartner", source = "jointCallPartner")
    @Mapping(target = "ownerId", source = "callOwner", qualifiedByName = "mapOwnerId")
    @Mapping(target = "ownerKind", source = "callOwner", qualifiedByName = "mapOwnerKind")
    CallMongoEntity fromDomain(Call call);

    @Mapping(target = "identifiers", source = "externalIdentifier.identifiers")
    @Mapping(target = "funderId", source = "funder.id.value")
    @Mapping(target = "partOfId", source = "partOf.id.value")
    @Mapping(target = "registeredJointCallPartnerIds", ignore = true) // set before mapping
    @Mapping(target = "otherJointCallpartner", source = "jointCallPartner")
    CallMongoEntity fromDomain(CallUpdate callUpdate);

    List<CallMongoEntity> fromDomain(List<Call> calls);

    @BeforeMapping
    default void preProcess(Call call, @MappingTarget CallMongoEntity callMongoEntity) {
        if (call.getJointCallPartner() == null || call.getJointCallPartner().isEmpty()) {
            return;
        }
        List<ObjectId> registeredJointCallPartnerIds = new ArrayList<>();
        List<FunderReference> otherJointCallpartner = new ArrayList<>();
        call.getJointCallPartner().forEach(jointCallPartner -> {
            if (jointCallPartner.id() != null) {
                registeredJointCallPartnerIds.add(ObjectIdUtils.toObjectId(jointCallPartner.id().value()));
            } else {
                otherJointCallpartner.add(jointCallPartner);
            }
        });
        callMongoEntity.registeredJointCallPartnerIds = registeredJointCallPartnerIds;
        call.setJointCallPartner(otherJointCallpartner);
    }

    @Named("mapOwnerId")
    default String mapOwnerId(CallOwner owner) {
        return owner != null ? owner.getOwnerId() : null;
    }

    @Named("mapOwnerKind")
    default String mapOwnerKind(CallOwner owner) {
        return (owner != null ) ? owner.getCallOwnerKind().name() : null;
    }
}
