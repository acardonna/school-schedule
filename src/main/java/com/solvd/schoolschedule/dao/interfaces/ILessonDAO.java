package com.solvd.schoolschedule.dao.interfaces;

import java.util.List;

import com.solvd.schoolschedule.model.Lesson;

/**
 * Data Access Object interface for Lesson entity operations.
 * Provides CRUD operations for managing individual lessons in the database.
 *
 * <p>A lesson represents a single scheduled class period, containing information about:
 * <ul>
 *   <li>The subject being taught</li>
 *   <li>The teacher teaching the class</li>
 *   <li>The classroom where it takes place</li>
 *   <li>The student group attending</li>
 *   <li>The time slot (day and period)</li>
 * </ul></p>
 *
 * <p>Lessons are always associated with a timetable through the timetable_id foreign key.</p>
 */
public interface ILessonDAO {
    /**
     * Creates a new lesson in the database associated with a timetable.
     *
     * @param lesson the Lesson to insert
     * @param timetableId the ID of the timetable this lesson belongs to
     * @throws RuntimeException if a database error occurs
     */
    void create(Lesson lesson, int timetableId);

    /**
     * Retrieves a lesson by its ID, including all related entities.
     * Loads the associated Group, Teacher, Classroom, and Subject.
     *
     * @param lessonId the unique identifier of the lesson
     * @return the fully populated Lesson object, or null if not found
     * @throws RuntimeException if a database error occurs
     */
    Lesson getById(int lessonId);

    /**
     * Retrieves all lessons from the database.
     *
     * @return a list of all Lesson objects
     * @throws RuntimeException if a database error occurs
     */
    List<Lesson> getAll();

    /**
     * Updates a lesson (currently not supported).
     * Lessons are immutable within a timetable.
     *
     * @param lesson the Lesson to update
     * @throws UnsupportedOperationException always
     */
    void update(Lesson lesson);

    /**
     * Deletes a lesson from the database.
     *
     * @param lessonId the ID of the lesson to delete
     * @throws RuntimeException if a database error occurs
     */
    void delete(int lessonId);

    /**
     * Retrieves all lessons belonging to a specific timetable.
     * Used when loading a timetable with all its lessons.
     *
     * @param timetableId the ID of the timetable
     * @return a list of all lessons in the timetable
     * @throws RuntimeException if a database error occurs
     */
    List<Lesson> getByTimetableId(int timetableId);
}
