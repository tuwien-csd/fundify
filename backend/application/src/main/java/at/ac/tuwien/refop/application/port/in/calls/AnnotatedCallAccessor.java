package at.ac.tuwien.refop.application.port.in.calls;

import at.ac.tuwien.refop.domain.annotating.AnnotatedCallId;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.CallId;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.dto.AnnotatedCallDTO;
import at.ac.tuwien.refop.domain.dto.CallPreviewDTO;
import java.util.List;


public interface AnnotatedCallAccessor {

    AnnotatedCallDTO getById(AnnotatedCallId id) throws EntityNotFoundException;

    AnnotatedCallDTO getByCallIdAndUniversityId(CallId callId, UniversityId universityId)
        throws EntityNotFoundException;

    CallPreviewDTO getCallPreviewByCallId(CallId callId)
        throws EntityNotFoundException;

    List<AnnotatedCallDTO> getByUniversityId(UniversityId universityId);

    List<AnnotatedCallDTO> getAll();
}
