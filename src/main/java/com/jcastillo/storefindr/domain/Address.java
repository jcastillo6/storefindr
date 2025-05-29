package com.jcastillo.storefindr.domain;

public record Address(
        String city,
        String postalCode,
        String street,
        String streetNumber,  // street2 in the API
        String additionalInfo // street3 in the API
) {}

