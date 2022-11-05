package com.kodilla.car_rental.frontend.client;

import com.kodilla.car_rental.frontend.config.BackendAppConfig;
import com.kodilla.car_rental.frontend.dto.VinApiDto;
import com.kodilla.car_rental.frontend.dto.VinDecodeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class VinApiClient {

    private final RestTemplate restTemplate;
    private final BackendAppConfig backendAppConfig;

    public VinDecodeDto decodeVinNumber(VinApiDto vinApiDto){
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getVinEndpoint() + "/" + vinApiDto.getVinNumber())
                    .build().encode().toUri();
            VinDecodeDto response = restTemplate.getForObject(url, VinDecodeDto.class);
            return ofNullable(response).orElse(new VinDecodeDto());
        } catch (RestClientException e) {
            return new VinDecodeDto();
        }
    }
}
