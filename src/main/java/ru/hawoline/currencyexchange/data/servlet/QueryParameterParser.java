package ru.hawoline.currencyexchange.data.servlet;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryParameterParser {
    public Map<String, List<String>> parseQueryParameters(String query) {
        Map<String, List<String>> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query null or empty");
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            int valueIndex = 1;
            String value = keyValue.length > valueIndex ? URLDecoder.decode(keyValue[valueIndex], StandardCharsets.UTF_8) : null;

            params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return params;
    }
}
