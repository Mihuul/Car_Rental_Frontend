package com.kodilla.car_rental.frontend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BackendAppConfig {

    @Value("${car.endpoint}")
    private String carEndpoint;

    @Value("${login.endpoint}")
    private String loginEndpoint;

    @Value("${rental.endpoint}")
    private String rentalEndpoint;

    @Value("${user.endpoint}")
    private String userEndpoint;

    @Value("${vin.endpoint}")
    private String vinEndpoint;
}
