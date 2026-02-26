package kg.attractor.java.lesson44;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ParseUrlEncoded {

    public static Map<String, String> parse(String raw, String delimiter) {

        Map<String, String> result = new HashMap<>();

        if (raw == null || raw.isEmpty()) {
            return result;
        }

        String[] pairs = raw.split(delimiter);

        for (String pair : pairs) {

            String[] keyValue = pair.split("=");

            if (keyValue.length != 2) continue;

            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            result.put(key, value);
        }

        return result;
    }
}