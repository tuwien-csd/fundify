package at.ac.tuwien.fundify.domain.ticketing;

public record TicketCreate(
    String name,
    String subject,
    String category,
    String email,
    String message,
    String kindOfInstitution
) {

}
