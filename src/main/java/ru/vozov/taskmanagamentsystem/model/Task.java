package ru.vozov.taskmanagamentsystem.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "task")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String title;

    String description;

    @Enumerated(EnumType.STRING)
    Priority priority;

    @Enumerated(EnumType.STRING)
    Status status;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    User author;

    @ManyToOne
    @JoinColumn(name = "executor_id", referencedColumnName = "id")
    User executor;

    @OneToMany(mappedBy = "task")
    List<Comment> comments;

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum Status {
        TODO,
        IN_PROGRESS,
        DONE
    }
}
