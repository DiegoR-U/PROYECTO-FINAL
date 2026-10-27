package tv.codely.mooc.courses_counter.infrastructure.persistence;

import tv.codely.mooc.courses_counter.domain.CoursesCounter;
import tv.codely.mooc.courses_counter.domain.CoursesCounterRepository;

import java.util.Optional;

public final class InMemoryCoursesCounterRepository implements CoursesCounterRepository {
    private CoursesCounter counter;

    @Override
    public void save(CoursesCounter counter) {
        this.counter = counter;
    }

    @Override
    public Optional<CoursesCounter> search() {
        return Optional.ofNullable(counter);
    }
}
