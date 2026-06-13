package com.calc.winapp.page;

import com.calc.winapp.driver.WinAppDriver;
import com.calc.winapp.model.Element;

public class CalculatorPage {
    private final WinAppDriver driver;

    // Locators
    public static final String BTN_1 = "num1Button";
    public static final String BTN_3 = "num3Button";
    public static final String BTN_PLUS = "plusButton";
    public static final String BTN_EQUAL = "equalButton";
    public static final String RESULT = "CalculatorResults";

    public CalculatorPage(WinAppDriver driver) {
        this.driver = driver;
    }

    public void press(String automationId) throws Exception {
        Element btn = driver.findByAccessibilityId(automationId);
        btn.click();
    }

    public String getResult() throws Exception {
        Element result = driver.findByAccessibilityId(RESULT);
        String raw = result.getText();

        // Strip "Display is" / "顯示為" / etc., keep only digits
        return raw.replaceAll("[^0-9]", "");
    }
}