package com.solvd.schoolschedule.dao.impl;

import java.sql.*;
import java.util.*;

import com.solvd.schoolschedule.dao.interfaces.IClassroomDAO;
import com.solvd.schoolschedule.model.Classroom;
import com.solvd.schoolschedule.model.Subject;
import com.solvd.schoolschedule.util.ConnectionPool;

/**
 * Implementation of {@link IClassroomDAO} for Classroom entity persistence.
 * Handles database operations for classrooms and their subject capabilities.
 *
 * <p>This DAO manages the complex many-to-many relationship between classrooms
 * and subjects through the classroom_subject_capability junction table.
 * All operations involving this relationship use transactions to ensure data consistency.</p>
 *
 * <p>Key features:
 * <ul>
 *   <li>Transactional create/update/delete operations</li>
 *   <li>Automatic handling of classroom-subject capability mappings</li>
 *   <li>Eager loading of allowed subjects when retrieving classrooms</li>
 * </ul></p>
 */
public class ClassroomDAOImpl implements IClassroomDAO {
    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    /**
     * Creates a classroom and its subject capabilities in a single transaction.
     * First inserts the classroom record, then inserts all capability mappings.
     *
     * @param classroom the Classroom with allowedSubjects to persist
     * @throws RuntimeException if transaction fails or database error occurs
     */
    @Override
    public void create(Classroom classroom) {
        String sqlClassroom = "INSERT INTO classroom (classroom_id, classroom_name) VALUES (?, ?)";
        String sqlCapability = "INSERT INTO classroom_subject_capability (classroom_id, subject_code) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement stmtClassroom = conn.prepareStatement(sqlClassroom);
            stmtClassroom.setInt(1, classroom.getId());
            stmtClassroom.setString(2, classroom.getName());
            stmtClassroom.executeUpdate();

            PreparedStatement stmtCapability = conn.prepareStatement(sqlCapability);
            for (Subject subject : classroom.getAllowedSubjects()) {
                stmtCapability.setInt(1, classroom.getId());
                stmtCapability.setString(2, subject.name());
                stmtCapability.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            throw new RuntimeException("Error creating classroom", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException("Error resetting auto-commit", e);
                }
                connectionPool.releaseConnection(conn);
            }
        }
    }

    /**
     * Retrieves a classroom by ID with all its allowed subjects.
     * Performs two queries: one for the classroom and one for its capabilities.
     *
     * @param classroomId the ID of the classroom
     * @return the Classroom with its allowedSubjects Set populated, or null if not found
     * @throws RuntimeException if database error occurs
     */
    @Override
    public Classroom getById(int classroomId) {
        String sqlClassroom = "SELECT classroom_id, classroom_name FROM classroom WHERE classroom_id = ?";
        String sqlCapabilities = "SELECT subject_code FROM classroom_subject_capability WHERE classroom_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();

            PreparedStatement stmtClassroom = conn.prepareStatement(sqlClassroom);
            stmtClassroom.setInt(1, classroomId);
            ResultSet rsClassroom = stmtClassroom.executeQuery();

            if (rsClassroom.next()) {
                int id = rsClassroom.getInt("classroom_id");
                String name = rsClassroom.getString("classroom_name");

                PreparedStatement stmtCapabilities = conn.prepareStatement(sqlCapabilities);
                stmtCapabilities.setInt(1, classroomId);
                ResultSet rsCapabilities = stmtCapabilities.executeQuery();

                Set<Subject> allowedSubjects = new HashSet<>();
                while (rsCapabilities.next()) {
                    allowedSubjects.add(Subject.valueOf(rsCapabilities.getString("subject_code")));
                }

                return new Classroom(id, name, allowedSubjects);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting classroom by id", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public List<Classroom> getAll() {
        String sqlClassrooms = "SELECT classroom_id, classroom_name FROM classroom";
        String sqlCapabilities = "SELECT subject_code FROM classroom_subject_capability WHERE classroom_id = ?";
        Connection conn = null;
        List<Classroom> classrooms = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();

            PreparedStatement stmtClassrooms = conn.prepareStatement(sqlClassrooms);
            ResultSet rsClassrooms = stmtClassrooms.executeQuery();

            while (rsClassrooms.next()) {
                int id = rsClassrooms.getInt("classroom_id");
                String name = rsClassrooms.getString("classroom_name");

                PreparedStatement stmtCapabilities = conn.prepareStatement(sqlCapabilities);
                stmtCapabilities.setInt(1, id);
                ResultSet rsCapabilities = stmtCapabilities.executeQuery();

                Set<Subject> allowedSubjects = new HashSet<>();
                while (rsCapabilities.next()) {
                    allowedSubjects.add(Subject.valueOf(rsCapabilities.getString("subject_code")));
                }

                classrooms.add(new Classroom(id, name, allowedSubjects));
            }
            return classrooms;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all classrooms", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    /**
     * Updates a classroom and replaces all its subject capabilities in a transaction.
     * Deletes existing capabilities and inserts new ones to ensure consistency.
     *
     * @param classroom the Classroom with updated data and allowedSubjects
     * @throws RuntimeException if transaction fails or database error occurs
     */
    @Override
    public void update(Classroom classroom) {
        String sqlClassroom = "UPDATE classroom SET classroom_name = ? WHERE classroom_id = ?";
        String sqlDeleteCapabilities = "DELETE FROM classroom_subject_capability WHERE classroom_id = ?";
        String sqlInsertCapability = "INSERT INTO classroom_subject_capability (classroom_id, subject_code) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement stmtClassroom = conn.prepareStatement(sqlClassroom);
            stmtClassroom.setString(1, classroom.getName());
            stmtClassroom.setInt(2, classroom.getId());
            stmtClassroom.executeUpdate();

            PreparedStatement stmtDelete = conn.prepareStatement(sqlDeleteCapabilities);
            stmtDelete.setInt(1, classroom.getId());
            stmtDelete.executeUpdate();

            PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertCapability);
            for (Subject subject : classroom.getAllowedSubjects()) {
                stmtInsert.setInt(1, classroom.getId());
                stmtInsert.setString(2, subject.name());
                stmtInsert.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            throw new RuntimeException("Error updating classroom", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException("Error resetting auto-commit", e);
                }
                connectionPool.releaseConnection(conn);
            }
        }
    }

    /**
     * Deletes a classroom and all its capability mappings in a transaction.
     * Removes capabilities first to maintain referential integrity.
     *
     * @param classroomId the ID of the classroom to delete
     * @throws RuntimeException if transaction fails or database error occurs
     */
    @Override
    public void delete(int classroomId) {
        String sqlCapabilities = "DELETE FROM classroom_subject_capability WHERE classroom_id = ?";
        String sqlClassroom = "DELETE FROM classroom WHERE classroom_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement stmtCapabilities = conn.prepareStatement(sqlCapabilities);
            stmtCapabilities.setInt(1, classroomId);
            stmtCapabilities.executeUpdate();

            PreparedStatement stmtClassroom = conn.prepareStatement(sqlClassroom);
            stmtClassroom.setInt(1, classroomId);
            stmtClassroom.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            throw new RuntimeException("Error deleting classroom", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException("Error resetting auto-commit", e);
                }
                connectionPool.releaseConnection(conn);
            }
        }
    }
}
