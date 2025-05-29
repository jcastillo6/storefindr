package com.jcastillo.storefindr.port.output;

import com.jcastillo.storefindr.domain.Store;

import java.util.Set;

public interface StoreRepository {
    Set<Store> findNearbyStores(double latitude, double longitude);
}
