package com.jcastillo.storefindr.port.output;

import com.jcastillo.storefindr.domain.Store;

import java.util.Set;

public interface StoreRepository {
    Set<Store> find5NearbyStores(double latitude, double longitude);
}
