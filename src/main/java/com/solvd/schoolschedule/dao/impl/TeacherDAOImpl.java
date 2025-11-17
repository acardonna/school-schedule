package com.solvd.schoolschedule.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.solvd.schoolschedule.dao.interfaces.ITeacherDAO;
import com.solvd.schoolschedule.model.Subject;
import com.solvd.schoolschedule.model.Teacher;
import com.solvd.schoolschedule.util.ConnectionPool;

/**
 * Implementation of {@link ITeacherDAO} for Teacher entity persistence.
 * Handles database operations for teachers and their subject associations.
 *
 * <p>This DAO manages the relationship between teachers and subjects,
 * ensuring each teacher is correctly linked to their teaching subject.</p>
 */
public class TeacherDAOImpl implements ITeacherDAO {
    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public void create(Teacher teacher) {
        String sql = "INSERT INTO teacher (teacher_id, teacher_name, subject_code) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teacher.getId());
            stmt.setString(2, teacher.getName());
            stmt.setString(3, teacher.getSubject().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating teacher", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public Teacher getById(int teacherId) {
        String sql = "SELECT teacher_id, teacher_name, subject_code FROM teacher WHERE teacher_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Teacher(
                    rs.getInt("teacher_id"),
                    rs.getString("teacher_name"),
                    Subject.valueOf(rs.getString("subject_code"))
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting teacher by id", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public List<Teacher> getAll() {
        String sql = "SELECT teacher_id, teacher_name, subject_code FROM teacher";
        Connection conn = null;
        List<Teacher> teachers = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                teachers.add(new Teacher(
                    rs.getInt("teacher_id"),
                    rs.getString("teacher_name"),
                    Subject.valueOf(rs.getString("subject_code"))
                ));
            }
            return teachers;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all teachers", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public void update(Teacher teacher) {
        String sql = "UPDATE teacher SET teacher_name = ?, subject_code = ? WHERE teacher_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, teacher.getName());
            stmt.setString(2, teacher.getSubject().name());
            stmt.setInt(3, teacher.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating teacher", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public void delete(int teacherId) {
        String sql = "DELETE FROM teacher WHERE teacher_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, teacherId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting teacher", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }
}
