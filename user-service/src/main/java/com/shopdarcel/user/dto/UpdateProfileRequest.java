package com.shopdarcel.user.dto;

import com.shopdarcel.user.entity.Gender;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Incoming request payload to update the currently authenticated user's
 * profile. All fields are optional — only non-null fields are applied,
 * so the client can update just one field without resending everything.
 * <p>
 * Deliberately excludes email (requires its own re-verification flow)
 * and password (has its own dedicated endpoint).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @Pattern(regexp = "^[\\p{L} '-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String firstName;

    @Pattern(regexp = "^[\\p{L} '-]*$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String lastName;

    private Integer birthYear;

    private Gender gender;
}