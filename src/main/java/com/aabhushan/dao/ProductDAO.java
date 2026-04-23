package com.aabhushan.dao;

import com.aabhushan.model.Product;
import com.aabhushan.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getProducts(String category, String mood, String occasion, String search, String sort) {
        List<Product> products = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM products WHERE 1=1");
        
        if (category != null && !category.isEmpty()) query.append(" AND category = ?");
        if (mood != null && !mood.isEmpty()) query.append(" AND mood = ?");
        if (occasion != null && !occasion.isEmpty()) query.append(" AND occasion = ?");
        if (search != null && !search.isEmpty()) query.append(" AND (name LIKE ? OR description LIKE ?)");

        if (sort != null) {
            switch (sort) {
                case "price_asc": query.append(" ORDER BY price ASC"); break;
                case "price_desc": query.append(" ORDER BY price DESC"); break;
                case "latest": query.append(" ORDER BY id DESC"); break;
                default: query.append(" ORDER BY id DESC");
            }
        } else {
            query.append(" ORDER BY id DESC");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
            int i = 1;
            if (category != null && !category.isEmpty()) ps.setString(i++, category);
            if (mood != null && !mood.isEmpty()) ps.setString(i++, mood);
            if (occasion != null && !occasion.isEmpty()) ps.setString(i++, occasion);
            if (search != null && !search.isEmpty()) {
                String searchParam = "%" + search + "%";
                ps.setString(i++, searchParam);
                ps.setString(i++, searchParam);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> searchProducts(String name) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getProductById(int id) {
        String query = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getDouble("price"),
            rs.getString("category"),
            rs.getString("mood"),
            rs.getString("occasion"),
            rs.getString("personality"),
            rs.getString("image_url"),
            rs.getString("description"),
            rs.getInt("stock")
        );
    }

    /* Admin CRUD Operations */

    public boolean addProduct(Product product) {
        String query = "INSERT INTO products (name, price, category, mood, occasion, personality, image_url, description, stock) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setString(3, product.getCategory());
            ps.setString(4, product.getMood());
            ps.setString(5, product.getOccasion());
            ps.setString(6, product.getPersonality());
            ps.setString(7, product.getImageUrl());
            ps.setString(8, product.getDescription());
            ps.setInt(9, product.getStock());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String query = "UPDATE products SET name=?, price=?, category=?, mood=?, occasion=?, personality=?, image_url=?, description=?, stock=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setString(3, product.getCategory());
            ps.setString(4, product.getMood());
            ps.setString(5, product.getOccasion());
            ps.setString(6, product.getPersonality());
            ps.setString(7, product.getImageUrl());
            ps.setString(8, product.getDescription());
            ps.setInt(9, product.getStock());
            ps.setInt(10, product.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        String query = "DELETE FROM products WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
