package com.wemarklinks.common;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jsonToMap(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return (Map<String,Object>)mapper.readValue(jsonString, Map.class);
        } catch (IOException e) {
            log.error("cannot convert response to json map, origin string: " + jsonString);
        }
        return null;
    }
}
