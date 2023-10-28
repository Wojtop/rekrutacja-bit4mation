package com.example.tree.rest;

import com.example.tree.exceptions.TreeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TreeExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<TreeErrorResponse> handleException(Exception exc){
        TreeErrorResponse response = new TreeErrorResponse("Unexpected error occurred");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<TreeErrorResponse> handleException(TreeException exc){
        TreeErrorResponse response = new TreeErrorResponse(exc.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
