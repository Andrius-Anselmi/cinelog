package com.cinelog.request;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(@NotEmpty(message = "email is required") String email, @NotEmpty(message = "password is required") String password) {
}
