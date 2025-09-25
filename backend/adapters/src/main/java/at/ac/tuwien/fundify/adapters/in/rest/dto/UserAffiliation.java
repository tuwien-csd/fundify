package at.ac.tuwien.fundify.adapters.in.rest.dto;

public record UserAffiliation(
    String affiliationId
) {
    public static UserAffiliation fromDomain(String affiliationId) {
        return new UserAffiliation(affiliationId);
    }
}