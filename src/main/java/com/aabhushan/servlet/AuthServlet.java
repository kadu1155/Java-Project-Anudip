package com.aabhushan.servlet;

import com.aabhushan.dao.UserDAO;
import com.aabhushan.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthServlet extends BaseServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            sendErrorResponse(response, 400, "Missing action");
            return;
        }

        switch (pathInfo) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/signup":
                handleSignup(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                sendErrorResponse(response, 404, "Unknown action");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if ("/session".equals(pathInfo)) {
            handleSessionCheck(request, response);
        } else {
            sendErrorResponse(response, 404, "Not found");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Map<String, String> credentials = gson.fromJson(request.getReader(), Map.class);
            String email = credentials.get("email");
            String password = credentials.get("password");

            if (email == null || password == null) {
                sendErrorResponse(response, 400, "Email and password are required");
                return;
            }

            User user = userDAO.login(email, password);
            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(3600); // 1 hour

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("user", user);
                result.put("message", "Login successful");
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, 401, "Invalid email or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Login failed: " + e.getMessage());
        }
    }

    private void handleSignup(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = gson.fromJson(request.getReader(), User.class);
            if (user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
                sendErrorResponse(response, 400, "Name, email and password are required");
                return;
            }
            if (userDAO.signup(user)) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "Registration successful. Please login.");
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, 400, "Registration failed. Email may already be in use.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Signup failed: " + e.getMessage());
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Logged out successfully");
        sendJsonResponse(response, result);
    }

    private void handleSessionCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Map<String, Object> result = new HashMap<>();
        if (session != null && session.getAttribute("user") != null) {
            result.put("loggedIn", true);
            result.put("user", session.getAttribute("user"));
        } else {
            result.put("loggedIn", false);
            result.put("success", true); // Do NOT throw an error — guests are valid
        }
        sendJsonResponse(response, result);
    }
}
