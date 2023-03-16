package com.dropitbusiness.deliveryservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String street;
    private String line1;
    private String line2;
    private String country;
    private String postcode;
}
