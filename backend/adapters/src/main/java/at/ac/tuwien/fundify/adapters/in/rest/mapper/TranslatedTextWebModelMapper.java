package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.TranslatedTextWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ETranslationWebModel;
import at.ac.tuwien.fundify.domain.common.ETranslation;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TranslatedTextWebModelMapper {
    TranslatedTextWebModelMapper INSTANCE = Mappers.getMapper(TranslatedTextWebModelMapper.class);

    @Mapping(target = "translation", source = "translation", qualifiedByName = "eTranslationFromETranslationWebModel")
    TranslatedText toDomain(TranslatedTextWebModel source);
    List<TranslatedText> toDomain(List<TranslatedTextWebModel> source);

    @Mapping(target = "translation", source = "translation", qualifiedByName = "eTranslationWebModelFromETranslation")
    TranslatedTextWebModel fromDomain(TranslatedText source);
    List<TranslatedTextWebModel> fromDomain(List<TranslatedText> source);

    @Named("eTranslationWebModelFromETranslation")
    default ETranslationWebModel eTranslationWebModelFromETranslation(ETranslation translation) {
        return switch (translation) {
            case TRANSLATION_HUMAN, TRANSLATION_MACHINE -> ETranslationWebModel.TRANSLATION;
            default -> ETranslationWebModel.ORIGINAL;
        };
    }

    @Named("eTranslationFromETranslationWebModel")
    default ETranslation eTranslationFromETranslationWebModel(ETranslationWebModel translation) {
        if (translation == null) {
            return ETranslation.ORIGINAL;
        }
        return switch (translation) {
            case ORIGINAL -> ETranslation.ORIGINAL;
            case TRANSLATION -> ETranslation.TRANSLATION_HUMAN;
        };
    }

}
