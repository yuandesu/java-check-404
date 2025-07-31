package com.example.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ApiController {

    public Map<String, Object> alwaysNotFound() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint always returns 404");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "not_found");
        
        return response;
    }

    public String getGreeting() {
        return "Hi ! I know you are using netty.";
    }
} 