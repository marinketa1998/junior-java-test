package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository claimRepository;
    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository,
                      InsuranceClaimRepository claimRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) return false;
        // TODO: optionally throw NotFound if car does not exist
        if(!carRepository.existsById(carId)) { //
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car with id " + carId + " not found!");
        }
        return policyRepository.existsActiveOnDate(carId, date);
    }

    public Car findCarById(Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Car with id " + carId + " not found!"
                )
        );
        return car;
    }

    public InsuranceClaim addInsuranceClaim(InsuranceClaim insuranceClaim){
        return claimRepository.save(insuranceClaim); //save to database
    }

    public List<InsuranceClaim> listInsuranceClaim(Long carId) {
        return claimRepository.findByCar_IdOrderByClaimDateAsc(carId); //find claims by car id
    }

    public List<InsurancePolicy> getExpiredPolicies() {
        return policyRepository.findByEndDate(LocalDate.now().minusDays(1)); //current date - one day
    }
}
