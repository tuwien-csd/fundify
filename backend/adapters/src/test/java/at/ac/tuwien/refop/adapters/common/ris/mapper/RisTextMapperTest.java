package at.ac.tuwien.refop.adapters.common.ris.mapper;

import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisText;
import at.ac.tuwien.refop.domain.common.ELanguage;
import at.ac.tuwien.refop.domain.common.ETranslation;
import at.ac.tuwien.refop.domain.common.TranslatedText;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RisTextMapperTest {

    RisTextMapper mapper = RisTextMapper.INSTANCE;

    @Test
    void fromDomainWithAllValuesPresent() {

        TranslatedText translatedText = new TranslatedText("text", ELanguage.ENGLISH, ETranslation.TRANSLATION_HUMAN);

        RisText result = mapper.fromDomain(translatedText);
        assertNotNull(result);
        assertEquals(translatedText.text(), result.getText());
        assertEquals("en", result.getLang());
        assertEquals(RisText.TransEnum.H, result.getTrans());
    }

    @Test
    void fromDomainWithNullTranslation() {

        TranslatedText translatedText = new TranslatedText("text", ELanguage.ENGLISH, null);

        RisText result = mapper.fromDomain(translatedText);
        assertNotNull(result);
        assertEquals(translatedText.text(), result.getText());
        assertEquals("en", result.getLang());
        assertEquals(RisText.TransEnum.H, result.getTrans());

    }

    @Test
    void fromDomainWithNullLanguage() {

        TranslatedText translatedText = new TranslatedText("text", null, ETranslation.TRANSLATION_HUMAN);

        RisText result = mapper.fromDomain(translatedText);
        assertNotNull(result);
        assertEquals(translatedText.text(), result.getText());
        assertEquals("en", result.getLang());
        assertEquals(RisText.TransEnum.H, result.getTrans());

    }

    @Test
    void fromDomainWithNullText() {

        TranslatedText translatedText = new TranslatedText(null, ELanguage.ENGLISH, ETranslation.TRANSLATION_HUMAN);

        RisText result = mapper.fromDomain(translatedText);
        assertNotNull(result);
        assertEquals("", result.getText());
        assertEquals("en", result.getLang());
        assertEquals(RisText.TransEnum.H, result.getTrans());

    }

}