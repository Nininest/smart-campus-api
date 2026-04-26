package com.smartcampus.application;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application bootstrap class.
 */

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // Jersey scans classpath automatically-no body needed.
}
