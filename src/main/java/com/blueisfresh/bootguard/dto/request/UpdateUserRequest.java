package com.blueisfresh.bootguard.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile information.
 * <p>
 * Allows modification of display name and description.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 2, max = 100)
    private String displayName;

    private String description;
}