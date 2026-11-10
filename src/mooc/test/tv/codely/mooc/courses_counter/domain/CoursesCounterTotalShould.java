package tv.codely.mooc.courses_counter.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class CoursesCounterTotalShould {

    @Test
    void start_at_zero_when_initialized() {
        // Act
        CoursesCounterTotal total = CoursesCounterTotal.initialize();

        // Assert
        assertEquals(0, total.value());
    }

    @Test
    void return_a_new_total_plus_one_when_incremented() {
        // Arrange
        CoursesCounterTotal total = new CoursesCounterTotal(4);

        // Act
        CoursesCounterTotal incremented = total.increment();

        // Assert
        assertEquals(5, incremented.value());
        assertEquals(4, total.value(), "El total original no debe cambiar (value object inmutable)");
    }

    @Test
    void fail_when_value_is_negative() {
        assertThrows(IllegalArgumentException.class, () -> new CoursesCounterTotal(-1));
    }

    @Test
    void be_equal_to_another_total_with_the_same_value() {
        assertEquals(new CoursesCounterTotal(3), new CoursesCounterTotal(3));
    }
}
