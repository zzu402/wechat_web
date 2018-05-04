package com.hzz.controller;

import com.hzz.security.PrivilegeConstant;
import com.hzz.security.annotation.Privileges;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/2
 */

@Controller
public class ProjectPageController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "project/login";
    }

    @Privileges(PrivilegeConstant.LOGIN_ADMIN)
    @RequestMapping(value = {"/index","/",""}, method = RequestMethod.GET)
    public String indexPage() {
        return "project/index";
    }


    @Privileges(PrivilegeConstant.LOGIN_ADMIN)
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String historyPage() {
        return "project/history";
    }

    @Privileges(PrivilegeConstant.LOGIN_ADMIN)
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String RegisterPage() {
        return "project/register";
    }
    @Privileges(PrivilegeConstant.LOGIN_ADMIN)
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String UserPage() {
        return "project/user";
    }

}
