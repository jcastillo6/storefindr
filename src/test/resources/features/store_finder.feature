# src/test/resources/features/store_finder.feature
Feature: Find nearby stores
  As a user
  I want to find stores near my location
  So that I can visit them

  Scenario: Find stores within range
    Given there are stores in the system:
      | uuid                     | name                                    | latitude  | longitude | type         |
      | EOgKYx4XFiQAAAFJa_YYZ4At | Jumbo 's Gravendeel Gravendeel Centrum | 51.778461 | 4.615551  | SupermarktPuP |
      | EOgKYx4XFiQAAAFJa_YYZ4Bt | Jumbo Rotterdam Centrum                | 51.924420 | 4.477733  | SupermarktPuP |
    When I search for stores near latitude 51.778000 and longitude 4.615000
    Then I should receive a list of 1 store
    And the store should be "Jumbo 's Gravendeel Gravendeel Centrum"

  Scenario: No stores found in range
    Given there are stores in the system:
      | uuid                     | name                                    | latitude  | longitude | type         |
      | EOgKYx4XFiQAAAFJa_YYZ4At | Jumbo 's Gravendeel Gravendeel Centrum | 51.778461 | 4.615551  | SupermarktPuP |
    When I search for stores near latitude 52.778000 and longitude 5.615000
    Then I should receive an empty list of stores

  Scenario: Invalid coordinates
    When I search for stores near latitude 91.000000 and longitude 4.615000
    Then I should receive an error with code "INVALID_COORDINATES"