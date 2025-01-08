package ru.vozov.taskmanagamentsystem.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vozov.taskmanagamentsystem.dto.CommentCreationDto;
import ru.vozov.taskmanagamentsystem.dto.CommentDto;
import ru.vozov.taskmanagamentsystem.dto.CommentUpdateDto;
import ru.vozov.taskmanagamentsystem.model.Comment;
import ru.vozov.taskmanagamentsystem.service.CommentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("id") UUID id) {
        Comment comment = commentService.findById(id);
        return new ResponseEntity<>(CommentDto.convert(comment), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentCreationDto commentCreationDto) {
        Comment comment = commentService.save(commentCreationDto);
        return new ResponseEntity<>(CommentDto.convert(comment), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") UUID id) {
        commentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("id") UUID id, @RequestBody @Valid CommentUpdateDto commentUpdateDto) {
        Comment comment = commentService.update(id, commentUpdateDto);
        return new ResponseEntity<>(CommentDto.convert(comment), HttpStatus.OK);
    }
}
