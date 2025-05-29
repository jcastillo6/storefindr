package com.jcastillo.storefindr.adapter.in.web;

import com.jcastillo.storefindr.adapter.in.web.mapper.StoreMapper;
import com.jcastillo.storefindr.api.StoresApi;
import com.jcastillo.storefindr.model.Store;
import com.jcastillo.storefindr.port.input.FindStoresUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StoreController implements StoresApi {
    private final FindStoresUseCase findStoresUseCase;
    private final StoreMapper storeMapper;

    public StoreController(FindStoresUseCase findStoresUseCase, StoreMapper storeMapper) {
        this.findStoresUseCase = findStoresUseCase;
        this.storeMapper = storeMapper;
    }

    @Override
    public ResponseEntity<List<Store>> findNearbyStores(@Valid @NotNull Double latitude,
                                                             @Valid @NotNull Double longitude) {

        return StoresApi.super.findNearbyStores(latitude, longitude);
    }
}
