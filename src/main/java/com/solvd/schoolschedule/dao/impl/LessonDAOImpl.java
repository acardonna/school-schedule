package com.solvd.schoolschedule.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.solvd.schoolschedule.dao.interfaces.ILessonDAO;
import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.util.ConnectionPool;

/**
 * Implementation of {@link ILessonDAO} for Lesson entity persistence.
 * Handles database operations for individual lessons within timetables.
 *
 * <p>Lessons are tightly coupled to timetables via the timetable_id foreign key.
 * When retrieving lessons, this DAO eagerly loads all related entities (Group,
 * Teacher, Classroom, Subject) to construct fully populated Lesson objects.</p>
 *
 * <p>Lessons are considered immutable once created, so update operations are
 * not supported. To modify a lesson, delete it and create a new one instead.</p>
 */
public class LessonDAOImpl implements ILessonDAO {
    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    /**
     * Creates a lesson associated with a specific timetable.
     * Extracts all necessary IDs from the lesson's related entities.
     *
     * @param lesson the Lesson to persist with all relationships set
     * @param timetableId the ID of the timetable this lesson belongs to
     * @throws RuntimeException if database error occurs
     */
    @Override
    public void create(Lesson lesson, int timetableId) {
        String sql = "INSERT INTO lesson (timetable_id, group_id, subject_code, teacher_id, classroom_id, day_of_week, period_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, timetableId);
            stmt.setInt(2, lesson.getGroup().getId());
            stmt.setString(3, lesson.getSubject().name());
            stmt.setInt(4, lesson.getTeacher().getId());
            stmt.setInt(5, lesson.getClassroom().getId());
            stmt.setInt(6, lesson.getTimeSlot().getDay());
            stmt.setInt(7, lesson.getTimeSlot().getPeriod());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating lesson", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    /**
     * Retrieves a lesson by ID with all related entities loaded.
     * Uses {@link #buildLessonFromResultSet(ResultSet)} to eagerly fetch Group,
     * Teacher, Classroom, and Subject.
     *
     * @param lessonId the ID of the lesson
     * @return the fully populated Lesson, or null if not found
     * @throws RuntimeException if database error occurs
     */
    @Override
    public Lesson getById(int lessonId) {
        String sql = "SELECT lesson_id, timetable_id, group_id, subject_code, teacher_id, classroom_id, day_of_week, period_number FROM lesson WHERE lesson_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, lessonId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return buildLessonFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting lesson by id", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    /**
     * Retrieves all lessons across all timetables.
     * Each lesson is eagerly loaded with all related entities.
     *
     * @return list of all lessons in the database
     * @throws RuntimeException if database error occurs
     */
    @Override
    public List<Lesson> getAll() {
        String sql = "SELECT lesson_id, timetable_id, group_id, subject_code, teacher_id, classroom_id, day_of_week, period_number FROM lesson";
        Connection conn = null;
        List<Lesson> lessons = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lessons.add(buildLessonFromResultSet(rs));
            }
            return lessons;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all lessons", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    /**
     * Retrieves all lessons for a specific timetable.
     * This is used when loading a complete timetable with all its lessons.
     *
     * @param timetableId the ID of the timetable
     * @return list of lessons belonging to the timetable
     * @throws RuntimeException if database error occurs
     */
    @Override
    public List<Lesson> getByTimetableId(int timetableId) {
        String sql = "SELECT lesson_id, timetable_id, group_id, subject_code, teacher_id, classroom_id, day_of_week, period_number FROM lesson WHERE timetable_id = ?";
        Connection conn = null;
        List<Lesson> lessons = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, timetableId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lessons.add(buildLessonFromResultSet(rs));
            }
            return lessons;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting lessons by timetable id", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public void update(Lesson lesson) {
        throw new UnsupportedOperationException("Lesson update not supported. Lessons are immutable within a timetable.");
    }

    /**
     * Deletes a lesson by ID.
     * Note: When deleting a timetable, all lessons are cascade-deleted,
     * so this method is primarily for individual lesson management.
     *
     * @param lessonId the ID of the lesson to delete
     * @throws RuntimeException if database error occurs
     */
    @Override
    public void delete(int lessonId) {
        String sql = "DELETE FROM lesson WHERE lesson_id = ?";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, lessonId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting lesson", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    /**
     * Helper method to construct a Lesson object from a database ResultSet.
     * Eagerly loads all related entities by creating temporary DAO instances.
     *
     * <p>Loads the following related entities:</p>
     * <ul>
     *   <li>Group - via StudentGroupDAOImpl.getById()</li>
     *   <li>Teacher - via TeacherDAOImpl.getById()</li>
     *   <li>Classroom - via ClassroomDAOImpl.getById()</li>
     *   <li>Subject - via Subject.valueOf() from subject_code</li>
     * </ul>
     *
     * @param rs the ResultSet positioned at a lesson row
     * @return fully populated Lesson object
     * @throws SQLException if error reading ResultSet or loading related entities
     */
    private Lesson buildLessonFromResultSet(ResultSet rs) throws SQLException {
        // Load related entities
        StudentGroupDAOImpl groupDAO = new StudentGroupDAOImpl();
        TeacherDAOImpl teacherDAO = new TeacherDAOImpl();
        ClassroomDAOImpl classroomDAO = new ClassroomDAOImpl();

        Subject subject = Subject.valueOf(rs.getString("subject_code"));
        Group group = groupDAO.getById(rs.getInt("group_id"));
        Teacher teacher = teacherDAO.getById(rs.getInt("teacher_id"));
        Classroom classroom = classroomDAO.getById(rs.getInt("classroom_id"));
        TimeSlot timeSlot = new TimeSlot(rs.getInt("day_of_week"), rs.getInt("period_number"));

        return new Lesson(subject, teacher, classroom, timeSlot, group);
    }
}
