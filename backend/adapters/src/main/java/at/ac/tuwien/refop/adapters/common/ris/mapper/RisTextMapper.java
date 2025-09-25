package at.ac.tuwien.refop.adapters.common.ris.mapper;

import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisText;
import at.ac.tuwien.refop.domain.common.ELanguage;
import at.ac.tuwien.refop.domain.common.ETranslation;
import at.ac.tuwien.refop.domain.common.TranslatedText;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RisTextMapper {

    RisTextMapper INSTANCE = Mappers.getMapper(RisTextMapper.class);

    @Mapping(target = "language", source = "lang", qualifiedByName = "stringToELanguage")
    @Mapping(target = "translation", source = "trans", qualifiedByName = "eTranslationFromRisTrans")
    TranslatedText toDomain(RisText source);
    List<TranslatedText> toDomain(List<RisText> source);

    @Named("eTranslationFromRisTrans")
    default ETranslation eTranslationFromRisTrans(RisText.TransEnum trans) {
        return switch (trans) {
            case O -> ETranslation.ORIGINAL;
            case H -> ETranslation.TRANSLATION_HUMAN;
            case M -> ETranslation.TRANSLATION_MACHINE;
        };
    }

    @Named("stringToELanguage")
    default ELanguage stringToELanguage(String lang) {
        return switch (lang.toLowerCase()) {
            case "en" -> ELanguage.ENGLISH;
            case "de" -> ELanguage.GERMAN;
            default -> throw new IllegalArgumentException("Unsupported language: " + lang);
        };
    }

    @Mapping(target = "lang", source = "language")
    @Mapping(target = "trans", source = "translation", qualifiedByName = "risTransFromETranslation")
    @Mapping(target = "text", source = "text", qualifiedByName = "risTextFromText")
    RisText fromDomain(TranslatedText source);
    List<RisText> fromDomain(List<TranslatedText> source);

    @Named("risTransFromETranslation")
    default RisText.TransEnum risTransFromETranslation(ETranslation translation) {

        // this should not happen, but if it does, we take default H
        if (translation == null) {
            return RisText.TransEnum.H;
        }

        return switch (translation) {
            case ORIGINAL -> RisText.TransEnum.O;
            case TRANSLATION_HUMAN -> RisText.TransEnum.H;
            case TRANSLATION_MACHINE -> RisText.TransEnum.M;
        };
    }

    @Named("risTextFromText")
    default String risTextFromText(String text) {

        // this should not happen, but if it does, we take default empty string
        if (text == null) {
            return "";
        }

        return text;
    }

    default String eLanguageToString(ELanguage language) {

        // this should not happen, but if it does, we default to en
        if (language == null) {
            return "en";
        }

        return switch (language) {
            case ENGLISH -> "en";
            case GERMAN -> "de";
        };
    }
}
