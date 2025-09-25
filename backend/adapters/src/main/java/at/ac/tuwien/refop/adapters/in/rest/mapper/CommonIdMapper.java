package at.ac.tuwien.refop.adapters.in.rest.mapper;

import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.CallId;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.common.ProgramId;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.common.VocabularyId;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommonIdMapper {

    CommonIdMapper INSTANCE = Mappers.getMapper(CommonIdMapper.class);

    default RisId stringToRisId(String source) {
        return source != null ? RisId.fromString(source) : null;
    }

    default String risIdToString(RisId source) {
        return source != null ? source.toString() : null;
    }

    default String idToString(FunderId source) {
        return source != null ? source.value() : null;
    }

    default String idToString(ProgramId source) {
        return source != null ? source.value() : null;
    }

    default String idToString(CallId source) {
        return source != null ? source.value() : null;
    }

    default String idToString(UniversityId source) {
        return source != null ? source.value() : null;
    }

    default String idToString(VocabularyId source) {
        return source != null ? source.value() : null;
    }

    default FunderId stringToFunderId(String id) {
        return id != null ? new FunderId(id) : null;
    }

    default CallId stringToCallId(String id) {
        return id != null ? new CallId(id) : null;
    }

    default ProgramId stringToProgramId(String id) {
        return id != null ? new ProgramId(id) : null;
    }

    default UniversityId stringToUniversityId(String value) {
        return value != null ? new UniversityId(value) : null;
    }

    default VocabularyId stringToVocabularyId(String id) {
        return id != null ? new VocabularyId(id) : null;
    }

}
