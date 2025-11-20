package com.solvd.schoolschedule.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.solvd.schoolschedule.dao.interfaces.ISubjectDAO;
import com.solvd.schoolschedule.model.Subject;
import com.solvd.schoolschedule.util.ConnectionPool;

/**
 * Implementation of {@link ISubjectDAO} for Subject entity persistence.\n * Handles database operations for subjects using JDBC and PreparedStatements.\n * \n * <p>This DAO uses the ConnectionPool for database connections and ensures\n * proper resource management by releasing connections in finally blocks.</p>\n
 */
public class SubjectDAOImpl implements ISubjectDAO {
    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public void create(Subject subject) {
        String sql = "INSERT INTO subject (subject_code, subject_name) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, subject.name());
            stmt.setString(2, subject.getDisplayName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating subject", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public Subject getById(String subjectCode) {
        String sql = "SELECT subject_code, subject_name FROM subject WHERE subject_code = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, subjectCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Subject.valueOf(rs.getString("subject_code"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting subject by id", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public List<Subject> getAll() {
        String sql = "SELECT subject_code, subject_name FROM subject";
        Connection conn = null;
        List<Subject> subjects = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subjects.add(Subject.valueOf(rs.getString("subject_code")));
            }
            return subjects;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all subjects", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public void update(Subject subject) {
        String sql = "UPDATE subject SET subject_name = ? WHERE subject_code = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, subject.getDisplayName());
            stmt.setString(2, subject.name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating subject", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public void delete(String subjectCode) {
        String sql = "DELETE FROM subject WHERE subject_code = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, subjectCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting subject", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }
}
