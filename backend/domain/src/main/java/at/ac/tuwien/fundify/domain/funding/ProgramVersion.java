package at.ac.tuwien.fundify.domain.funding;

import at.ac.tuwien.fundify.domain.common.DateRange;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ProgramVersion {

    private String id;
    private ProgramId programId;
    private LocalDateTime versionedAt;
    private EUpdateSource updateSource;

    private List<TranslatedText> description;
    private DateRange duration;
}
