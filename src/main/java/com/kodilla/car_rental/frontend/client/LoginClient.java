package com.kodilla.car_rental.frontend.client;

import com.kodilla.car_rental.frontend.config.BackendAppConfig;
import com.kodilla.car_rental.frontend.dto.LoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class LoginClient {

    private final RestTemplate restTemplate;
    private final BackendAppConfig backendAppConfig;

    public Boolean isLoginRegistered(LoginDto loginDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getLoginEndpoint() + "/is_registered")
                .queryParam("email", loginDto.getEmail())
                .queryParam("password", loginDto.getPassword())
                .build().encode().toUri();
        return restTemplate.getForObject(url, Boolean.class);
    }
}
