package com.solvd.schoolschedule.dao.interfaces;

import java.util.List;

import com.solvd.schoolschedule.model.Subject;

/**
 * Data Access Object interface for Subject entity operations.
 * Provides CRUD operations for managing subjects in the database.
 *
 * <p>Note: Subjects use their enum name (subject_code) as the primary key
 * rather than an auto-generated integer ID.</p>
 */
public interface ISubjectDAO {
    /**
     * Creates a new subject in the database.
     *
     * @param subject the Subject enum to insert
     * @throws RuntimeException if a database error occurs
     */
    void create(Subject subject);

    /**
     * Retrieves a subject by its code.
     *
     * @param subjectCode the subject code (e.g., "MATH", "PHYSICS")
     * @return the Subject enum, or null if not found
     * @throws RuntimeException if a database error occurs
     */
    Subject getById(String subjectCode);

    /**
     * Retrieves all subjects from the database.
     *
     * @return a list of all Subject enums
     * @throws RuntimeException if a database error occurs
     */
    List<Subject> getAll();

    /**
     * Updates an existing subject in the database.
     *
     * @param subject the Subject enum with updated values
     * @throws RuntimeException if a database error occurs
     */
    void update(Subject subject);

    /**
     * Deletes a subject from the database.
     *
     * @param subjectCode the subject code to delete
     * @throws RuntimeException if a database error occurs
     */
    void delete(String subjectCode);
}
