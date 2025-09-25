package at.ac.tuwien.refop.domain.common;

public record TranslatedText(
        String text,
        ELanguage language,
        ETranslation translation
) {}