package com.example.carins.web.dto;

import java.time.LocalDate;

public record ClaimResponseDto(Long id, Long carId, LocalDate claimDate, String description, float amount) {
}
