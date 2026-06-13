package com.calc.winapp.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {
    private static final Gson gson = new Gson();

    public static JsonObject parse(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static String extractValue(JsonObject obj, String... path) {
        JsonObject current = obj;
        for (int i = 0; i < path.length - 1; i++) {
            if (current.has(path[i])) {
                current = current.getAsJsonObject(path[i]);
            } else {
                return null;
            }
        }
        String lastKey = path[path.length - 1];
        if (current.has(lastKey)) {
            return current.get(lastKey).getAsString();
        }
        return null;
    }
}