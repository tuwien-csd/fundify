package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.domain.common.DateRange;
import at.ac.tuwien.fundify.domain.common.EFundingCharacteristic;
import at.ac.tuwien.fundify.domain.common.ELanguage;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.ETranslation;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.domain.annotating.AnnotatingFunderReference;
import at.ac.tuwien.fundify.domain.annotating.AnnotatingProgramReference;
import at.ac.tuwien.fundify.domain.annotating.CallStagePreview;
import at.ac.tuwien.fundify.domain.dto.CallPreviewDTO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CallPreviewProjectionMapperTest {

    private String exampleFunderIdString = "AAAAAAAAAAAAAAAAAAAAAAAA";
    private String exampleProgramIdString = "BBBBBBBBBBBBBBBBBBBBBBBB";
    private ObjectId exampleFunderId = new ObjectId(exampleFunderIdString);
    private ObjectId exampleProgramId = new ObjectId(exampleProgramIdString);
    private AnnotatingProgramReference exampleProgram = new AnnotatingProgramReference(
            new ProgramId(exampleProgramIdString), null, null);
    private AnnotatingFunderReference exampleFunder = new AnnotatingFunderReference(
            new FunderId(exampleFunderIdString), null, null);


    private CallPreviewProjectionMapper callPreviewProjectionMapper = CallPreviewProjectionMapper.INSTANCE;

    @Test
    void toDTO() {

        CallPreviewProjection source = exampleCallPreviewProjection();
        MongoCrossReferenceResolver context = mockMongoCrossReferenceResolver(
                exampleFunderId, exampleFunder, exampleProgramId, exampleProgram);

        CallPreviewDTO expected = expectedDTO(source, exampleFunder, exampleProgram);

        CallPreviewDTO actual = callPreviewProjectionMapper.toDTO(source, context);

        assertEquals(expected, actual);

    }

    private CallPreviewDTO expectedDTO(CallPreviewProjection source,
                                       AnnotatingFunderReference funderReference,
                                       AnnotatingProgramReference programReference) {

        ArrayList name =  new ArrayList<>(source.name());

        CallPreviewDTO expected = new CallPreviewDTO(
                source._id().toString(),
                source.registrationDate(),
                source.lastSync(),
                name,
                programReference,
                funderReference,
                source.targetGroups(),
                source.characteristics(),
                source.callStages()
                );

        return expected;
    }

    private MongoCrossReferenceResolver mockMongoCrossReferenceResolver(
            ObjectId funderId,
            AnnotatingFunderReference funder,
            ObjectId programId,
            AnnotatingProgramReference program) {
        MongoCrossReferenceResolver context = mock(MongoCrossReferenceResolver.class);
        given(context.resolveAnnotatingFunderReference(funderId)).willReturn(funder);
        given(context.resolveAnnotatingProgramReference(programId)).willReturn(program);
        return context;
    }

    private CallPreviewProjection exampleCallPreviewProjection() {

        CallStagePreview cexampleCallStagePreview = new CallStagePreview(1, new DateRange(
                LocalDateTime.of(2024, 11, 1, 12, 30),
                LocalDateTime.of(2024, 11, 14, 0, 0)
        ));

        TranslatedText exampleName = new TranslatedText("TEST", ELanguage.ENGLISH, ETranslation.TRANSLATION_HUMAN);

        return new CallPreviewProjection(
                new ObjectId("CCCCCCCCCCCCCCCCCCCCCCCC"),
                LocalDateTime.of(2024, 10, 1, 12, 30),
                LocalDateTime.of(2024, 10, 1, 9, 0),
                List.of(exampleName),
                exampleFunderId,
                exampleProgramId,
                EnumSet.of(ETargetGroup.RESEARCH_INSTITUTE, ETargetGroup.GOVERNMENT).stream().toList(),
                EnumSet.of(EFundingCharacteristic.INFRASTRUCTURE, EFundingCharacteristic.BILATERAL_PROGRAMME).stream().toList(),
                List.of(cexampleCallStagePreview)
        );
    }

}