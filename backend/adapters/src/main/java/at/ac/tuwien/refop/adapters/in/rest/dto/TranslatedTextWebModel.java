package at.ac.tuwien.refop.adapters.in.rest.dto;


import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ELanguageWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ETranslationWebModel;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record TranslatedTextWebModel(
    @Schema(required = true)
    String text,
    @Schema(required = true)
    ELanguageWebModel language,
    @Schema(required = true)
    ETranslationWebModel translation
) {

}