package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.ClaimDto;
import com.example.carins.web.dto.ClaimResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    static final LocalDate MIN_DATE = LocalDate.of(1990,1,1);
    static final LocalDate MAX_DATE = LocalDate.of(2025,12,12);

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        // TODO: validate date format and handle errors consistently
        LocalDate d;
        try{
            d = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid date format. Expected ISO YYYY-MM-DD");
        }
        if(d.isBefore(MIN_DATE) || d.isAfter(MAX_DATE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Date " + d + " is out of supported range " + MIN_DATE + " to " + MAX_DATE);
        }
        boolean valid = service.isInsuranceValid(carId, d);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));
    }

    @GetMapping("/cars/{carId}/history")
    public List<ClaimResponseDto> getInsuranceClaims(@PathVariable long carId) {
        return service.listInsuranceClaim(carId).stream().map(this::toInsuranceClaimResponse).toList();
    }
    @PostMapping("cars/{carId}/claims")
    public ResponseEntity<?> registerInsuranceClaim(@PathVariable Long carId, @Valid @RequestBody ClaimDto claimDto) {
        Car car = service.findCarById(carId); // find car with carId(if exists)
        InsuranceClaim insuranceClaim = toInsuranceClaim(claimDto, car); //create insurance claim dto
        InsuranceClaim claim = service.addInsuranceClaim(insuranceClaim); // add to database
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .pathSegment(claim.getId().toString()) //add path for location header
                .buildAndExpand().toUri())
                .body(toInsuranceClaimResponse(claim)); //set body of a response
    }
    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    private InsuranceClaim toInsuranceClaim(ClaimDto claimDto, Car car) {
        return new InsuranceClaim(car, claimDto.amount(), claimDto.claimDate(), claimDto.description()); // dto to claim
    }

    private ClaimResponseDto toInsuranceClaimResponse(InsuranceClaim insuranceClaim) {
        return new ClaimResponseDto(insuranceClaim.getId(),
                insuranceClaim.getCar().getId(),
                insuranceClaim.getClaimDate(),
                insuranceClaim.getDescription(),
                insuranceClaim.getAmount()); // claim to responseDto
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}
}
