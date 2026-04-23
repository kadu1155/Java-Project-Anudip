package com.aabhushan.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class BaseServlet extends HttpServlet {
    protected static final Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws javax.servlet.ServletException, IOException {
        // Allow CORS for local development
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        super.service(request, response);
    }

    protected void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    protected void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        sendJsonResponse(response, new ErrorMessage(message));
    }

    private static class ErrorMessage {
        @SuppressWarnings("unused")
        private final String message;
        @SuppressWarnings("unused")
        private final boolean success = false;
        ErrorMessage(String message) { this.message = message; }
    }
}
