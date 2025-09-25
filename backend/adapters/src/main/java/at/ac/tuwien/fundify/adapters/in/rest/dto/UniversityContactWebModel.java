package at.ac.tuwien.fundify.adapters.in.rest.dto;


public record UniversityContactWebModel(
        String name,
        String email,
        String phone,
        String website,
        String department
) {
}