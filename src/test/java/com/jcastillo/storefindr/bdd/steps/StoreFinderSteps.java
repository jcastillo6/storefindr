package com.jcastillo.storefindr.bdd.steps;

import com.jcastillo.storefindr.bdd.config.CucumberSpringConfiguration;
import com.jcastillo.storefindr.model.Store;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class StoreFinderSteps {
    private TestRestTemplate restTemplate;
    private CucumberSpringConfiguration config;
    private ResponseEntity<?> response;

    StoreFinderSteps(TestRestTemplate restTemplate,
                     CucumberSpringConfiguration config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    @When("I search for stores near latitude {double} and longitude {double}")
    public void iSearchForStoresNear(double latitude, double longitude) {
        String url = config.getBaseUrl() + "/stores/nearby?latitude=" + latitude + "&longitude=" + longitude;
        response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Store>>() {
                }
        );
    }

    @Then("I should receive a list of {int} store")
    public void iShouldReceiveListOfStores(int count) {
        assertEquals(200, response.getStatusCodeValue());
        List<Store> stores = (List<Store>) response.getBody();
        assertEquals(count, stores.size());
    }

    @Then("the store should be {string}")
    public void theStoreShouldBe(String storeName) {
        List<Store> stores = (List<Store>) response.getBody();
        assertTrue(stores.stream()
                .anyMatch(store -> store.getAddressName().equals(storeName)));
    }

    @Then("I should receive an empty list of stores")
    public void iShouldReceiveEmptyList() {
        assertEquals(200, response.getStatusCodeValue());
        List<Store> stores = (List<Store>) response.getBody();
        assertTrue(stores.isEmpty());
    }

    @Then("I should receive an error with code {string}")
    public void iShouldReceiveError(String errorCode) {
        assertEquals(400, response.getStatusCodeValue());
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertEquals(errorCode, error.get("code"));
    }
}
