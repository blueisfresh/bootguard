package com.blueisfresh.bootguard.log;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;

/**
 * Wrapper for HttpServletResponse that captures the HTTP status code.
 * Useful for logging filters to know the final response status.
 */

// StatusCaptureResponseWrapper only handles status capturing

public class StatusCaptureResponseWrapper extends HttpServletResponseWrapper {
    private int httpStatus = SC_OK;

    public StatusCaptureResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void setStatus(int sc) {
        super.setStatus(sc);
        this.httpStatus = sc;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        super.sendError(sc, msg);
        this.httpStatus = sc;
    }

    @Override
    public void sendError(int sc) throws IOException {
        super.sendError(sc);
        this.httpStatus = sc;
    }

    public int getStatus() {
        return this.httpStatus;
    }
}
