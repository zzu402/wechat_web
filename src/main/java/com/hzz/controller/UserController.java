package com.hzz.controller;

import com.hzz.exception.CommonException;
import com.hzz.model.ConfigInfo;
import com.hzz.model.User;
import com.hzz.security.PrivilegeConstant;
import com.hzz.security.annotation.Privileges;
import com.hzz.service.ConfigService;
import com.hzz.service.UserService;
import com.hzz.utils.RestResultHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    @Autowired
    private ConfigService configService;
    @RequestMapping(value ="/login", method = RequestMethod.POST)
    public Map<String,Object> login(@RequestParam String userName, @RequestParam String password, HttpServletRequest request) throws CommonException {
        Map<String,Object>result= RestResultHelper.success();
        userService.login(userName,password,request,result);
        return result;
    }



    @Privileges(PrivilegeConstant.USER_REGISTER)
    @RequestMapping(value ="/register", method = RequestMethod.POST)
    public Map<String,Object> register(@RequestParam String userName, @RequestParam String password, HttpServletRequest request) throws CommonException {
        Map<String,Object>result= RestResultHelper.success();
        userService.register(userName,"",0,"",password);
        return result;
    }

    @Privileges(PrivilegeConstant.QUERY_USER)
    @RequestMapping(value ="/getUserList", method = RequestMethod.POST)
    public Map<String,Object> getUserList( HttpServletRequest request) throws CommonException {
        Map<String,Object>result= RestResultHelper.success();
        result.put("userList",userService.getUserList());
        return result;
    }
    @Privileges(PrivilegeConstant.QUERY_USER_PROFILE)
    @RequestMapping(value ="/getUserProfile", method = RequestMethod.POST)
    public Map<String,Object> getUserProfile( HttpServletRequest request) throws CommonException {
        Map<String,Object>result= RestResultHelper.success();
        Long userId= (Long) request.getSession().getAttribute("userId");
        List<User> userList=userService.getUserProfile(userId);
        if(userList!=null&&!userList.isEmpty())
        result.put("user",userList.get(0));
        ConfigInfo configInfo=configService.getConfig();
        if(configInfo!=null){
            result.put("urlPassword",configInfo.getPassword());
        }
        return result;
    }

    @Privileges(PrivilegeConstant.LOGIN_ADMIN)
    @RequestMapping(value ="/logout", method = RequestMethod.POST)
    public Map<String,Object> logout( HttpServletRequest request) throws CommonException {
        Map<String,Object>result= RestResultHelper.success();
        request.getSession().removeAttribute("userId");
        return result;
    }
    @Privileges(PrivilegeConstant.UPDATE_PASSWORD)
    @RequestMapping(value ="/addBaiduApiInfo", method = RequestMethod.POST)
    public Map<String,Object> addBaiduApiInfo(@RequestParam String apiId,@RequestParam String apiKey,@RequestParam String secretKey, HttpServletRequest request) throws CommonException {
        Map<String,Object>result= RestResultHelper.success();
        userService.addBaiduApiInfo(apiId,apiKey,secretKey,request);
        return result;
    }

}
