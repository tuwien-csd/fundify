package at.ac.tuwien.fundify.adapters.common.ris.mapper;

import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisIdentifier;
import at.ac.tuwien.fundify.domain.common.ELanguage;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.funding.ExternalIdentifier;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAnswerYN;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EFundingScheme;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommonRisMappingConfig {

    CommonRisMappingConfig INSTANCE = Mappers.getMapper(CommonRisMappingConfig.class);

    default ExternalIdentifier toExternalFundingIdentifier(RisFunding source, @Context String memberId) {
        return toExternalFundingIdentifier(source.getIdentifiers(), source.getId(), memberId);
    }

    default ExternalIdentifier toExternalFundingIdentifier(List<RisIdentifier> identifiers, String id, String memberId) {
        RisId risId = RisId.generateFundingRisId(memberId, id);
        return new ExternalIdentifier(RisIdentifierMapper.INSTANCE.toDomain(identifiers), risId);
    }

    @Named("orgunitRisIdfromId")
    default RisId orgunitRisIdfromId(String id, @Context String memberId) {
        return RisId.generateOrgunitRisId(memberId, id);
    }

    @Named("fundingRisIdFromId")
    default  RisId fundingRisIdFromId(String id, @Context String memberId) {
        return RisId.generateFundingRisId(memberId, id);
    }

    default  String risIdToString(RisId risId) {
        return risId != null ? risId.toString() : null;
    }

    default  RisId stringToRisId(String risId) {
        return risId != null ? RisId.fromString(risId) : null;
    }

    default  EFundingScheme fundingTypeToEFundingScheme(RisFundingType fundingType) {
        if (fundingType == null) {
            return null;
        }
        return switch (fundingType) {
            case AWARD -> EFundingScheme.AWARD;
            case GRANT -> EFundingScheme.GRANT;
            case RESEARCH_CONTRACT -> EFundingScheme.RESEARCH_CONTRACT;
            case SCHOLARSHIP -> EFundingScheme.SCHOLARSHIP;
            case SEMESTER_GRANT -> EFundingScheme.SEMESTER_GRANT;
            case SUMMER_GRANT -> EFundingScheme.SUMMER_GRANT;
            case PRACTICAL_TRAINING -> EFundingScheme.PRACTICAL_TRAINING;
            case SUBSIDY -> EFundingScheme.SUBSIDY;
            case RESEARCH_ALLOWANCE -> EFundingScheme.RESEARCH_ALLOWANCE;
            default -> throw new IllegalArgumentException("Unknown funding scheme: " + fundingType);
        };
    }

    default RisFundingType eFundingSchemeToFundingType(EFundingScheme fundingScheme) {
        if (fundingScheme == null) {
            return null;
        }
        return switch (fundingScheme) {
            case AWARD -> RisFundingType.AWARD;
            case GRANT -> RisFundingType.GRANT;
            case RESEARCH_CONTRACT -> RisFundingType.RESEARCH_CONTRACT;
            case SCHOLARSHIP -> RisFundingType.SCHOLARSHIP;
            case SEMESTER_GRANT -> RisFundingType.SEMESTER_GRANT;
            case SUMMER_GRANT -> RisFundingType.SUMMER_GRANT;
            case PRACTICAL_TRAINING -> RisFundingType.PRACTICAL_TRAINING;
            case SUBSIDY -> RisFundingType.SUBSIDY;
            case RESEARCH_ALLOWANCE -> RisFundingType.RESEARCH_ALLOWANCE;
        };
    }


  default  List<ELanguage> stringListToELanguages(List<String> languages) {
    if (languages == null) {
      return Collections.emptyList();
    }
    return languages.stream()
        .map(RisTextMapper.INSTANCE::stringToELanguage)
        .toList();
  }

  default  List<String> eLanguagesToStringList(List<ELanguage> languages) {
    if (languages == null) {
      return Collections.emptyList();
    }
    return languages.stream()
        .map(RisTextMapper.INSTANCE::eLanguageToString)
        .toList();
  }

    default  EAnswerYN map(Boolean value) {
        return value != null && value ? EAnswerYN.YES : EAnswerYN.NO;
    }

    default  Boolean map(EAnswerYN value) {
        return value == EAnswerYN.YES;
    }

    default  LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }

    default  OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? OffsetDateTime.of(localDateTime, ZoneOffset.UTC) : null;
    }
}