package com.aabhushan.dao;

import com.aabhushan.model.LockerItem;
import com.aabhushan.model.Product;
import com.aabhushan.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LockerDAO {
    public boolean saveToLocker(LockerItem item) {
        String query = "INSERT INTO locker (user_id, product_id, event_name, event_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, item.getUserId());
            ps.setInt(2, item.getProductId());
            ps.setString(3, item.getEventName());
            ps.setDate(4, item.getEventDate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LockerItem> getLockerByUserId(int userId) {
        List<LockerItem> lockerItems = new ArrayList<>();
        String query = "SELECT l.*, p.name, p.price, p.image_url FROM locker l JOIN products p ON l.product_id = p.id WHERE l.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LockerItem item = new LockerItem();
                item.setId(rs.getInt("id"));
                item.setUserId(rs.getInt("user_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setEventName(rs.getString("event_name"));
                item.setEventDate(rs.getDate("event_date"));
                
                Product p = new Product();
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setImageUrl(rs.getString("image_url"));
                item.setProduct(p);
                
                lockerItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lockerItems;
    }

    public boolean removeFromLocker(int lockerId) {
        String query = "DELETE FROM locker WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, lockerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
