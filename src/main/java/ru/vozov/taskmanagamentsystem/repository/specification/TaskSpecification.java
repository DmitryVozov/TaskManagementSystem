package ru.vozov.taskmanagamentsystem.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.vozov.taskmanagamentsystem.model.Task;

import java.util.UUID;

@Component
public class TaskSpecification {
    public static Specification<Task> filterByTitle(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Task> filterByDescription(String description) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"), "%" + description + "%");
    }

    public static Specification<Task> filterByPriority(String priority) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Task> filterByStatus(String status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> filterByAuthor(UUID authorId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("author").get("id"), authorId);
    }

    public static Specification<Task> filterByExecutor(UUID executorId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("executor").get("id"), executorId);
    }
}
