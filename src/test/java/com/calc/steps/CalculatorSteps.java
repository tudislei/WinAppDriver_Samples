package com.calc.steps;

import com.calc.hooks.CalculatorHooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorSteps {

    @Given("the calculator is open")
    public void the_calculator_is_open() {
        // Already opened in @Before hook
    }

    @When("I press {string}")
    public void i_press(String buttonId) throws Exception {
        CalculatorHooks.calcPage.press(buttonId);
        Thread.sleep(300); // Small pause between clicks
    }

    @Then("the result should be {string}")
    public void the_result_should_be(String expected) throws Exception {
        String actual = CalculatorHooks.calcPage.getResult();
        assertEquals(expected, actual);
    }
}