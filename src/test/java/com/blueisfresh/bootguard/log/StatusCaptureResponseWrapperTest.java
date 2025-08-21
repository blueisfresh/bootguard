package com.blueisfresh.bootguard.log;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class StatusCaptureResponseWrapperTest {
    @Test
    void testSetStatus() {
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        StatusCaptureResponseWrapper wrapper = new StatusCaptureResponseWrapper(mockResponse);

        wrapper.setStatus(404);

        assertThat(wrapper.getStatus()).isEqualTo(404);
    }

    @Test
    void testSendError() throws Exception {
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        StatusCaptureResponseWrapper wrapper = new StatusCaptureResponseWrapper(mockResponse);

        wrapper.sendError(500, "Internal Server Error");

        assertThat(wrapper.getStatus()).isEqualTo(500);
    }
}
