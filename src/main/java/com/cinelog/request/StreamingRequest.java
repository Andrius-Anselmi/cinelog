package com.cinelog.request;

import jakarta.validation.constraints.NotEmpty;

public record StreamingRequest(@NotEmpty(message = "Name of streaming is requiired") String name) {
}
