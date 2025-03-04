package com.ll.TeamProject.standard.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class Json {
    private static final ObjectMapper om = new ObjectMapper();

    @SneakyThrows
    public static String toString(Object obj) {
        return om.writeValueAsString(obj);
    }
}
