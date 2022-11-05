package com.kodilla.car_rental.frontend.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private int phoneNumber;
    private LocalDate creationDate;
}
