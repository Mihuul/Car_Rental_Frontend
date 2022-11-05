package com.kodilla.car_rental.frontend.client;

import com.kodilla.car_rental.frontend.config.BackendAppConfig;
import com.kodilla.car_rental.frontend.dto.FullRentalDto;
import com.kodilla.car_rental.frontend.dto.RentalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class RentalClient {

    private final RestTemplate restTemplate;
    private final BackendAppConfig backendAppConfig;


    public List<FullRentalDto> getAllRentals() {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getRentalEndpoint()).build().encode().toUri();
            FullRentalDto[] response = restTemplate.getForObject(url, FullRentalDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new FullRentalDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public List<FullRentalDto> getRentalByUserId(Long userId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getRentalEndpoint() + "/" + userId)
                    .build().encode().toUri();
            FullRentalDto[] response = restTemplate.getForObject(url, FullRentalDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new FullRentalDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public void createRental(RentalDto rentalDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getRentalEndpoint()).build().encode().toUri();
        restTemplate.postForObject(url, rentalDto, FullRentalDto.class);
    }


    public void updateRental(RentalDto rentalDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getRentalEndpoint()).build().encode().toUri();
        restTemplate.put(url, rentalDto);
    }

    public void closeRental(Long id) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getRentalEndpoint() + "/" + id)
                .build().encode().toUri();
        restTemplate.delete(url);
    }
}
