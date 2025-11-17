package com.solvd.schoolschedule.util;

import java.sql.*;

/**
 * Utility class for initializing the database with reference data.
 * Populates the database tables with predefined subjects, student groups, teachers,
 * classrooms, and classroom-subject capability mappings.
 *
 * <p>This class performs a check before populating to avoid duplicate insertions.
 * If the database already contains data, the population process is skipped.</p>
 *
 * <p>The following tables are populated:
 * <ul>
 *   <li>subject - 4 subjects (MATH, PHYSICS, INFORMATICS, PHYSICAL_CULTURE)</li>
 *   <li>student_group - 4 groups with 30 students each</li>
 *   <li>teacher - 4 teachers (one per subject)</li>
 *   <li>classroom - 5 classrooms (3 general, 1 physics lab, 1 computer lab)</li>
 *   <li>classroom_subject_capability - Subject-to-classroom mappings</li>
 * </ul></p>
 *
 * @see DatabaseInitializer#populateDatabase()
 */
public class DatabaseInitializer {

    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    /**
     * Populates the database with initial reference data if not already populated.
     * Performs a check on multiple tables to verify if data exists before inserting.
     *
     * <p>This method is idempotent - it can be called multiple times safely.
     * If data already exists, the method returns early without making any changes.</p>
     *
     * <p>All inserts are performed using PreparedStatements to prevent SQL injection
     * and improve performance.</p>
     *
     * @throws RuntimeException if a database error occurs during population
     */
    public void populateDatabase() {
        if (isDatabasePopulated()) {
            System.out.println("=== Database is already populated. Skipping initialization. ===");
            return;
        }

        System.out.println("=== Populating database with initial data... ===");

        Connection conn = null;
        try {
            conn = connectionPool.getConnection();

            insertSubjects(conn);
            insertStudentGroups(conn);
            insertTeachers(conn);
            insertClassrooms(conn);
            insertClassroomSubjectCapabilities(conn);

        } catch (SQLException e) {
            throw new RuntimeException("Error populating database", e);
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }

        System.out.println("=== Database populated successfully! ===");
    }

