package com.aabhushan.dao;

import com.aabhushan.model.Order;
import com.aabhushan.model.OrderItem;
import com.aabhushan.model.Product;
import com.aabhushan.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public int placeOrderFromCart(int userId) {
        String cartQuery = "SELECT c.*, p.price FROM cart c JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
        String orderQuery = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?)";
        String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        String clearCartQuery = "DELETE FROM cart WHERE user_id = ?";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Calculate total from cart
            double total = 0;
            List<OrderItem> items = new ArrayList<>();
            try (PreparedStatement psCart = conn.prepareStatement(cartQuery)) {
                psCart.setInt(1, userId);
                ResultSet rs = psCart.executeQuery();
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("price"));
                    items.add(item);
                    total += (item.getPrice() * item.getQuantity());
                }
            }

            if (items.isEmpty()) return -1;
            
            // Add GST and Insurance (Simulated)
            total = total + (total * 0.03) + 2400;

            // 2. Create Order
            int orderId = -1;
            try (PreparedStatement psOrder = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setInt(1, userId);
                psOrder.setDouble(2, total);
                psOrder.setString(3, "CONFIRMED");
                psOrder.executeUpdate();
                ResultSet rs = psOrder.getGeneratedKeys();
                if (rs.next()) orderId = rs.getInt(1);
            }

            // 3. Add Items to Order
            if (orderId != -1) {
                try (PreparedStatement psItem = conn.prepareStatement(itemQuery)) {
                    for (OrderItem item : items) {
                        psItem.setInt(1, orderId);
                        psItem.setInt(2, item.getProductId());
                        psItem.setInt(3, item.getQuantity());
                        psItem.setDouble(4, item.getPrice());
                        psItem.addBatch();
                    }
                    psItem.executeBatch();
                }

                // 4. Clear Cart
                try (PreparedStatement psClear = conn.prepareStatement(clearCartQuery)) {
                    psClear.setInt(1, userId);
                    psClear.executeUpdate();
                }

                conn.commit();
                return orderId;
            }
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return -1;
    }


    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT oi.*, p.name, p.image_url FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getDouble("price"));
                
                Product p = new Product();
                p.setName(rs.getString("name"));
                p.setImageUrl(rs.getString("image_url"));
                item.setProduct(p);
                
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
