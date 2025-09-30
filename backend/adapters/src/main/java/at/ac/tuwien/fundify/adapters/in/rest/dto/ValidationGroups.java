package at.ac.tuwien.fundify.adapters.in.rest.dto;

import jakarta.validation.groups.Default;

public interface ValidationGroups {
    interface Post extends Default {
    }
    interface Put extends Default {
    }
}