package com.example.carins.web.dto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClaimDto(
        @PastOrPresent(message = "Date must not be in the future")
        LocalDate claimDate,
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,
        @Positive(message = "Amount must be greater than 0")
        float amount
) {
}