package edu.cit.badinas.instock.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "fullName is required")
    @Size(max = 100, message = "fullName must be at most 100 characters")
    private String fullName;
}
