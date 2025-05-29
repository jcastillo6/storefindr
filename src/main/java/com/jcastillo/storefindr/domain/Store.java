package com.jcastillo.storefindr.domain;

public record Store(
        String uuid,
        String addressName,
        Address address,
        Location location,
        String complexNumber,
        boolean showWarningMessage,
        OpeningHours openingHours,
        String locationType,
        boolean collectionPoint,
        String sapStoreID) {

}
