package at.ac.tuwien.fundify.application.port.common;

/**
 * An exception indicating that no Vocabulary was found for the Vocabulary ID specified by the user.
 * @deprecated Use {@link at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException} instead.
 **/
@Deprecated
public class VocabularyNotFoundException extends EntityNotFoundException{
}