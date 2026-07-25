package com.shopdarcel.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private Long id;
    private String recipientName;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}