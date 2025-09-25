package at.ac.tuwien.refop.application.port.common;

/**
 * An exception indicating that no Vocabulary was found for the Vocabulary ID specified by the user.
 * @deprecated Use {@link at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException} instead.
 **/
@Deprecated
public class VocabularyNotFoundException extends EntityNotFoundException{
}