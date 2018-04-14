package com.wemarklinks.common;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    
    /**
     * String to <T>
     */
    public static <T> T convertToT(String jsonString, Class<T> clazz){
        ObjectMapper mapper = new ObjectMapper();  
        try {  
            return mapper.readValue(jsonString, clazz);  
        } catch (JsonGenerationException e) {  
            e.printStackTrace();  
        } catch (JsonMappingException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }
    
}
