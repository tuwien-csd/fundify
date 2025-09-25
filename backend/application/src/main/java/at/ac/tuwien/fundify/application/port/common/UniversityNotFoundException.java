package at.ac.tuwien.fundify.application.port.common;


/**
 * An exception indicating that no University was found for the University ID specified by the user.
 * @deprecated Use {@link at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException} instead.
 **/
@Deprecated
public class UniversityNotFoundException extends EntityNotFoundException{
}