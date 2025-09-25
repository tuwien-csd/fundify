package at.ac.tuwien.fundify.domain.annotating;

public record UniversityContact(
        String name,
        String email,
        String phone,
        String website,
        String department
) {
}