
package com.example;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("text/html");

    try {
        response.getWriter().println(
            "<h1>Hello from Docker Using Jenkins!</h1>"
        );
    } catch (IOException e) {
        getServletContext().log("Unable to write response", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
}

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

