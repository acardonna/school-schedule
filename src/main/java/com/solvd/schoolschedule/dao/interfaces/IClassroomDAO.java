package com.solvd.schoolschedule.dao.interfaces;

import java.util.List;

import com.solvd.schoolschedule.model.Classroom;

/**
 * Data Access Object interface for Classroom entity operations.
 * Provides CRUD operations for managing classrooms and their subject capabilities.
 *
 * <p>Classrooms have a many-to-many relationship with subjects through the
 * classroom_subject_capability table. The DAO handles this relationship automatically.</p>
 */
public interface IClassroomDAO {
    /**
     * Creates a new classroom in the database along with its subject capabilities.
     * Uses a transaction to ensure both the classroom and its capabilities are created atomically.
     *
     * @param classroom the Classroom to insert (including allowedSubjects)
     * @throws RuntimeException if a database error occurs
     */
    void create(Classroom classroom);

    /**
     * Retrieves a classroom by its ID, including all allowed subjects.
     * Loads the classroom's subject capabilities from the junction table.
     *
     * @param classroomId the unique identifier of the classroom
     * @return the Classroom object with its allowed subjects, or null if not found
     * @throws RuntimeException if a database error occurs
     */
    Classroom getById(int classroomId);

    /**
     * Retrieves all classrooms from the database, including their allowed subjects.
     *
     * @return a list of all Classroom objects with their capabilities
     * @throws RuntimeException if a database error occurs
     */
    List<Classroom> getAll();

    /**
     * Updates an existing classroom and its subject capabilities.
     * Removes old capabilities and inserts new ones in a transaction.
     *
     * @param classroom the Classroom with updated values
     * @throws RuntimeException if a database error occurs
     */
    void update(Classroom classroom);

    /**
     * Deletes a classroom and its subject capabilities from the database.
     * Uses a transaction to ensure both are deleted atomically.
     *
     * @param classroomId the ID of the classroom to delete
     * @throws RuntimeException if a database error occurs
     */
    void delete(int classroomId);
}
