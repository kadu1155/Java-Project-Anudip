package com.aabhushan.dao;

import com.aabhushan.model.Bargain;
import com.aabhushan.model.Product;
import com.aabhushan.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BargainDAO {
    
    public boolean placeOffer(Bargain bargain) {
        String query = "INSERT INTO bargains (user_id, product_id, offered_price, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, bargain.getUserId());
            ps.setInt(2, bargain.getProductId());
            ps.setDouble(3, bargain.getOfferedPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Bargain> getBargainsByUserId(int userId) {
        List<Bargain> bargains = new ArrayList<>();
        String query = "SELECT b.*, p.name, p.price, p.image_url FROM bargains b JOIN products p ON b.product_id = p.id WHERE b.user_id = ? ORDER BY b.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bargain b = extractBargain(rs);
                
                Product p = new Product();
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setImageUrl(rs.getString("image_url"));
                b.setProduct(p);
                
                bargains.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bargains;
    }

    public List<Bargain> getAllBargains() {
        List<Bargain> bargains = new ArrayList<>();
        String query = "SELECT b.*, p.name, p.price FROM bargains b JOIN products p ON b.product_id = p.id ORDER BY b.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Bargain b = extractBargain(rs);
                Product p = new Product();
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                b.setProduct(p);
                bargains.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bargains;
    }

    public boolean updateStatus(int id, String status) {
        String query = "UPDATE bargains SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Bargain extractBargain(ResultSet rs) throws SQLException {
        return new Bargain(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("product_id"),
            rs.getDouble("offered_price"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
    }
}
