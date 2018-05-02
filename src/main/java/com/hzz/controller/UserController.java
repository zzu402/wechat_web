package com.hzz.controller;

import com.hzz.exception.CommonException;
import com.hzz.service.UserService;
import com.hzz.utils.RestResultHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/2
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;
    @RequestMapping(value ="/login", method = RequestMethod.POST)
    public Map<String,Object> login(@RequestParam String userName, @RequestParam String password, HttpServletRequest request) throws CommonException {
        Map<String,Object>result= RestResultHelper.success();
        userService.login(userName,password,request);
        return result;
    }
}
