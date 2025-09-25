package at.ac.tuwien.refop.application.port.in.calls;

import at.ac.tuwien.refop.domain.annotating.AnnotatedCallId;
import at.ac.tuwien.refop.domain.annotating.CallAnnotation;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.CallId;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;

public interface AnnotatedCallUseCase {

    AnnotatedCallId addAnnotation(CallId callId, UniversityId universityId, CallAnnotation annotation) throws FundifyException;

    AnnotatedCallId updateAnnotation(AnnotatedCallId id, CallAnnotation annotation)
        throws FundifyException;

    boolean deleteAnnotation(AnnotatedCallId id) throws FundifyException;

    AnnotatedCallId publish(AnnotatedCallId id) throws FundifyException;

}