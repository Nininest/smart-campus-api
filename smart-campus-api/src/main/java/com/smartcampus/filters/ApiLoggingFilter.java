package com.smartcampus.filters;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.spi.Container;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * API Request & Response Logging Filter
 */
@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(ApiLoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOG.info(String.format("[REQUEST] %s %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri(),toString()));
    }
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOG.info(String.format("[RESPONSE] %s %s  →  HTTP %d",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri().toString(),
                responseContext.getStatus()));
    }
}

