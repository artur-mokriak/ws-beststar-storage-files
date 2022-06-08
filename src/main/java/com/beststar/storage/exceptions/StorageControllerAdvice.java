package com.beststar.storage.exceptions;

import com.beststar.storage.entity.MessageResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class StorageControllerAdvice {

	@ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<MessageResponse> handleAllException(Exception ex) {
		return new ResponseEntity<MessageResponse>(new MessageResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);  
    }
    @ExceptionHandler(MethodArgumentNotValidException.class) 
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<MessageResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return new ResponseEntity<MessageResponse>(new MessageResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class) 
    @ResponseStatus(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<MessageResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return new ResponseEntity<MessageResponse>(new MessageResponse(ex.getMessage()), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class) 
    @ResponseStatus(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<MessageResponse> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        return new ResponseEntity<MessageResponse>(new MessageResponse(ex.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<MessageResponse> handleStorageException(StorageException ex) {
        return new ResponseEntity<MessageResponse>(new MessageResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }    
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public MessageResponse handleMaxSizeException(MaxUploadSizeExceededException exc) {
      return new MessageResponse("File too large!");
    }    
}
