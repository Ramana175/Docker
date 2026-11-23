package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloServletTest {

    @Test
    void testApplicationMessage() {

        String message = "Hello from Docker Using Jenkins!";

        assertEquals(
            "Hello from Docker Using Jenkins!",
            message
        );
    }
}