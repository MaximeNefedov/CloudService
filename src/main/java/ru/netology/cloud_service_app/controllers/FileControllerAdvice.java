package ru.netology.cloud_service_app.controllers;

import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloud_service_app.exceptions.*;
import ru.netology.cloud_service_app.models.ApiResponse;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class FileControllerAdvice {
    private final int badRequestResponseId = 400;
    private final int notFoundExceptionResponseId = 404;
    private final int internalServerErrorExceptionResponseId = 500;

    @ExceptionHandler(SaveFileException.class)
    public ResponseEntity<ApiResponse> handleSaveFileException(SaveFileException e) {
        val exceptionResponse = new ApiResponse(e.getMessage(), internalServerErrorExceptionResponseId);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException e) {
        val exceptionResponse = new ApiResponse(e.getMessage(), notFoundExceptionResponseId);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DeleteFileException.class)
    public ResponseEntity<ApiResponse> handleDeleteFileException(DeleteFileException e) {
        val exceptionResponse = new ApiResponse(e.getMessage(), internalServerErrorExceptionResponseId);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DownloadFileException.class)
    public ResponseEntity<ApiResponse> handleDownloadFileException(DownloadFileException e) {
        val exceptionResponse = new ApiResponse(e.getMessage(), notFoundExceptionResponseId);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EditFileException.class)
    public ResponseEntity<ApiResponse> handleEditFileException(EditFileException e) {
        val exceptionResponse = new ApiResponse(e.getMessage(), internalServerErrorExceptionResponseId);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException() {
        val exceptionResponse = new ApiResponse("Невалидные данные", badRequestResponseId);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException() {
        val exceptionResponse = new ApiResponse("Новое имя файла невалидно", badRequestResponseId);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
