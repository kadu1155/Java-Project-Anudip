package com.aabhushan.servlet;

import com.aabhushan.dao.OrderDAO;
import com.aabhushan.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/orders/*")
public class OrderServlet extends BaseServlet {
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, 401, "Login required to view orders");
            return;
        }
        try {
            User user = (User) session.getAttribute("user");
            sendJsonResponse(response, orderDAO.getOrdersByUserId(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Failed to load orders: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, 401, "Login required to place order");
            return;
        }
        try {
            User user = (User) session.getAttribute("user");

            // Read optional shipping details (ignored for now, stored in future)
            // Map<String, Object> orderData = gson.fromJson(request.getReader(), Map.class);

            int orderId = orderDAO.placeOrderFromCart(user.getId());

            Map<String, Object> result = new HashMap<>();
            if (orderId != -1) {
                result.put("success", true);
                result.put("orderId", orderId);
                result.put("message", "Order placed successfully. Your heirloom piece is being prepared.");
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, 400, "Failed to place order. Is your cart empty?");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Order placement failed: " + e.getMessage());
        }
    }
}
