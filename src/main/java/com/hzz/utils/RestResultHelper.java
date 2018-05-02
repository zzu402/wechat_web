package com.hzz.utils;

import com.hzz.exception.CommonException;
import com.hzz.exception.CommonRuntimeException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/2
 */
public class RestResultHelper {

    public static Map<String, Object> success() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", "success");
        return map;
    }

    public static Map<String, Object> fail(String code, String errorMsg) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code",code);
        map.put("errorMsg", errorMsg);
        return map;
    }

    public static String formatException(CommonException exception) {
        return String.format("code=%s,errorMsg=%s",exception.getCode(), exception.getErrorMessage());
    }

    public static String formatException(CommonRuntimeException exception) {
        return String.format("code=%s,errorMsg=%s",exception.getCode(), exception.getErrorMessage());
    }

}
