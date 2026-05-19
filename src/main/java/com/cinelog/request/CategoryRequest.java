package com.cinelog.request;

import jakarta.validation.constraints.NotEmpty;

public record CategoryRequest(@NotEmpty(message = "Name of category is required ") String name) {
}
