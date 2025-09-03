package com.example.carins;

import com.example.carins.service.CarService;
import com.example.carins.web.CarController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarInsuranceApplicationTests {

    @Autowired
    CarService service;
    CarController controller;
    @Test
    void insuranceValidityBasic() {
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-06-01")));
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2025-06-01")));
        assertFalse(service.isInsuranceValid(2L, LocalDate.parse("2025-02-01")));
    }

    @Test
    void notFoundWhenCarDoesNotExist() {
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(100L, LocalDate.parse(("2025-08-08"))));
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
        assertTrue(responseStatusException.getReason().contains("Car with id 100 not found!"));
    }

    @Test
    void notValidDateFormat() {
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> controller.isInsuranceValid(2L, "20-06-2010"));
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
        assertTrue(responseStatusException.getReason().contains("Invalid date format. Expected ISO YYYY-MM-DD"));
    }

    @Test
    void dateOutOfRange(){
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> controller.isInsuranceValid(2L, "2040-06-01"));
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
        assertTrue(responseStatusException.getReason().contains("Date 2040-06-01 is out of supported range 1990-01-01 to 2025-12-12"));
    }
}
