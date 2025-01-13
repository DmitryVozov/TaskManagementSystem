package ru.vozov.taskmanagamentsystem.exception.handler;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.vozov.taskmanagamentsystem.dto.ErrorDto;
import ru.vozov.taskmanagamentsystem.exception.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ErrorDto(400, LocalDateTime.now(), Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorDto handleJsonErrors(HttpMessageNotReadableException e){
        return new ErrorDto(400, LocalDateTime.now(), e.getLocalizedMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto accessDeniedException(AccessDeniedException e) {
        return new ErrorDto(403, LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(ChangePasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto changePasswordException(ChangePasswordException e) {
        return new ErrorDto(400, LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(BlankFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto fieldIsBlankException(BlankFieldException e) {
        return new ErrorDto(400, LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto emailAlreadyExistsException(EmailAlreadyExistsException e) {
        return new ErrorDto(400, LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(IncorrectExecutorRoleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto incorrectExecutorRoleException(IncorrectExecutorRoleException e) {
        return new ErrorDto(400, LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(NoDataToUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto noDataToUpdateException(NoDataToUpdateException e) {
        return new ErrorDto(400, LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto resourceNotFoundException(ResourceNotFoundException e) {
        return new ErrorDto(404, LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(SignInException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto signInException(SignInException e) {
        return new ErrorDto(401, LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto taskNotFoundException(TaskNotFoundException e) {
        return new ErrorDto(400, LocalDateTime.now(), e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto userNotFoundException(UserNotFoundException e) {
        return new ErrorDto(400, LocalDateTime.now(), e.getMessage());
    }
}
