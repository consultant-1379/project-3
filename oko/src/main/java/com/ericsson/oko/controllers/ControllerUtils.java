package com.ericsson.oko.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Map;

public class ControllerUtils {

    private static class Messages{
        public static final String NOT_FOUND_BY_ID = "%s with id %d not found";
        public static final String MISSING_PROPERTIES = "Missing mandatory request body properties: %s";
    }

    public static void assertPropertiesList(Map<String, String> properties, String... props){
        var missingProps = new HashSet<String>();
        for(String p: props){
            if(!properties.containsKey(p)) missingProps.add(p);
        }
        if(!missingProps.isEmpty()) throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format(Messages.MISSING_PROPERTIES , missingProps.toString()));
    }

    public static void assertFound(Object o, String msg){
        if(o == null) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                msg
        );
    }

    public static void assertNotNull(String msg, Object... objects){
        for(Object o: objects){
            if(o == null){
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        msg
                );
            }
        }
    }

    public static final String getNotFoundById(){
        return Messages.NOT_FOUND_BY_ID;
    }

    public static final String getMissingProperties(){
        return Messages.MISSING_PROPERTIES;
    }

    private ControllerUtils(){}
}
