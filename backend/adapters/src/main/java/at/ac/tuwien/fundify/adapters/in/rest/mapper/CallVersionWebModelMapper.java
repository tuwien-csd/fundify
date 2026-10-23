package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.CallVersionWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.MonetaryNumberWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ECurrencyWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EUpdateSourceWebModel;
import at.ac.tuwien.fundify.domain.funding.CallVersion;
import at.ac.tuwien.fundify.domain.funding.vo.MonetaryNumber;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import java.util.List;

public class CallVersionWebModelMapper {

    private CallVersionWebModelMapper() {}

    public static CallVersionWebModel fromDomain(CallVersion version) {
        return new CallVersionWebModel(
            version.getId(),
            version.getCallId().value(),
            version.getVersionedAt(),
            mapSource(version.getUpdateSource()),
            TranslatedTextWebModelMapper.INSTANCE.fromDomain(version.getName()),
            TranslatedTextWebModelMapper.INSTANCE.fromDomain(version.getDescription()),
            TranslatedTextWebModelMapper.INSTANCE.fromDomain(version.getEligibleApplicants()),
            CallStageWebModelMapper.INSTANCE.fromDomain(version.getCallStages()),
            mapMonetary(version.getCallVolumeAmount()),
            version.getWebsite()
        );
    }

    public static List<CallVersionWebModel> fromDomain(List<CallVersion> versions) {
        return versions.stream().map(CallVersionWebModelMapper::fromDomain).toList();
    }

    private static EUpdateSourceWebModel mapSource(EUpdateSource source) {
        if (source == null) return null;
        return EUpdateSourceWebModel.valueOf(source.name());
    }

    private static MonetaryNumberWebModel mapMonetary(MonetaryNumber monetary) {
        if (monetary == null) return null;
        ECurrencyWebModel currency = monetary.currency() != null
            ? ECurrencyWebModel.valueOf(monetary.currency().name())
            : null;
        return new MonetaryNumberWebModel(monetary.amount(), currency);
    }
}
