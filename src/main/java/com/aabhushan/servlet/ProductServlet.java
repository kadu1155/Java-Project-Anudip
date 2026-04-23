package com.aabhushan.servlet;

import com.aabhushan.dao.ProductDAO;
import com.aabhushan.model.Product;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/products/*")
public class ProductServlet extends BaseServlet {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                // GET /api/products?category=&mood=&occasion=&q=&sort=
                String category = request.getParameter("category");
                String mood     = request.getParameter("mood");
                String occasion = request.getParameter("occasion");
                String search   = request.getParameter("q");
                String sort     = request.getParameter("sort");

                List<Product> products = productDAO.getProducts(category, mood, occasion, search, sort);
                sendJsonResponse(response, products);

            } else if (pathInfo.startsWith("/item/")) {
                // GET /api/products/item/{id}
                int id = Integer.parseInt(pathInfo.substring(6));
                Product product = productDAO.getProductById(id);
                if (product != null) {
                    sendJsonResponse(response, product);
                } else {
                    sendErrorResponse(response, 404, "Product not found");
                }

            } else if ("/search".equals(pathInfo)) {
                // GET /api/products/search?q=
                String q = request.getParameter("q");
                if (q == null || q.isEmpty()) {
                    sendJsonResponse(response, productDAO.getProducts(null, null, null, null, null));
                } else {
                    sendJsonResponse(response, productDAO.searchProducts(q));
                }

            } else {
                sendErrorResponse(response, 404, "Unknown products endpoint");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, 400, "Invalid product ID");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Server error: " + e.getMessage());
        }
    }
}
