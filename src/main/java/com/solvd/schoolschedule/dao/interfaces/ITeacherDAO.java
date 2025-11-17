package com.solvd.schoolschedule.dao.interfaces;

import java.util.List;

import com.solvd.schoolschedule.model.Teacher;

/**
 * Data Access Object interface for Teacher entity operations.
 * Provides CRUD operations for managing teachers in the database.
 *
 * <p>Each teacher is associated with one subject that they teach.</p>
 */
public interface ITeacherDAO {
    /**
     * Creates a new teacher in the database.
     *
     * @param teacher the Teacher to insert
     * @throws RuntimeException if a database error occurs
     */
    void create(Teacher teacher);

    /**
     * Retrieves a teacher by their ID.
     *
     * @param teacherId the unique identifier of the teacher
     * @return the Teacher object, or null if not found
     * @throws RuntimeException if a database error occurs
     */
    Teacher getById(int teacherId);

    /**
     * Retrieves all teachers from the database.
     *
     * @return a list of all Teacher objects
     * @throws RuntimeException if a database error occurs
     */
    List<Teacher> getAll();

    /**
     * Updates an existing teacher in the database.
     *
     * @param teacher the Teacher with updated values
     * @throws RuntimeException if a database error occurs
     */
    void update(Teacher teacher);

    /**
     * Deletes a teacher from the database.
     *
     * @param teacherId the ID of the teacher to delete
     * @throws RuntimeException if a database error occurs
     */
    void delete(int teacherId);
}
