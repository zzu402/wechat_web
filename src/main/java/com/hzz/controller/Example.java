package com.hzz.controller;

import com.hzz.exception.CommonException;
import com.hzz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Example {

    @Autowired
    private UserService userService;
    @RequestMapping(value ="/home", method = RequestMethod.GET)
    @ResponseBody
    public String home(){
        return "你好，Spring Boot";
    }

    @RequestMapping(value ="/index", method = RequestMethod.GET)
    public String index(){
        return "index";
    }


    @RequestMapping(value ="/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam String name,@RequestParam  String password) throws CommonException {
        userService.login(name,password);
        return "userName:"+name+" userPassword:"+password;
    }





}
