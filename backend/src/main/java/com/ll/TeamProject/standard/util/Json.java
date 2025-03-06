package com.ll.TeamProject.standard.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {
    private static final ObjectMapper om = new ObjectMapper();

    public static String toString(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }
}
