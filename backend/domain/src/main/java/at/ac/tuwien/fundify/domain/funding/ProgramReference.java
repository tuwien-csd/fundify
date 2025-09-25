package at.ac.tuwien.fundify.domain.funding;

import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.funding.vo.Identifier;
import at.ac.tuwien.fundify.domain.common.TranslatedText;

import java.util.List;


public record ProgramReference(
    ProgramId id,
    RisId risId,
    List<TranslatedText> name,
    String acronym,
    List<String> website,
    List<Identifier> identifiers,
    FunderReference funder
) { }