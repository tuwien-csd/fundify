package at.ac.tuwien.refop.adapters.common.ris.mapper;

import at.ac.tuwien.refop.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.refop.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.refop.domain.funding.vo.enums.ERegionalScope;
import at.ac.tuwien.refop.domain.common.ETargetGroup;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisEligibleApplicantsRegion;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisEligibleApplicantsScope;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisTargetGroup;

public class RisQueryParamMapperUtil {

    private RisQueryParamMapperUtil() {}

    public static ECallType mapRisFundingTypeToECallType(RisFundingType risFundingType) {
        if (risFundingType == null) {
            return null;
        }
        return switch (risFundingType) {
            case CALL -> ECallType.CALL;
            case ONGOING_CALL -> ECallType.ONGOING_CALL;
            default -> null;
        };
    }

    public static ETargetGroup mapRisTargetGroupToETargetGroup(RisTargetGroup risTargetGroup) {
        if (risTargetGroup == null) {
            return null;
        }
        return switch (risTargetGroup) {
            case UNIVERSITY_OF_APPLIED_SCIENCES -> ETargetGroup.UNIVERSITY_OF_APPLIED_SCIENCES;
            case PRIVATE_UNIVERSITY -> ETargetGroup.PRIVATE_UNIVERSITY;
            case RESEARCH_INSTITUTE -> ETargetGroup.RESEARCH_INSTITUTE;
            case COMPANY -> ETargetGroup.COMPANY;
            case PRIVATE_NON_PROFIT -> ETargetGroup.PRIVATE_NON_PROFIT;
            case UNIVERSITY -> ETargetGroup.UNIVERSITY;
            case INDEPENDENT_RESEARCHER -> ETargetGroup.INDEPENDENT_RESEARCHER;
            case GOVERNMENT -> ETargetGroup.GOVERNMENT;
        };
    }

    public static EAustrianState mapRisEligibleApplicantsRegionToEAustrianState(RisEligibleApplicantsRegion risEligibleApplicantsRegion) {
        if (risEligibleApplicantsRegion == null) {
            return null;
        }
        return switch (risEligibleApplicantsRegion) {
            case BURGENLAND -> EAustrianState.BURGENLAND;
            case CARINTHIA -> EAustrianState.CARINTHIA;
            case LOWER_AUSTRIA -> EAustrianState.LOWER_AUSTRIA;
            case SALZBURG -> EAustrianState.SALZBURG;
            case STYRIA -> EAustrianState.STYRIA;
            case TYROL -> EAustrianState.TYROL;
            case UPPER_AUSTRIA -> EAustrianState.UPPER_AUSTRIA;
            case VIENNA -> EAustrianState.VIENNA;
            case VORARLBERG -> EAustrianState.VORARLBERG;
        };
    }

    public static ERegionalScope mapRisEligibleApplicantsScopeToERegionalScope(RisEligibleApplicantsScope risEligibleApplicantsScope) {
        if (risEligibleApplicantsScope == null) {
            return null;
        }
        return switch (risEligibleApplicantsScope) {
            case NATIONAL -> ERegionalScope.NATIONAL;
            case REGIONAL -> ERegionalScope.REGIONAL;
        };
    }
}
