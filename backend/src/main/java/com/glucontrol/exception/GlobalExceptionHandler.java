package com.glucontrol.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  record ApiError(Instant timestamp, int status, String error, String message, Map<String,String> fields) {}
  @ExceptionHandler(ResourceNotFoundException.class) ResponseEntity<ApiError> notFound(ResourceNotFoundException ex) { return error(HttpStatus.NOT_FOUND,ex.getMessage(),null); }
  @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<ApiError> badRequest(IllegalArgumentException ex) { return error(HttpStatus.BAD_REQUEST,ex.getMessage(),null); }
  @ExceptionHandler(AccessDeniedException.class) ResponseEntity<ApiError> forbidden(AccessDeniedException ex) { return error(HttpStatus.FORBIDDEN,ex.getMessage(),null); }
  @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
    Map<String,String> fields=new LinkedHashMap<>(); ex.getBindingResult().getFieldErrors().forEach(e->fields.put(e.getField(),e.getDefaultMessage()));
    return error(HttpStatus.UNPROCESSABLE_ENTITY,"Revisa los campos enviados",fields);
  }
  @ExceptionHandler(Exception.class) ResponseEntity<ApiError> generic(Exception ex) { return error(HttpStatus.INTERNAL_SERVER_ERROR,"Ocurrió un error inesperado",null); }
  private ResponseEntity<ApiError> error(HttpStatus s,String m,Map<String,String> f) { return ResponseEntity.status(s).body(new ApiError(Instant.now(),s.value(),s.getReasonPhrase(),m,f)); }
}
