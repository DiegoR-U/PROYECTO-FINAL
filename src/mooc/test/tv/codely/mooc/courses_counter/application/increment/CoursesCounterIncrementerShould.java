package tv.codely.mooc.courses_counter.application.increment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tv.codely.mooc.courses.domain.CourseId;
import tv.codely.mooc.courses.domain.CourseIdMother;
import tv.codely.mooc.courses_counter.domain.CoursesCounter;
import tv.codely.mooc.courses_counter.domain.CoursesCounterMother;
import tv.codely.mooc.courses_counter.domain.CoursesCounterRepository;
import tv.codely.shared.domain.UuidGenerator;
import tv.codely.shared.domain.UuidMother;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class CoursesCounterIncrementerShould {

    @Mock
    private CoursesCounterRepository repository;

    @Mock
    private UuidGenerator uuidGenerator;

    @InjectMocks
    private CoursesCounterIncrementer incrementer;

    @Test
    void initialize_a_new_counter_when_none_exists() {
        // Arrange
        String   newId    = UuidMother.random();
        CourseId courseId = CourseIdMother.random();
        when(repository.search()).thenReturn(Optional.empty());
        when(uuidGenerator.generate()).thenReturn(newId);

        // Act
        incrementer.increment(courseId);

        // Assert
        ArgumentCaptor<CoursesCounter> saved = ArgumentCaptor.forClass(CoursesCounter.class);
        verify(repository).save(saved.capture());
        assertEquals(newId, saved.getValue().id().value());
        assertEquals(1, saved.getValue().total().value());
        assertTrue(saved.getValue().hasIncremented(courseId));
    }

    @Test
    void increment_an_existing_counter_without_generating_a_new_id() {
        // Arrange
        CourseId       courseId = CourseIdMother.random();
        CoursesCounter existing = CoursesCounterMother.random();
        CoursesCounter expected = CoursesCounterMother.incrementing(existing, courseId);
        when(repository.search()).thenReturn(Optional.of(existing));

        // Act
        incrementer.increment(courseId);

        // Assert
        verify(repository).save(expected);
        verifyNoInteractions(uuidGenerator);
    }

    @Test
    void not_save_when_the_course_was_already_counted() {
        // Arrange
        CourseId       courseId = CourseIdMother.random();
        CoursesCounter existing = CoursesCounterMother.withOne(courseId);
        when(repository.search()).thenReturn(Optional.of(existing));

        // Act
        incrementer.increment(courseId);

        // Assert
        verify(repository, never()).save(any());
        assertEquals(1, existing.total().value());
    }
}
