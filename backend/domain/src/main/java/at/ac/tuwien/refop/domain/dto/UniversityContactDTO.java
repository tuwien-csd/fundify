package at.ac.tuwien.refop.domain.dto;

public record UniversityContactDTO(
        String name,
        String email,
        String phone,
        String website,
        String department
) {
}