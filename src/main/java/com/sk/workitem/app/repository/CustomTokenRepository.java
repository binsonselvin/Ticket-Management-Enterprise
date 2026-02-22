package com.sk.workitem.app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Repository;

@Repository
public class CustomTokenRepository implements org.springframework.security.web.authentication.rememberme.PersistentTokenRepository {

	@Autowired
    private final DataSource dataSource;

    public CustomTokenRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
    	System.out.println("TOKEN: createNewToken Triggered");
        String sql = "INSERT INTO persistent_logins (username, series, token, last_used) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token.getUsername());
            ps.setString(2, token.getSeries());
            ps.setString(3, token.getTokenValue());
            ps.setTimestamp(4, new java.sql.Timestamp(token.getDate().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating token", e);
        }
    }

    @Override
    public void updateToken(String seriesId, String tokenValue, Date lastUsed) {
    	System.out.println("TOKEN: updateToken Triggered");
        String sql = "UPDATE persistent_logins SET token = ?, last_used = ? WHERE series = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tokenValue);
            ps.setTimestamp(2, new java.sql.Timestamp(lastUsed.getTime()));
            ps.setString(3, seriesId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating token", e);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
    	System.out.println("TOKEN: getTokenForSeries Triggered");
        String sql = "SELECT * FROM persistent_logins WHERE series = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seriesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String token = rs.getString("token");
                    Date lastUsed = rs.getTimestamp("last_used");
                    return new PersistentRememberMeToken(username, seriesId, token, lastUsed);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving token", e);
        }
        return null;
    }

    @Override
    public void removeUserTokens(String username) {
    	System.out.println("TOKEN: removeUserTokens Triggered");
        String sql = "DELETE FROM persistent_logins WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing tokens", e);
        }
    }
}