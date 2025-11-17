package com.solvd.schoolschedule.dao.interfaces;

import java.util.List;

import com.solvd.schoolschedule.model.Group;

/**
 * Data Access Object interface for StudentGroup (Group) entity operations.
 * Provides CRUD operations for managing student groups in the database.
 *
 * <p>Each group represents a class of students that needs its own timetable.</p>
 */
public interface IStudentGroupDAO {
    /**
     * Creates a new student group in the database.
     *
     * @param group the Group to insert
     * @throws RuntimeException if a database error occurs
     */
    void create(Group group);

    /**
     * Retrieves a student group by its ID.
     *
     * @param groupId the unique identifier of the group
     * @return the Group object, or null if not found
     * @throws RuntimeException if a database error occurs
     */
    Group getById(int groupId);

    /**
     * Retrieves all student groups from the database.
     *
     * @return a list of all Group objects
     * @throws RuntimeException if a database error occurs
     */
    List<Group> getAll();

    /**
     * Updates an existing student group in the database.
     *
     * @param group the Group with updated values
     * @throws RuntimeException if a database error occurs
     */
    void update(Group group);

    /**
     * Deletes a student group from the database.
     *
     * @param groupId the ID of the group to delete
     * @throws RuntimeException if a database error occurs
     */
    void delete(int groupId);
}
