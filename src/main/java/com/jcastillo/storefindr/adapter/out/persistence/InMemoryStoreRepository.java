package com.jcastillo.storefindr.adapter.out.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcastillo.storefindr.domain.Address;
import com.jcastillo.storefindr.domain.Location;
import com.jcastillo.storefindr.domain.OpeningHours;
import com.jcastillo.storefindr.domain.Store;
import com.jcastillo.storefindr.port.output.StoreRepository;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryStoreRepository implements StoreRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryStoreRepository.class);
    private static final double EARTH_RADIUS = 6371; // Radius of the Earth in kilometers
    private static final int MAX_SIZE = 5;
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
                    storeNode.has("collectionPoint") && storeNode.get("collectionPoint").asBoolean(),
                    storeNode.get("sapStoreID").asText()
                );

                stores.put(store.uuid(), store);

            });
        } catch (IOException e) {
            throw new PersistenceException("Failed to load stores from JSON file", e);
        }
        log.info("InMemoryStoreRepository initialized with {} stores", stores.size());
    }

    @Override
    public Set<Store> find5NearbyStores(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees and longitude must be between -180 and 180 degrees");
        }

        return stores.values().stream()
            .map(store -> Map.entry(store, calculateDistance(latitude, longitude, store)))
            .sorted(Map.Entry.comparingByValue())
            .limit(MAX_SIZE)
            .map(Map.Entry::getKey)
            .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    private double calculateDistance(double latitude, double longitude, Store store) {
        // Haversine formula to calculate the distance between two points on the Earth
        // https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
        double storeLat = store.location().latitude();
        double storeLon = store.location().longitude();

        double latDistance = Math.toRadians(storeLat - latitude);
        double lonDistance = Math.toRadians(storeLon - longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(latitude))
            * Math.cos(Math.toRadians(storeLat))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
