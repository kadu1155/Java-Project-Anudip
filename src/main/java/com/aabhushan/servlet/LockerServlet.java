package com.aabhushan.servlet;

import com.aabhushan.dao.LockerDAO;
import com.aabhushan.model.LockerItem;
import com.aabhushan.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/locker/*")
public class LockerServlet extends BaseServlet {
    private final LockerDAO lockerDAO = new LockerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, 401, "Login required to view locker");
            return;
        }
        try {
            User user = (User) session.getAttribute("user");
            sendJsonResponse(response, lockerDAO.getLockerByUserId(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Failed to load locker: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, 401, "Login required for locker operations");
            return;
        }

        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        Map<String, Object> result = new HashMap<>();

        try {
            if ("/add".equals(pathInfo)) {
                LockerItem item = gson.fromJson(request.getReader(), LockerItem.class);
                item.setUserId(user.getId());
                if (lockerDAO.saveToLocker(item)) {
                    result.put("success", true);
                    result.put("message", "Piece saved to your digital locker");
                } else {
                    result.put("success", false);
                    result.put("message", "Failed to save to locker");
                }
                sendJsonResponse(response, result);

            } else if (pathInfo != null && pathInfo.startsWith("/remove/")) {
                int lockerId = Integer.parseInt(pathInfo.substring(8));
                result.put("success", lockerDAO.removeFromLocker(lockerId));
                sendJsonResponse(response, result);

            } else {
                sendErrorResponse(response, 404, "Unknown locker action");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, 400, "Invalid locker item ID");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Locker operation failed: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, 401, "Login required");
            return;
        }
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/remove/")) {
            try {
                int lockerId = Integer.parseInt(pathInfo.substring(8));
                Map<String, Object> result = new HashMap<>();
                result.put("success", lockerDAO.removeFromLocker(lockerId));
                sendJsonResponse(response, result);
            } catch (NumberFormatException e) {
                sendErrorResponse(response, 400, "Invalid ID");
            }
        } else {
            sendErrorResponse(response, 404, "Unknown endpoint");
        }
    }
}
