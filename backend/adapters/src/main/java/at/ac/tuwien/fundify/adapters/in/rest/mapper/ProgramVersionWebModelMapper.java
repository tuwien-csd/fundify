package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.DateRangeWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.ProgramVersionWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EUpdateSourceWebModel;
import at.ac.tuwien.fundify.domain.common.DateRange;
import at.ac.tuwien.fundify.domain.funding.ProgramVersion;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import java.util.List;

public class ProgramVersionWebModelMapper {

    private ProgramVersionWebModelMapper() {}

    public static ProgramVersionWebModel fromDomain(ProgramVersion version) {
        return new ProgramVersionWebModel(
            version.getId(),
            version.getProgramId().value(),
            version.getVersionedAt(),
            mapSource(version.getUpdateSource()),
            TranslatedTextWebModelMapper.INSTANCE.fromDomain(version.getDescription()),
            mapDateRange(version.getDuration())
        );
    }

    public static List<ProgramVersionWebModel> fromDomain(List<ProgramVersion> versions) {
        return versions.stream().map(ProgramVersionWebModelMapper::fromDomain).toList();
    }

    private static EUpdateSourceWebModel mapSource(EUpdateSource source) {
        if (source == null) return null;
        return EUpdateSourceWebModel.valueOf(source.name());
    }

    private static DateRangeWebModel mapDateRange(DateRange dateRange) {
        if (dateRange == null) return null;
        return new DateRangeWebModel(dateRange.start(), dateRange.end());
    }
}
