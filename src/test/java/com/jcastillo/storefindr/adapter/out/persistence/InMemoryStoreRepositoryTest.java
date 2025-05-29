package com.jcastillo.storefindr.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcastillo.storefindr.domain.Address;
import com.jcastillo.storefindr.domain.Location;
import com.jcastillo.storefindr.domain.OpeningHours;
import com.jcastillo.storefindr.domain.Store;

@ExtendWith(MockitoExtension.class)
class InMemoryStoreRepositoryTest {
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private InMemoryStoreRepository repository;

    @Test
    void initWhenStoresJsonIsValidShouldLoadStores() throws IOException {
        // Given
        String json = """
            {
              "stores": [
                {
                  "city": "TestCity",
                  "postalCode": "12345",
                  "street": "TestStreet",
                  "street2": "1",
                  "street3": "",
                  "latitude": "10.0",
                  "longitude": "20.0",
                  "todayOpen": "08:00",
                  "todayClose": "20:00",
                  "uuid": "uuid-1",
                  "addressName": "Test Store 1",
                  "complexNumber": "1",
                  "showWarningMessage": false,
                  "locationType": "TypeA",
                  "collectionPoint": true,
                  "sapStoreID": "SAP1"
                }
              ]
            }
            """;
        var node = new ObjectMapper().readTree(json);
        given(objectMapper.readTree(any(InputStream.class))).willReturn(node);

        // When
        repository.init();

        // Then
        var result = repository.find5NearbyStores(10.0, 20.0);
        assertFalse(result.isEmpty(), "Should find at least one store");
        var store = result.iterator().next();
        assertEquals("Test Store 1", store.addressName(), "Store name should match");
        assertEquals("uuid-1", store.uuid());
    }

    @Test
    void initWhenStoresJsonIsInvalidShouldThrowPersistenceException() throws IOException {
        // Given
        given(objectMapper.readTree(any(InputStream.class))).willThrow(new IOException("bad json"));

        // When / Then
        assertThrows(PersistenceException.class, () -> repository.init());
    }

    @Test
    void find5NearbyStoresWhenCoordinatesAreValidShouldReturnSortedStores() {
        // Given
        var store1 = new Store("1", "A", new Address("C", "P", "S", "1", ""), new Location(0, 0), "CN", false, new OpeningHours("08:00", "20:00"), "T", false, "SAP1");
        var store2 = new Store("2", "B", new Address("C", "P", "S", "2", ""), new Location(1, 1), "CN", false, new OpeningHours("08:00", "20:00"), "T", false, "SAP2");
        var store3 = new Store("3", "C", new Address("C", "P", "S", "3", ""), new Location(2, 2), "CN", false, new OpeningHours("08:00", "20:00"), "T", false, "SAP3");
        var store4 = new Store("4", "D", new Address("C", "P", "S", "4", ""), new Location(3, 3), "CN", false, new OpeningHours("08:00", "20:00"), "T", false, "SAP4");
        var store5 = new Store("5", "E", new Address("C", "P", "S", "5", ""), new Location(4, 4), "CN", false, new OpeningHours("08:00", "20:00"), "T", false, "SAP5");
        var store6 = new Store("6", "F", new Address("C", "P", "S", "6", ""), new Location(5, 5), "CN", false, new OpeningHours("08:00", "20:00"), "T", false, "SAP6");

        repository = new InMemoryStoreRepository(new ObjectMapper());
        var storesField = getPrivateStoresMap(repository);
        storesField.put(store1.uuid(), store1);
        storesField.put(store2.uuid(), store2);
        storesField.put(store3.uuid(), store3);
        storesField.put(store4.uuid(), store4);
        storesField.put(store5.uuid(), store5);
        storesField.put(store6.uuid(), store6);

        // When
        Set<Store> result = repository.find5NearbyStores(0, 0);

        // Then
        assertEquals(5, result.size());
        assertTrue(result.stream().anyMatch(s -> s.uuid().equals("1")));
        assertTrue(result.stream().anyMatch(s -> s.uuid().equals("2")));
        assertTrue(result.stream().anyMatch(s -> s.uuid().equals("3")));
        assertTrue(result.stream().anyMatch(s -> s.uuid().equals("4")));
        assertTrue(result.stream().anyMatch(s -> s.uuid().equals("5")));
        // Should not contain the 6th farthest store
        assertFalse(result.stream().anyMatch(s -> s.uuid().equals("6")));
    }

    @Test
    void find5NearbyStoresWhenLatitudeIsInvalidShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.find5NearbyStores(-91, 0));
        assertThrows(IllegalArgumentException.class, () -> repository.find5NearbyStores(91, 0));
    }

    @Test
    void find5NearbyStoresWhenLongitudeIsInvalidShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.find5NearbyStores(0, -181));
        assertThrows(IllegalArgumentException.class, () -> repository.find5NearbyStores(0, 181));
    }

    @Test
    void calculateDistanceWhenSameLocationShouldReturnZero() {
        // Given
        var store = new Store("1", "A", new Address("C", "P", "S", "1", ""), new Location(10, 20), "CN", false, new OpeningHours("08:00", "20:00"), "T", false, "SAP1");
        repository = new InMemoryStoreRepository(new ObjectMapper());
        // When
        double distance = invokeCalculateDistance(repository, 10, 20, store);
        // Then
        assertEquals(0.0, distance, 0.0001);
    }

    // --- Helper methods for reflection ---

    @SuppressWarnings("unchecked")
    private Map<String, Store> getPrivateStoresMap(InMemoryStoreRepository repo) {
        try {
            var field = InMemoryStoreRepository.class.getDeclaredField("stores");
            field.setAccessible(true);
            return (Map<String, Store>) field.get(repo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private double invokeCalculateDistance(InMemoryStoreRepository repo, double lat, double lon, Store store) {
        try {
            var method = InMemoryStoreRepository.class.getDeclaredMethod("calculateDistance", double.class, double.class, Store.class);
            method.setAccessible(true);
            return (double) method.invoke(repo, lat, lon, store);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}