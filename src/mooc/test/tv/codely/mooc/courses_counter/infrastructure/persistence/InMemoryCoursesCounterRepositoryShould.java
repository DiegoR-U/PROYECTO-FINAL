package tv.codely.mooc.courses_counter.infrastructure.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tv.codely.mooc.courses.domain.CourseIdMother;
import tv.codely.mooc.courses_counter.domain.CoursesCounter;
import tv.codely.mooc.courses_counter.domain.CoursesCounterMother;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class InMemoryCoursesCounterRepositoryShould {

    private InMemoryCoursesCounterRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCoursesCounterRepository();
    }

    @AfterEach
    void tearDown() {
        repository = null;
    }

    @Test
    void return_empty_when_no_counter_was_saved() {
        assertTrue(repository.search().isEmpty());
    }

    @Test
    void return_the_saved_counter() {
        // Arrange
        CoursesCounter counter = CoursesCounterMother.random();

        // Act
        repository.save(counter);

        // Assert
        assertEquals(Optional.of(counter), repository.search());
    }

    @Test
    void keep_only_the_last_saved_counter() {
        CoursesCounter first  = CoursesCounterMother.withOne(CourseIdMother.random());
        CoursesCounter second = CoursesCounterMother.incrementing(first, CourseIdMother.random());

        repository.save(first);
        repository.save(second);

        assertEquals(Optional.of(second), repository.search());
        assertEquals(2, repository.search().get().total().value());
    }
}
