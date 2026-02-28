package com.svrmslk.authservice.authentication.presentation.exception;

import com.svrmslk.authservice.authentication.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
        problem.setTitle("Invalid Credentials");
        problem.setType(URI.create("https://api.svrmslk.com/errors/invalid-credentials"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(AccountLockedException.class)
    public ProblemDetail handleAccountLocked(AccountLockedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
        problem.setTitle("Account Locked");
        problem.setType(URI.create("https://api.svrmslk.com/errors/account-locked"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problem.setTitle("User Already Exists");
        problem.setType(URI.create("https://api.svrmslk.com/errors/user-exists"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ProblemDetail handleWeakPassword(WeakPasswordException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problem.setTitle("Weak Password");
        problem.setType(URI.create("https://api.svrmslk.com/errors/weak-password"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ProblemDetail handleInvalidEmail(InvalidEmailException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problem.setTitle("Invalid Email");
        problem.setType(URI.create("https://api.svrmslk.com/errors/invalid-email"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed: " + errors
        );
        problem.setTitle("Validation Error");
        problem.setType(URI.create("https://api.svrmslk.com/errors/validation"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("https://api.svrmslk.com/errors/internal"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}