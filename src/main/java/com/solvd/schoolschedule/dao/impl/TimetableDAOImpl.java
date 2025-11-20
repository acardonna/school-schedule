package com.solvd.schoolschedule.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.solvd.schoolschedule.dao.interfaces.ITimetableDAO;
import com.solvd.schoolschedule.model.Timetable;
import com.solvd.schoolschedule.util.ConnectionPool;

/**
 * Implementation of {@link ITimetableDAO} for Timetable entity persistence.
 * Handles database operations for timetables and cascades to save associated lessons.
 *
 * <p>This DAO automatically handles the relationship between timetables and lessons.
 * When a timetable is created, all its lessons are also persisted. When a timetable
 * is loaded, all associated lessons are eagerly fetched and added to the timetable object.</p>
 *
 * <p>Timetables are created with auto-generated names based on timestamps and include
 * metadata like fitness score, generation number, and creation timestamp.</p>
 */
public class TimetableDAOImpl implements ITimetableDAO {
    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    /**
     * Creates a timetable and all its lessons in the database.
     * Generates an auto-incrementing ID for the timetable, then uses that ID
     * to save all associated lessons through LessonDAO.
     *
     * <p>The timetable name is auto-generated with a timestamp to ensure uniqueness.</p>
     *
     * @param timetable the Timetable with all lessons to persist
     * @throws RuntimeException if database error occurs
     */
    @Override
    public void create(Timetable timetable) {
        String sql = "INSERT INTO timetable (name, fitness_score, generation_number) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Generate a meaningful name with timestamp
            String name = "Timetable_" + new Timestamp(System.currentTimeMillis()).toString().replace(" ", "_");
            stmt.setString(1, name);
            stmt.setDouble(2, timetable.getFitness());
            stmt.setInt(3, timetable.getGeneration());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int timetableId = rs.getInt(1);
                // Store lessons using LessonDAO
                LessonDAOImpl lessonDAO = new LessonDAOImpl();
                for (var lesson : timetable.getLessons()) {
                    lessonDAO.create(lesson, timetableId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating timetable", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    /**
     * Retrieves a timetable by ID with all its lessons.
     * Loads the timetable metadata first, then fetches all associated lessons.
     *
     * @param timetableId the ID of the timetable
     * @return the Timetable with all lessons loaded, or null if not found
     * @throws RuntimeException if database error occurs
     */
    @Override
    public Timetable getById(int timetableId) {
        String sql = "SELECT timetable_id, fitness_score FROM timetable WHERE timetable_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, timetableId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timetable timetable = new Timetable();
                timetable.setFitness(rs.getDouble("fitness_score"));

                // Load lessons using LessonDAO
                LessonDAOImpl lessonDAO = new LessonDAOImpl();
                var lessons = lessonDAO.getByTimetableId(timetableId);
                for (var lesson : lessons) {
                    timetable.addLesson(lesson);
                }

                return timetable;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting timetable by id", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public List<Timetable> getAll() {
        String sql = "SELECT timetable_id, fitness_score FROM timetable";
        Connection conn = null;
        List<Timetable> timetables = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            LessonDAOImpl lessonDAO = new LessonDAOImpl();

            while (rs.next()) {
                int timetableId = rs.getInt("timetable_id");
                Timetable timetable = new Timetable();
                timetable.setFitness(rs.getDouble("fitness_score"));

                var lessons = lessonDAO.getByTimetableId(timetableId);
                for (var lesson : lessons) {
                    timetable.addLesson(lesson);
                }

                timetables.add(timetable);
            }
            return timetables;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all timetables", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public void update(Timetable timetable) {
        throw new UnsupportedOperationException("Timetable update not supported. Create a new timetable instead.");
    }

    /**
     * Deletes a timetable and all its lessons in a transaction.
     * Deletes lessons first to maintain referential integrity.
     *
     * @param timetableId the ID of the timetable to delete
     * @throws RuntimeException if transaction fails or database error occurs
     */
    @Override
    public void delete(int timetableId) {
        String sqlLessons = "DELETE FROM lesson WHERE timetable_id = ?";
        String sqlTimetable = "DELETE FROM timetable WHERE timetable_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement stmtLessons = conn.prepareStatement(sqlLessons);
            stmtLessons.setInt(1, timetableId);
            stmtLessons.executeUpdate();

            PreparedStatement stmtTimetable = conn.prepareStatement(sqlTimetable);
            stmtTimetable.setInt(1, timetableId);
            stmtTimetable.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            throw new RuntimeException("Error deleting timetable", e);
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
