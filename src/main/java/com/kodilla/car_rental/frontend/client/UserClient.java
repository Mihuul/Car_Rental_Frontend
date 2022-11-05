package com.kodilla.car_rental.frontend.client;

import com.kodilla.car_rental.frontend.config.BackendAppConfig;
import com.kodilla.car_rental.frontend.dto.UserDto;
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
public class UserClient {

    private final RestTemplate restTemplate;
    private final BackendAppConfig backendAppConfig;


    public List<UserDto> getUsers() {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getUserEndpoint())
                    .build().encode().toUri();
            UserDto[] response = restTemplate.getForObject(url, UserDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new UserDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public UserDto getUserByMail(String email) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getUserEndpoint() + "/mail/" + email)
                    .build().encode().toUri();
            UserDto response = restTemplate.getForObject(url, UserDto.class);
            return ofNullable(response).orElse(new UserDto());
        } catch (RestClientException e) {
            return new UserDto();
        }
    }

    public UserDto getUserByPhoneNumber(String phoneNumber) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getUserEndpoint() + "/phone/" + phoneNumber)
                    .build().encode().toUri();
            UserDto response = restTemplate.getForObject(url, UserDto.class);
            return ofNullable(response).orElse(new UserDto());
        } catch (RestClientException e) {
            return new UserDto();
        }
    }
    public Boolean doesUserExist(String email) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getUserEndpoint() + "/exist")
                .queryParam("email", email)
                .build().encode().toUri();
        return restTemplate.getForObject(url, Boolean.class);
    }

    public void createUser(UserDto userDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getUserEndpoint())
                .build().encode().toUri();
        restTemplate.postForObject(url, userDto, UserDto.class);
    }

    public void updateUser(UserDto userDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getUserEndpoint()).build().encode().toUri();
        restTemplate.put(url, userDto);
    }

    public void deleteUser(Long id) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendAppConfig.getUserEndpoint() + "/" + id)
                .build().encode().toUri();
        restTemplate.delete(url);
    }
}
