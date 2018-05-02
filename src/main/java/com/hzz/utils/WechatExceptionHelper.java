package com.hzz.utils;

import com.hzz.exception.CommonException;
import com.hzz.exception.CommonRuntimeException;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/2
 */
public class WechatExceptionHelper {

    public static CommonException wechatException(String errorMessage, Throwable cause){
        return CommonException.customException("WECHAT", 100000, errorMessage, cause);
    }

    public static CommonRuntimeException wechatRuntimeException(String errorMessage, Throwable cause){
        return CommonRuntimeException.customException("WECHAT", 200000, errorMessage, cause);
    }

    public static CommonException userException(String errorMessage, Throwable cause){
        return CommonException.customException("WECHAT", 101000, errorMessage, cause);
    }

    public static CommonRuntimeException userRuntimeException(String errorMessage, Throwable cause){
        return CommonRuntimeException.customException("WECHAT", 201000, errorMessage, cause);
    }

    public static CommonException privilegeException(String errorMessage, Throwable cause){
        return CommonException.customException("WECHAT", 101403, errorMessage, cause);
    }

    public static CommonRuntimeException privilegeRuntimeException(String errorMessage, Throwable cause){
        return CommonRuntimeException.customException("WECHAT", 201403, errorMessage, cause);
    }

    public static CommonException loginException(String errorMessage, Throwable cause){
        return CommonException.customException("WECHAT", 101701, errorMessage, cause);
    }

}
