package ir.baltazzar.backend.custom_file_server.api;

import ir.baltazzar.backend.custom_file_server.service.DownloadException;
import ir.baltazzar.backend.custom_file_server.service.UploadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestResponseEntityExceptionHandler {

    private static final Logger logger = LogManager.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler({UploadException.class, DownloadException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void handleApiValidationError(Exception e) {
        logger.error(e);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleApiConstraintValidationError(ConstraintViolationException e) {
        logger.error(e);
        Map<String, String> messageMap = new HashMap<>();
        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            buildConstraintViolationMessage(messageMap, constraintViolation);
        }
        return messageMap;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void handleOtherException(Exception e) {
        logger.error(e);
    }

    private void buildConstraintViolationMessage(Map<String, String> answerMap, ConstraintViolation<?> violation){
        String key = violation.getPropertyPath().toString().substring(violation.getPropertyPath().toString().lastIndexOf(".")+1);
        String value = violation.getMessage();
        if(key.trim().equals("arg0") || key.trim().equals("arg1") || key.trim().equals("arg2"))
            answerMap.put( "path", value);
        else {
            answerMap.put(key , value);
        }
    }




}
