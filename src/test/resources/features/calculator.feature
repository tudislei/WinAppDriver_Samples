Feature: Calculator Basic Math

  Scenario: 1 + 3 = 4
    Given the calculator is open
    When I press "num1Button"
    And I press "plusButton"
    And I press "num3Button"
    And I press "equalButton"
    Then the result should be "4"