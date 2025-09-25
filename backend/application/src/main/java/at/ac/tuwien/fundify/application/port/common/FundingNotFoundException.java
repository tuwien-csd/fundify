package at.ac.tuwien.fundify.application.port.common;


/**
 * An exception indicating that no Funding was found for the funding ID specified by the user.
 * @deprecated Use {@link at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException} instead.
 **/
@Deprecated
public class FundingNotFoundException extends EntityNotFoundException{
}