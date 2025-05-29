package com.jcastillo.storefindr.adapter.out.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcastillo.storefindr.domain.Address;
import com.jcastillo.storefindr.domain.Location;
import com.jcastillo.storefindr.domain.OpeningHours;
import com.jcastillo.storefindr.domain.Store;
import com.jcastillo.storefindr.port.output.StoreRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryStoreRepository implements StoreRepository {
    // kilometers
    public static final double EARTH_RADIUS = 6371;// 5km radius
    public static final double MAX_DISTANCE = 5.0;
    private final Map<String, Store> stores = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public InMemoryStoreRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            var resource = new ClassPathResource("stores.json");
            var storesData = objectMapper.readTree(resource.getInputStream());
            var storesList = storesData.get("stores");

            storesList.forEach(storeNode -> {
                Address address = new Address(
                        storeNode.get("city").asText(),
                        storeNode.get("postalCode").asText(),
                        storeNode.get("street").asText(),
                        storeNode.get("street2").asText(),
                        storeNode.get("street3").asText()
                );

                Location location = new Location(
                        Double.parseDouble(storeNode.get("latitude").asText()),
                        Double.parseDouble(storeNode.get("longitude").asText())
                );

                OpeningHours openingHours = new OpeningHours(
                        storeNode.get("todayOpen").asText(),
                        storeNode.get("todayClose").asText()
                );

                Store store = new Store(
                        storeNode.get("uuid").asText(),
                        storeNode.get("addressName").asText(),
                        address,
                        location,
                        storeNode.get("complexNumber").asText(),
                        storeNode.get("showWarningMessage").asBoolean(),
                        openingHours,
                        storeNode.get("locationType").asText(),
                        storeNode.has("collectionPoint") ?
                                storeNode.get("collectionPoint").asBoolean() :
                                false,
                        storeNode.get("sapStoreID").asText()
                );

                stores.put(store.uuid(), store);
            });
        } catch (IOException e) {
            throw new PersistenceException("Failed to load stores from JSON file", e);
        }
    }

    @Override
    public Set<Store> findNearbyStores(double latitude, double longitude) {
        return stores.values().stream()
                .filter(store -> calculate(latitude, longitude, store))
                .collect(Collectors.toSet());
    }

    private boolean calculate(double latitude, double longitude, Store store) {
        double storeLat = store.location().latitude();
        double storeLon = store.location().longitude();

        double latDistance = Math.toRadians(storeLat - latitude);
        double lonDistance = Math.toRadians(storeLon - longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude))
                * Math.cos(Math.toRadians(storeLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return distance <= MAX_DISTANCE;
    }
}
