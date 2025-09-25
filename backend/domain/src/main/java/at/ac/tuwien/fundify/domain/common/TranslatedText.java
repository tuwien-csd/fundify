package at.ac.tuwien.fundify.domain.common;

public record TranslatedText(
        String text,
        ELanguage language,
        ETranslation translation
) {}