package com.jcastillo.storefindr.bdd.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.jcastillo.storefindr.bdd.config.CucumberSpringConfiguration;
import com.jcastillo.storefindr.model.Error;
import com.jcastillo.storefindr.model.Store;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class StoreFinderSteps {
    private final TestRestTemplate restTemplate;
    private final CucumberSpringConfiguration config;
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
        assertEquals(200, response.getStatusCode().value());
        assertInstanceOf(List.class, response.getBody());
        var stores = (List<Store>) response.getBody();
        assertEquals(count, stores.size());
    }

    @Then("the store should be {string}")
    public void theStoreShouldBe(String storeName) {
        assertInstanceOf(List.class, response.getBody());
        List<Store> stores = (List<Store>) response.getBody();
        assertTrue(stores.stream()
                .anyMatch(store -> store.getAddressName().equals(storeName)));
    }

    @Then("I should receive an empty list of stores")
    public void iShouldReceiveEmptyList() {
        assertEquals(200, response.getStatusCode().value());
        assertInstanceOf(List.class, response.getBody());
        List<Store> stores = (List<Store>) response.getBody();
        assertTrue(stores.isEmpty());
    }

    @Then("I should receive an error with code {string}")
    public void iShouldReceiveError(String errorCode) {
        assertEquals(400, response.getStatusCode().value());
        assertInstanceOf(Error.class, response.getBody());
        var error = (Error) response.getBody();
        assertEquals(errorCode, error.getCode().name());
    }

    @Given("the full list of stores in the system")
    public void theFullListOfStoresInTheSystem() {
        // This step is a placeholder for any setup needed before the tests run.
        // In a real scenario, you might want to ensure that the database is populated with
    }

    @And("the stores should be sorted by distance in the following order:")
    public void validateStoreResponse(DataTable dataTable) {
        assertEquals(200, response.getStatusCode().value());
        assertInstanceOf(List.class, response.getBody());
        var storesResponse = (List<Store>) response.getBody();
        assertEquals(5, storesResponse.size());
        // Extract expected order from the DataTable (skip header row)
        var expectedRows = dataTable.asMaps(String.class, String.class);
        var expectedOrder = expectedRows.stream()
            .map(row -> row.get("storeName"))
            .toList();

        var actualOrder = storesResponse.stream()
            .map(Store::getAddressName)
            .toList();

        assertEquals(expectedOrder, actualOrder, "Stores are not sorted as expected");
    }
}
