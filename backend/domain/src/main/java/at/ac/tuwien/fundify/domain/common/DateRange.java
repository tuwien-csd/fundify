package at.ac.tuwien.fundify.domain.common;


import java.time.LocalDateTime;

public record DateRange(
        LocalDateTime start,
        LocalDateTime end
) {}
