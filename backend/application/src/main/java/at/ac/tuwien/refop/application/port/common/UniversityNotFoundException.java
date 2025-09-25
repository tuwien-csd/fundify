package at.ac.tuwien.refop.application.port.common;


/**
 * An exception indicating that no University was found for the University ID specified by the user.
 * @deprecated Use {@link at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException} instead.
 **/
@Deprecated
public class UniversityNotFoundException extends EntityNotFoundException{
}