package at.ac.tuwien.fundify.adapters.in.rest.dto;


import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ELanguageWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ETranslationWebModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record TranslatedTextWebModel(
    @Schema(required = true) @NotBlank
    String text,
    @Schema(required = true) @NotNull
    ELanguageWebModel language,
    @Schema(required = true)
    ETranslationWebModel translation
) {

}