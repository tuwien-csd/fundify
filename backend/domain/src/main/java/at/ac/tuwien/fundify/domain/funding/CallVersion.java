package at.ac.tuwien.fundify.domain.funding;

import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.vo.CallStage;
import at.ac.tuwien.fundify.domain.funding.vo.MonetaryNumber;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CallVersion {

    private String id;
    private CallId callId;
    private LocalDateTime versionedAt;
    private EUpdateSource updateSource;

    private List<TranslatedText> name;
    private List<TranslatedText> description;
    private List<TranslatedText> eligibleApplicants;
    private List<CallStage> callStages;
    private MonetaryNumber callVolumeAmount;
    private List<String> website;
}
