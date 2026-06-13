package com.calc.hooks;

import com.calc.winapp.driver.WinAppDriver;
import com.calc.winapp.page.CalculatorPage;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class CalculatorHooks {
    public static WinAppDriver driver;
    public static CalculatorPage calcPage;

    // Windows 11 UWP Calculator
    private static final String CALC_APP = "Microsoft.WindowsCalculator_8wekyb3d8bbwe!App";

    @Before
    public void setUp() throws Exception {
        driver = new WinAppDriver("127.0.0.1", 4723);
        driver.start(CALC_APP);
        driver.setImplicitWait(5);

        Thread.sleep(2000); // Let UI settle

        calcPage = new CalculatorPage(driver);
    }

    @After
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}