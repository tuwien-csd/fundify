package at.ac.tuwien.fundify.domain.common;

public interface CallOwner {
    String getOwnerId();
    CallOwnerKind getCallOwnerKind();
    String getAcronym();
    enum CallOwnerKind { UNIVERSITY,  FUNDER }
}