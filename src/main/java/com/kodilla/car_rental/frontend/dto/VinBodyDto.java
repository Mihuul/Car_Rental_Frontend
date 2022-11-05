package com.kodilla.car_rental.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VinBodyDto {

    @JsonProperty("Make")
    private String manufacturer;

    @JsonProperty("Model")
    private String model;

    @JsonProperty("Fuel")
    private String fuel;

    @JsonProperty("ModelYear")
    private String productYear;

    @JsonProperty("VehicleType")
    private String vehicleType;

}
