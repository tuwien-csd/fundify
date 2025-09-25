package at.ac.tuwien.fundify.domain.ticketing;

import java.util.List;

/**
 *
 * @param ticketKey Is used to check if a ticket already exists and therefore should be appended.
 * @param description Is only stored during initial ticket creation
 * @param additionalText Is written to the description; or the comments if the ticket already exists.
 */
public record AppendableTicketCreate(
    String ticketKey,
    String description,
    List<String> additionalText
) {

}
