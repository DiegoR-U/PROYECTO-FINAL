package tv.codely.mooc.courses_counter.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tv.codely.mooc.courses.domain.CourseId;
import tv.codely.mooc.courses.domain.CourseIdMother;
import tv.codely.shared.domain.UuidMother;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CoursesCounterShould {

    private CoursesCounter counter;

    @BeforeEach
    void setUp() {
        counter = CoursesCounter.initialize(UuidMother.random());
    }

    @AfterEach
    void tearDown() {
        counter = null;
    }

    @Test
    void be_initialized_with_zero_courses() {
        assertEquals(0, counter.total().value());
        assertTrue(counter.existingCourses().isEmpty());
    }

    @Test
    void fail_when_initialized_with_an_invalid_id() {
        assertThrows(IllegalArgumentException.class, () -> CoursesCounter.initialize("no-es-un-uuid"));
    }

    @Test
    void increment_the_total_and_remember_the_course() {
        // Arrange
        CourseId courseId = CourseIdMother.random();

        // Act
        counter.increment(courseId);

        // Assert
        assertEquals(1, counter.total().value());
        assertTrue(counter.hasIncremented(courseId));
    }

    @Test
    void count_different_courses() {
        counter.increment(CourseIdMother.random());
        counter.increment(CourseIdMother.random());
        counter.increment(CourseIdMother.random());

        assertEquals(3, counter.total().value());
        assertEquals(3, counter.existingCourses().size());
    }

    @Test
    void not_have_incremented_a_course_that_was_never_counted() {
        assertFalse(counter.hasIncremented(CourseIdMother.random()));
    }

    @Test
    void not_count_the_same_course_twice() {
        // Arrange
        CourseId courseId = CourseIdMother.random();
        counter.increment(courseId);

        // Act + Assert
        assertThrows(CourseAlreadyCounted.class, () -> counter.increment(courseId));
        assertEquals(1, counter.total().value(), "El total no debe cambiar tras el intento duplicado");
    }

    @Test
    void be_equal_to_a_counter_with_the_same_data() {
        CourseId courseId = CourseIdMother.random();
        CoursesCounter one = CoursesCounterMother.withOne(courseId);
        CoursesCounter copy = CoursesCounterMother.create(one.id(), one.total(), one.existingCourses());

        assertEquals(one, copy);
        assertEquals(one.hashCode(), copy.hashCode());
    }

    @Test
    void not_be_equal_to_a_counter_with_different_courses() {
        assertNotEquals(
            CoursesCounterMother.withOne(CourseIdMother.random()),
            CoursesCounterMother.withOne(CourseIdMother.random())
        );
    }
}
