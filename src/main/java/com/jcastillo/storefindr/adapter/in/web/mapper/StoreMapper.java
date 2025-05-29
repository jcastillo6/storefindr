package com.jcastillo.storefindr.adapter.in.web.mapper;

import com.jcastillo.storefindr.domain.Store;
import org.springframework.stereotype.Service;

@Service
public class StoreMapper {

    public com.jcastillo.storefindr.model.Store toStoreResponseModel(Store domainStore) {
        return new com.jcastillo.storefindr.model.Store()
                .uuid(domainStore.uuid())
                .addressName(domainStore.addressName())
                .city(domainStore.address().city())
                .postalCode(domainStore.address().postalCode())
                .street(domainStore.address().street())
                .street2(domainStore.address().streetNumber())
                .street3(domainStore.address().additionalInfo())
                .latitude(String.valueOf(domainStore.location().latitude()))
                .longitude(String.valueOf(domainStore.location().longitude()))
                .complexNumber(domainStore.complexNumber())
                .showWarningMessage(domainStore.showWarningMessage())
                .todayOpen(domainStore.openingHours().openTime())
                .todayClose(domainStore.openingHours().closeTime())
                .locationType(domainStore.locationType())
                .collectionPoint(domainStore.collectionPoint())
                .sapStoreID(domainStore.sapStoreID());
    }
}
