package at.ac.tuwien.fundify.application.port.common;


import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;

/**
 * An exception indicating that no Program was found for the funder ID specified by the user.
 * @deprecated Use {@link EntityNotFoundException} instead.
 **/
@Deprecated
public class ProgramNotFoundException extends FundingNotFoundException{
}