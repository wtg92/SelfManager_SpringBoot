package manager.config;

import manager.exception.SMException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SMException.class)
    public ResponseEntity<Map<String,Object>> handleException(SMException e) {
        Map<String,Object> rlt = new HashMap<>();
        rlt.put("code",e.type.code);
        rlt.put("params",e.params);
        // 这里可以根据实际情况定义返回的异常信息
        return new ResponseEntity<>(rlt, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
