package com.example;

import com.example.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final int PORT = 8080;

    public static void main(String[] args) {
        logger.info("Starting Java Check 404 application on port {}", PORT);
        
        try {
            HttpServer server = new HttpServer(PORT);
            server.start();
            
            logger.info("Application started successfully on port {}", PORT);
            
            // Keep the application running
            server.waitForShutdown();
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }
} 