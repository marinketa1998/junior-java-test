package com.example.carins.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "insurancepolicy")
public class InsurancePolicy {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY,s)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policy_seq")
    @SequenceGenerator(
            name = "policy_seq",
            sequenceName = "policy_seq",   // must exist in DB or be created by Hibernate
            initialValue = 10,           // first value Hibernate will ask for
            allocationSize = 50            // performance; keep in sync with DB INCREMENT BY
    )
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Car car;

    private String provider;
    private LocalDate startDate;
    @Column(nullable = false)  //marks column as not null, database level
    private LocalDate endDate;


    public InsurancePolicy() {}
    public InsurancePolicy(Car car, String provider, LocalDate startDate, LocalDate endDate) {
        this.car = car; this.provider = provider; this.startDate = startDate; this.endDate = endDate;
    }

    public Long getId() { return id; }
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
