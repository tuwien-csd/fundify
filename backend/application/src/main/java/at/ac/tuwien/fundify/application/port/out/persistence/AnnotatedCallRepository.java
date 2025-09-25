package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.annotating.AnnotatedCall;
import at.ac.tuwien.fundify.domain.annotating.AnnotatedCallId;
import java.util.Optional;

public interface AnnotatedCallRepository {

    AnnotatedCall persist(AnnotatedCall annotatedCall);

    AnnotatedCall update(AnnotatedCall annotatedCall);

    boolean delete(AnnotatedCallId id);

    Optional<AnnotatedCall> findById(AnnotatedCallId id);
}
