package com.jcastillo.storefindr.service;

import com.jcastillo.storefindr.domain.Store;
import com.jcastillo.storefindr.port.input.FindStoresUseCase;
import com.jcastillo.storefindr.port.output.StoreRepository;

import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class StoreFinderService implements FindStoresUseCase {
    private final StoreRepository storeRepository;

    public StoreFinderService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public Set<Store> findNearbyStores(double latitude, double longitude) {
        return storeRepository.find5NearbyStores(latitude, longitude);
    }
}
