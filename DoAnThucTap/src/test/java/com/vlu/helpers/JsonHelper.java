package com.vlu.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;

public class JsonHelper {
    public static JsonObject readJson(String filePath) {
        try {
            FileReader reader = new FileReader(filePath);
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            System.err.println("Không đọc được file JSON: " + e.getMessage());
            return null;
        }
    }
}