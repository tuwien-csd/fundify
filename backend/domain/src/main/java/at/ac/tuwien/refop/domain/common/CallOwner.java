package at.ac.tuwien.refop.domain.common;

public interface CallOwner {
    String getOwnerId();
    CallOwnerKind getCallOwnerKind();
    String getAcronym();
    enum CallOwnerKind { UNIVERSITY,  FUNDER }
}