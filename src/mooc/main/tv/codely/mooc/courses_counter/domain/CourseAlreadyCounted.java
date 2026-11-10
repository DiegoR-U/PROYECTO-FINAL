package tv.codely.mooc.courses_counter.domain;

import tv.codely.mooc.courses.domain.CourseId;
import tv.codely.shared.domain.DomainError;

public final class CourseAlreadyCounted extends DomainError {
    public CourseAlreadyCounted(CourseId id) {
        super("course_already_counted", String.format("The course <%s> was already counted", id.value()));
    }
}
