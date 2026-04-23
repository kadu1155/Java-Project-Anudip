package com.aabhushan.servlet;

import com.aabhushan.dao.ProductDAO;
import com.aabhushan.model.Product;
import com.aabhushan.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/admin/*")
public class AdminServlet extends BaseServlet {
    private final ProductDAO productDAO = new ProductDAO();

    /** Security gate: only ADMIN role allowed */
    private boolean checkAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, 401, "Unauthorized. Please login.");
            return false;
        }
        User user = (User) session.getAttribute("user");
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            sendErrorResponse(response, 403, "Access denied. Admin role required.");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkAdmin(request, response)) return;
        try {
            String pathInfo = request.getPathInfo();
            if ("/products".equals(pathInfo) || pathInfo == null || "/".equals(pathInfo)) {
                sendJsonResponse(response, productDAO.getAllProducts());
            } else {
                sendErrorResponse(response, 404, "Unknown admin endpoint");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Admin fetch failed: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkAdmin(request, response)) return;
        String pathInfo = request.getPathInfo();
        try {
            if ("/products/add".equals(pathInfo)) {
                Product product = gson.fromJson(request.getReader(), Product.class);
                if (product.getName() == null || product.getPrice() <= 0) {
                    sendErrorResponse(response, 400, "Product name and price are required");
                    return;
                }
                Map<String, Object> result = new HashMap<>();
                if (productDAO.addProduct(product)) {
                    result.put("success", true);
                    result.put("message", "Product added successfully");
                } else {
                    result.put("success", false);
                    result.put("message", "Failed to add product");
                }
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, 404, "Unknown admin endpoint");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Add product failed: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkAdmin(request, response)) return;
        String pathInfo = request.getPathInfo();
        try {
            if ("/products/update".equals(pathInfo)) {
                Product product = gson.fromJson(request.getReader(), Product.class);
                if (product.getId() == 0) {
                    sendErrorResponse(response, 400, "Product ID is required for update");
                    return;
                }
                Map<String, Object> result = new HashMap<>();
                if (productDAO.updateProduct(product)) {
                    result.put("success", true);
                    result.put("message", "Product updated successfully");
                } else {
                    result.put("success", false);
                    result.put("message", "Failed to update product");
                }
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, 404, "Unknown admin endpoint");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Update product failed: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkAdmin(request, response)) return;
        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.startsWith("/products/delete/")) {
                int id = Integer.parseInt(pathInfo.substring(17));
                Map<String, Object> result = new HashMap<>();
                if (productDAO.deleteProduct(id)) {
                    result.put("success", true);
                    result.put("message", "Product deleted successfully");
                } else {
                    result.put("success", false);
                    result.put("message", "Failed to delete product");
                }
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, 404, "Unknown admin endpoint");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, 400, "Invalid product ID");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Delete product failed: " + e.getMessage());
        }
    }
}
