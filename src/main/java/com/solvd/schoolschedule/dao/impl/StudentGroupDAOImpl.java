package com.solvd.schoolschedule.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.solvd.schoolschedule.dao.interfaces.IStudentGroupDAO;
import com.solvd.schoolschedule.model.Group;
import com.solvd.schoolschedule.util.ConnectionPool;

/**
 * Implementation of {@link IStudentGroupDAO} for StudentGroup entity persistence.
 * Handles database operations for student groups using JDBC and PreparedStatements.
 *
 * <p>Each student group represents a class that requires its own timetable.
 * This DAO manages the persistence of group data including name and student count.</p>
 */
public class StudentGroupDAOImpl implements IStudentGroupDAO {
    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public void create(Group group) {
        String sql = "INSERT INTO student_group (group_id, group_name, number_of_students) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, group.getId());
            stmt.setString(2, group.getName());
            stmt.setInt(3, group.getNumberOfStudents());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating student group", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public Group getById(int groupId) {
        String sql = "SELECT group_id, group_name, number_of_students FROM student_group WHERE group_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Group(
                    rs.getInt("group_id"),
                    rs.getString("group_name"),
                    rs.getInt("number_of_students")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting student group by id", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public List<Group> getAll() {
        String sql = "SELECT group_id, group_name, number_of_students FROM student_group";
        Connection conn = null;
        List<Group> groups = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                groups.add(new Group(
                    rs.getInt("group_id"),
                    rs.getString("group_name"),
                    rs.getInt("number_of_students")
                ));
            }
            return groups;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all student groups", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public void update(Group group) {
        String sql = "UPDATE student_group SET group_name = ?, number_of_students = ? WHERE group_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, group.getName());
            stmt.setInt(2, group.getNumberOfStudents());
            stmt.setInt(3, group.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating student group", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public void delete(int groupId) {
        String sql = "DELETE FROM student_group WHERE group_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, groupId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting student group", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }
}
