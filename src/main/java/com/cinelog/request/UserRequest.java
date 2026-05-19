package com.cinelog.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserRequest(
        @NotEmpty(message = "name is required")String name,
        @NotEmpty(message = "email is required") String email,
        @NotEmpty(message = "password is required") String password) {
}
