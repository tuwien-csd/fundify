package at.ac.tuwien.refop.domain.funding;

import at.ac.tuwien.refop.domain.common.CallId;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.common.TranslatedText;
import at.ac.tuwien.refop.domain.funding.vo.Identifier;

import java.util.List;

public record CallReference(
    CallId id,
    RisId risId,
    List<TranslatedText> name,
    String acronym,
    List<Identifier> identifiers,
    FunderReference funder,
    ProgramReference partOf
){ }
