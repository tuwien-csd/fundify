package at.ac.tuwien.refop.domain.common;


import java.time.LocalDateTime;

public record DateRange(
        LocalDateTime start,
        LocalDateTime end
) {}
