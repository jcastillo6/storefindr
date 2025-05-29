package com.jcastillo.storefindr.port.input;

import com.jcastillo.storefindr.domain.Store;

import java.util.Set;

public interface FindStoresUseCase {
    Set<Store> findNearbyStores(double latitude, double longitude);
}
