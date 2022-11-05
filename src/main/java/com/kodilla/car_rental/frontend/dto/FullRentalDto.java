package com.kodilla.car_rental.frontend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FullRentalDto {
    private Long id;
    private LocalDate rentedFrom;
    private LocalDate rentedUntil;
    private BigDecimal cost;
    private Long carId;
    private String carBrand;
    private String carModel;
    private String userName;
    private String userSurname;
    private String userMail;
    private int userPhoneNumber;
}
