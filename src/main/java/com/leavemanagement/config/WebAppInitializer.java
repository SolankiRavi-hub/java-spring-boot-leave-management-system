package com.leavemanagement.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import io.github.cdimascio.dotenv.Dotenv;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    // Static block to load .env file when application starts
    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            // Optionally log that .env was loaded
            System.out.println("✅ .env file loaded successfully");
        } catch (Exception ex) {
            System.err.println("⚠️  Warning: Could not load .env file: " + ex.getMessage());
            // Don't fail - will use environment variables or defaults
        }
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null; // Root configuration could go here
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { AppConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" }; // Map all requests to DispatcherServlet
    }
}
