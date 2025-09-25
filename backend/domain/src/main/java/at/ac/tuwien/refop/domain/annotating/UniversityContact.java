package at.ac.tuwien.refop.domain.annotating;

public record UniversityContact(
        String name,
        String email,
        String phone,
        String website,
        String department
) {
}