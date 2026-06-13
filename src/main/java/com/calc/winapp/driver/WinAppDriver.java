package com.calc.winapp.driver;

import com.calc.winapp.model.Element;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WinAppDriver {
    private final WinAppDriverClient client;
    private String sessionId;

    public WinAppDriver(String host, int port) {
        this.client = new WinAppDriverClient(host, port);
    }

    // ─── Session ──────────────────────────────────────────────

    public void start(String appPath) throws Exception {
        Map<String, Object> caps = new HashMap<>();
        caps.put("app", appPath);
        caps.put("platformName", "Windows");
        caps.put("deviceName", "WindowsPC");

        Map<String, Object> payload = new HashMap<>();
        payload.put("desiredCapabilities", caps);   // legacy
        payload.put("capabilities", Map.of("firstMatch", new Object[]{new HashMap<>()}, "alwaysMatch", caps)); // W3C

        JsonObject response = client.post("/session", payload);

        // Try W3C format first
        if (response.has("value") && response.get("value").isJsonObject()) {
            JsonObject value = response.getAsJsonObject("value");
            if (value.has("sessionId")) {
                this.sessionId = value.get("sessionId").getAsString();
                return;
            }
        }

        // Fallback to legacy
        if (response.has("sessionId")) {
            this.sessionId = response.get("sessionId").getAsString();
            return;
        }

        throw new RuntimeException("Cannot extract sessionId from: " + response);
    }

    public void quit() throws Exception {
        if (sessionId != null) {
            client.delete("/session/" + sessionId);
            sessionId = null;
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    // ─── Timeouts ─────────────────────────────────────────────

    public void setImplicitWait(int seconds) throws Exception {
        Map<String, Object> payload = Map.of("ms", seconds * 1000);
        client.post("/session/" + sessionId + "/timeouts/implicit_wait", payload);
    }

    // ─── Find Element ─────────────────────────────────────────

    public Element findByAccessibilityId(String automationId) throws Exception {
        return find("accessibility id", automationId);
    }

    public Element findByName(String name) throws Exception {
        return find("name", name);
    }

    public Element findByXPath(String xpath) throws Exception {
        return find("xpath", xpath);
    }

    private Element find(String strategy, String value) throws Exception {
        Map<String, Object> payload = Map.of("using", strategy, "value", value);
        JsonObject response = client.post("/session/" + sessionId + "/element", payload);

        JsonObject valueObj = response.getAsJsonObject("value");

        // Check for W3C error
        if (valueObj.has("error")) {
            throw new RuntimeException("Find failed [" + strategy + "=" + value + "]: " + valueObj.get("error").getAsString());
        }

        // Extract element ID
        for (String key : valueObj.keySet()) {
            if (key.equals("ELEMENT") || key.startsWith("element-")) {
                String elementId = valueObj.get(key).getAsString();
                return new Element(elementId, this);
            }
        }

        throw new RuntimeException("No element ID in response: " + response);
    }

    // ─── Element Actions ────────────────────────────────────────

    public void click(String elementId) throws Exception {
       // client.post("/session/" + sessionId + "/element/" + elementId + "/click", Map.of());

        // 1. 直接触发 Windows 底层的 SetFocus，让控件在不移动鼠标的情况下获取焦点
        client.post("/session/" + sessionId + "/element/" + elementId + "/active", Map.of());

        // 2. 向该元素直接发送 空白键 (\uE00D 是 WebDriver 的 Space 键常量)
        client.post("/session/" + sessionId + "/element/" + elementId + "/value", Map.of(
                "value", List.of("\uE00D")
        ));
    }

    public String getText(String elementId) throws Exception {
        JsonObject response = client.get("/session/" + sessionId + "/element/" + elementId + "/text");
        return response.get("value").getAsString();
    }

    public String getAttribute(String elementId, String attribute) throws Exception {
        JsonObject response = client.get("/session/" + sessionId + "/element/" + elementId + "/attribute/" + attribute);
        return response.get("value").getAsString();
    }
}