package com.aabhushan.servlet;

import com.aabhushan.dao.CartDAO;
import com.aabhushan.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/cart/*")
public class CartServlet extends BaseServlet {
    private final CartDAO cartDAO = new CartDAO();

    private User getAuthenticatedUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, 401, "Login required for cart operations");
            return null;
        }
        return (User) session.getAttribute("user");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) return;
        try {
            sendJsonResponse(response, cartDAO.getCartByUserId(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Failed to load cart: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) return;

        String pathInfo = request.getPathInfo();
        Map<String, Object> result = new HashMap<>();

        try {
            if ("/add".equals(pathInfo)) {
                Map<String, Object> body = gson.fromJson(request.getReader(), Map.class);
                int productId = ((Number) body.get("productId")).intValue();
                int quantity  = body.containsKey("quantity") ? ((Number) body.get("quantity")).intValue() : 1;

                if (cartDAO.addToCart(user.getId(), productId, quantity)) {
                    result.put("success", true);
                    result.put("message", "Added to cart");
                } else {
                    result.put("success", false);
                    result.put("message", "Failed to add to cart");
                }
                sendJsonResponse(response, result);

            } else if ("/update".equals(pathInfo)) {
                Map<String, Object> body = gson.fromJson(request.getReader(), Map.class);
                int cartId   = ((Number) body.get("cartId")).intValue();
                int quantity = ((Number) body.get("quantity")).intValue();

                if (quantity <= 0) {
                    if (cartDAO.removeItem(cartId)) {
                        result.put("success", true);
                        result.put("message", "Item removed");
                    } else {
                        result.put("success", false);
                    }
                } else {
                    result.put("success", cartDAO.updateQuantity(cartId, quantity));
                }
                sendJsonResponse(response, result);

            } else if (pathInfo != null && pathInfo.startsWith("/remove/")) {
                int cartId = Integer.parseInt(pathInfo.substring(8));
                result.put("success", cartDAO.removeItem(cartId));
                sendJsonResponse(response, result);

            } else {
                sendErrorResponse(response, 404, "Unknown cart action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Cart operation failed: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) return;
        // DELETE /api/cart/clear
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("success", cartDAO.clearCart(user.getId()));
            sendJsonResponse(response, result);
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Failed to clear cart");
        }
    }
}
