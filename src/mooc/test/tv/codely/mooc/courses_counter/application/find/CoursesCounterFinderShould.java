package tv.codely.mooc.courses_counter.application.find;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tv.codely.mooc.courses_counter.domain.CoursesCounter;
import tv.codely.mooc.courses_counter.domain.CoursesCounterMother;
import tv.codely.mooc.courses_counter.domain.CoursesCounterNotInitialized;
import tv.codely.mooc.courses_counter.domain.CoursesCounterRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class CoursesCounterFinderShould {

    @Mock
    private CoursesCounterRepository repository;

    @InjectMocks
    private CoursesCounterFinder finder;

    @Test
    void return_the_total_of_the_existing_counter() {
        // Arrange
        CoursesCounter counter = CoursesCounterMother.random();
        when(repository.search()).thenReturn(Optional.of(counter));

        // Act
        CoursesCounterResponse response = finder.find();

        // Assert
        assertEquals(counter.total().value(), response.total());
        verify(repository).search();
    }

    @Test
    void fail_when_the_counter_was_not_initialized() {
        when(repository.search()).thenReturn(Optional.empty());

        assertThrows(CoursesCounterNotInitialized.class, () -> finder.find());
    }
}
