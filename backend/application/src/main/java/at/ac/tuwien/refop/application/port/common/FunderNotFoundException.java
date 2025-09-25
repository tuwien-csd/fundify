package at.ac.tuwien.refop.application.port.common;


/**
 * An exception indicating that no Funder was found for the funder ID specified by the user.
 * @deprecated Use {@link at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException} instead.
 **/
@Deprecated
public class FunderNotFoundException extends EntityNotFoundException{
}