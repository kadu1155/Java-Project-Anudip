package com.aabhushan.servlet;

import com.aabhushan.dao.BargainDAO;
import com.aabhushan.model.Bargain;
import com.aabhushan.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/bargains/*")
public class BargainServlet extends BaseServlet {
    private final BargainDAO bargainDAO = new BargainDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, 401, "Login required to view negotiations");
            return;
        }
        try {
            User user = (User) session.getAttribute("user");
            sendJsonResponse(response, bargainDAO.getBargainsByUserId(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Failed to load negotiations: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, 401, "Login required to negotiate");
            return;
        }

        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        Map<String, Object> result = new HashMap<>();

        try {
            if ("/negotiate".equals(pathInfo)) {
                Bargain bargain = gson.fromJson(request.getReader(), Bargain.class);
                bargain.setUserId(user.getId());

                if (bargain.getProductId() == 0 || bargain.getOfferedPrice() <= 0) {
                    sendErrorResponse(response, 400, "Product ID and offered price are required");
                    return;
                }

                if (bargainDAO.placeOffer(bargain)) {
                    result.put("success", true);
                    result.put("message", "Offer submitted to our atelier experts. You will be notified within 24 hours.");
                } else {
                    result.put("success", false);
                    result.put("message", "Failed to submit offer");
                }
                sendJsonResponse(response, result);

            } else {
                sendErrorResponse(response, 404, "Unknown bargain action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Negotiation failed: " + e.getMessage());
        }
    }
}
