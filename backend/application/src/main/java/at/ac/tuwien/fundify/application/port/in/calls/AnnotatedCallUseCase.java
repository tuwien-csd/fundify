package at.ac.tuwien.fundify.application.port.in.calls;

import at.ac.tuwien.fundify.domain.annotating.AnnotatedCallId;
import at.ac.tuwien.fundify.domain.annotating.CallAnnotation;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;

public interface AnnotatedCallUseCase {

    AnnotatedCallId addAnnotation(CallId callId, UniversityId universityId, CallAnnotation annotation) throws FundifyException;

    AnnotatedCallId updateAnnotation(AnnotatedCallId id, CallAnnotation annotation)
        throws FundifyException;

    boolean deleteAnnotation(AnnotatedCallId id) throws FundifyException;

    AnnotatedCallId publish(AnnotatedCallId id) throws FundifyException;

}