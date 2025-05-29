package com.jcastillo.storefindr.adapter.in.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.jcastillo.storefindr.adapter.in.web.mapper.StoreMapper;
import com.jcastillo.storefindr.api.StoresApi;
import com.jcastillo.storefindr.model.Store;
import com.jcastillo.storefindr.port.input.FindStoresUseCase;

@RestController
public class StoreController implements StoresApi {
    private final FindStoresUseCase findStoresUseCase;
    private final StoreMapper storeMapper;

    public StoreController(FindStoresUseCase findStoresUseCase, StoreMapper storeMapper) {
        this.findStoresUseCase = findStoresUseCase;
        this.storeMapper = storeMapper;
    }

    @Override
    public ResponseEntity<List<Store>> findNearbyStores(Double latitude, Double longitude) {
        var stores = findStoresUseCase.findNearbyStores(latitude, longitude);
        if (stores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var storesResponse = stores.stream()
            .map(storeMapper::toStoreResponseModel)
            .toList();
        return ResponseEntity.ok(storesResponse);
    }
}
