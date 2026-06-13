package com.calc.winapp.model;

import com.calc.winapp.driver.WinAppDriver;

public class Element {
    private final String id;
    private final WinAppDriver driver;

    public Element(String id, WinAppDriver driver) {
        this.id = id;
        this.driver = driver;
    }

    public String getId() {
        return id;
    }

    public void click() throws Exception {
        driver.click(id);
    }

    public String getText() throws Exception {
        return driver.getText(id);
    }

    public String getAttribute(String name) throws Exception {
        return driver.getAttribute(id, name);
    }
}