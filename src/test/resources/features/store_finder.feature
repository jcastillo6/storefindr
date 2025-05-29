# src/test/resources/features/store_finder.feature
Feature: Find nearby stores
  As a user
  I want to find stores near my location
  So that I can visit them

  Scenario: Find stores near a specific location
    Given the full list of stores in the system
    When I search for stores near latitude 51.778000 and longitude 4.615000
    And the stores should be sorted by distance in the following order:
      | order | storeName                              |
      | 1     | Jumbo 's Gravendeel Gravendeel Centrum |
      | 2     | Jumbo Dordrecht Slangenburg            |
      | 3     | Jumbo Dordrecht P.A. de Kokplein       |
      | 4     | Jumbo Zwijndrecht Walburg              |
      | 5     | Jumbo Papendrecht Meent Passage        |

  Scenario: No stores found in range
    Given the full list of stores in the system
    When I search for stores near latitude 52.778000 and longitude 5.615000
    Then I should receive an empty list of stores

  Scenario: Invalid coordinates
    When I search for stores near latitude 91.000000 and longitude 4.615000
    Then I should receive an error with code "INVALID_COORDINATES"