    /**
     * Checks if the database is already populated by verifying if key tables contain data.
     * Queries the subject table to determine if initialization has already occurred.
     *
     * If any table is empty, the database is considered unpopulated.</p>
     *
     * @return true if all checked tables contain data, false otherwise
     */
    private boolean isDatabasePopulated() {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM subject");
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            return false;
        } catch (SQLException e) {
            // If query fails, assume not populated
            return false;
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    /**
     * Inserts the predefined subjects into the subject table.
     * Populates subjects with their display names and weekly lesson counts.
     *
     * <p>Subjects inserted:
     * <ul>
     *   <li>MATH - Mathematics (5 weekly lessons)</li>
     *   <li>PHYSICS - Physics (4 weekly lessons)</li>
     *   <li>INFORMATICS - Informatics (3 weekly lessons)</li>
     *   <li>PHYSICAL_CULTURE - Physical Culture (2 weekly lessons)</li>
     * </ul></p>
     *
     * @param conn the database connection to use
     * @throws SQLException if a database error occurs
     */
    private void insertSubjects(Connection conn) throws SQLException {
        System.out.println("=== Inserting subjects... ===");
        String sql = "INSERT INTO subject (subject_code, display_name, weekly_lessons) VALUES (?, ?, ?)";

        String[][] subjects = {
            {"MATH", "Mathematics", "5"},
            {"PHYSICS", "Physics", "4"},
            {"INFORMATICS", "Informatics", "3"},
            {"PHYSICAL_CULTURE", "Physical Culture", "2"}
        };

        PreparedStatement stmt = conn.prepareStatement(sql);
        for (String[] subject : subjects) {
            stmt.setString(1, subject[0]);
            stmt.setString(2, subject[1]);
            stmt.setInt(3, Integer.parseInt(subject[2]));
            stmt.executeUpdate();
        }
        System.out.println("=== Inserted " + subjects.length + " subjects" + " ===");
    }

    /**
     * Inserts the predefined student groups into the student_group table.
     * Creates 4 groups, each with 30 students.
     *
     * @param conn the database connection to use
     * @throws SQLException if a database error occurs
     */
    private void insertStudentGroups(Connection conn) throws SQLException {
        System.out.println("=== Inserting student groups... ===");
        String sql = "INSERT INTO student_group (name, student_count) VALUES (?, ?)";

        String[][] groups = {
            {"Group 1", "30"},
            {"Group 2", "30"},
            {"Group 3", "30"},
            {"Group 4", "30"}
        };

        PreparedStatement stmt = conn.prepareStatement(sql);
        for (String[] group : groups) {
            stmt.setString(1, group[0]);
            stmt.setInt(2, Integer.parseInt(group[1]));
            stmt.executeUpdate();
        }
        System.out.println("=== Inserted " + groups.length + " student groups" + " ===");
    }

    /**
     * Inserts the predefined teachers into the teacher table.
     * Creates one teacher per subject.
     *
     * <p>Teachers created:
     * <ul>
     *   <li>Mr. Smith - Mathematics</li>
     *   <li>Ms. Johnson - Physics</li>
     *   <li>Dr. Brown - Informatics</li>
     *   <li>Mrs. Davis - Physical Culture</li>
     * </ul></p>
     *
     * @param conn the database connection to use
     * @throws SQLException if a database error occurs
     */
    private void insertTeachers(Connection conn) throws SQLException {
        System.out.println("=== Inserting teachers... ===");
        String sql = "INSERT INTO teacher (name, subject_code) VALUES (?, ?)";

        String[][] teachers = {
            {"Mr. Smith", "MATH"},
            {"Ms. Johnson", "PHYSICS"},
            {"Dr. Brown", "INFORMATICS"},
            {"Mrs. Davis", "PHYSICAL_CULTURE"}
        };

        PreparedStatement stmt = conn.prepareStatement(sql);
        for (String[] teacher : teachers) {
            stmt.setString(1, teacher[0]);
            stmt.setString(2, teacher[1]);
            stmt.executeUpdate();
        }
        System.out.println("=== Inserted " + teachers.length + " teachers" + " ===");
    }

    /**
     * Inserts the predefined classrooms into the classroom table.
     * Creates a mix of general-purpose rooms and specialized labs.
     *
     * <p>Classrooms created:
     * <ul>
     *   <li>Room 101, 102, 103 - General purpose classrooms</li>
     *   <li>Physics Lab - Specialized for physics classes</li>
     *   <li>Computer Lab - Specialized for informatics classes</li>
     * </ul></p>
     *
     * @param conn the database connection to use
     * @throws SQLException if a database error occurs
     */
    private void insertClassrooms(Connection conn) throws SQLException {
        System.out.println("=== Inserting classrooms... ===");
        String sql = "INSERT INTO classroom (name, room_type) VALUES (?, ?)";

        String[][] classrooms = {
            {"Room 101", "GENERAL"},
            {"Room 102", "GENERAL"},
            {"Room 103", "GENERAL"},
            {"Physics Lab", "PHYSICS_LAB"},
            {"Computer Lab", "COMPUTER_LAB"}
        };

        PreparedStatement stmt = conn.prepareStatement(sql);
        for (String[] classroom : classrooms) {
            stmt.setString(1, classroom[0]);
            stmt.setString(2, classroom[1]);
            stmt.executeUpdate();
        }
        System.out.println("=== Inserted " + classrooms.length + " classrooms" + " ===");
    }

    /**
     * Inserts classroom-subject capability mappings into the classroom_subject_capability table.
     * Defines which subjects can be taught in which classrooms.
     *
     * <p>Capability mappings:
     * <ul>
     *   <li>Rooms 101-103: Can accommodate MATH, PHYSICS, and PHYSICAL_CULTURE</li>
     *   <li>Physics Lab: Can only accommodate PHYSICS</li>
     *   <li>Computer Lab: Can only accommodate INFORMATICS</li>
     * </ul></p>
     *
     * <p>This creates a many-to-many relationship between classrooms and subjects,
     * ensuring that specialized subjects are only scheduled in appropriate facilities.</p>
     *
     * @param conn the database connection to use
     * @throws SQLException if a database error occurs
     */
    private void insertClassroomSubjectCapabilities(Connection conn) throws SQLException {
        System.out.println("=== Inserting classroom-subject capabilities... ===");
        String sql = "INSERT INTO classroom_subject_capability (classroom_id, subject_code) VALUES (?, ?)";

        int[][] capabilities = {
            {1, 1}, {1, 2}, {1, 4},  // Room 101: MATH, PHYSICS, PHYSICAL_CULTURE
            {2, 1}, {2, 2}, {2, 4},  // Room 102: MATH, PHYSICS, PHYSICAL_CULTURE
            {3, 1}, {3, 2}, {3, 4},  // Room 103: MATH, PHYSICS, PHYSICAL_CULTURE
            {4, 2},                   // Physics Lab: PHYSICS
            {5, 3}                    // Computer Lab: INFORMATICS
        };

        String[] subjectCodes = {"MATH", "PHYSICS", "INFORMATICS", "PHYSICAL_CULTURE"};

        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int[] capability : capabilities) {
            stmt.setInt(1, capability[0]);
            stmt.setString(2, subjectCodes[capability[1] - 1]);
            stmt.executeUpdate();
        }
        System.out.println("=== Inserted " + capabilities.length + " classroom-subject capabilities" + " ===");
    }
}
