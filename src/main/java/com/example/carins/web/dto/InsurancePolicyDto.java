package com.example.carins.web.dto;

import com.example.carins.model.Car;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record InsurancePolicyDto(
        Long carId,
        String provider,
        LocalDate startDate,
        @NotNull //runtime validation, before persistance
        LocalDate endDate
) {
}
