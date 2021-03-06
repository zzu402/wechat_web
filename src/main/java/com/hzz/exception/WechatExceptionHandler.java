package com.hzz.exception;

import com.hzz.utils.RestResultHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@ControllerAdvice
public class WechatExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(WechatExceptionHandler.class);

    @ExceptionHandler({CommonException.class,CommonRuntimeException.class})
    @ResponseBody
    public Map<String,Object> handle(HttpServletRequest request, Exception e) {
        String errorMsg = null;
        String code = "";
        if (e instanceof CommonException) {
            CommonException exception = (CommonException) e;
            errorMsg = exception.getMessage();
            code = exception.getCode();
            logger.error(RestResultHelper.formatException(exception), exception);
        } else if (e instanceof CommonRuntimeException) {
            CommonRuntimeException exception = (CommonRuntimeException) e;
            errorMsg = exception.getMessage();
            code = exception.getCode();
            logger.error(RestResultHelper.formatException(exception), exception);
        }else{
            code="SYSTEM_9999";
            errorMsg=e.getLocalizedMessage();
            logger.error(errorMsg, e);
        }
        Map<String,Object> map = RestResultHelper.fail(code, errorMsg);
        return map;
    }
}
