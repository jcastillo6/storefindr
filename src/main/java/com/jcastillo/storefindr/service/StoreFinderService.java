package com.jcastillo.storefindr.service;

import com.jcastillo.storefindr.domain.Store;
import com.jcastillo.storefindr.port.input.FindStoresUseCase;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class StoreFinderService implements FindStoresUseCase {

    @Override
    public Set<Store> findNearbyStores(double latitude, double longitude) {
        return null;
    }
}
