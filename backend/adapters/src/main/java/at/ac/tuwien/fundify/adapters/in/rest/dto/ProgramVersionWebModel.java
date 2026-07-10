package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EUpdateSourceWebModel;
import java.time.LocalDateTime;
import java.util.List;

public record ProgramVersionWebModel(
    String id,
    String programId,
    LocalDateTime versionedAt,
    EUpdateSourceWebModel updateSource,
    List<TranslatedTextWebModel> description,
    DateRangeWebModel duration
) {}
