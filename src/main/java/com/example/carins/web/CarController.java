package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.ClaimDto;
import com.example.carins.web.dto.ClaimResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

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
        LocalDate d = LocalDate.parse(date);
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
