package com.amilus.apc.domain.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * The BaseResource is an abstract class holds the common object mapper methods that has been used by other resource classes
 */

public abstract class BaseResource
{
    /**
     * This is generic method of parsing JSON string into List of JAVA dto's
     */
    protected final <T> List<T> mapFromJsonList(String json, Class<T> className) throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, className));
    }

    protected final <T> T mapFromJson(String json, Class<T> className) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(json, className);
    }
}
