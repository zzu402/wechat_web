package com.hzz.controller;

import com.hzz.exception.CommonException;
import com.hzz.model.ConfigInfo;
import com.hzz.security.PrivilegeConstant;
import com.hzz.security.annotation.Privileges;
import com.hzz.service.ConfigService;
import com.hzz.utils.LogUtils;
import com.hzz.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/2
 */

@Controller
public class ProjectPageController {
    @Autowired
    private ConfigService configService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "project/login";
    }

    @Privileges(PrivilegeConstant.LOGIN_ADMIN)
    @RequestMapping(value = {"/index","/",""}, method = RequestMethod.GET)
    public String indexPage() {
        return "project/index";
    }


    @Privileges(PrivilegeConstant.QUERY_VERIFY_HISTORY)
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String historyPage() {
        return "project/history";
    }

    @Privileges(PrivilegeConstant.USER_REGISTER)
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String RegisterPage() {
        return "project/register";
    }
    @Privileges(PrivilegeConstant.QUERY_USER)
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String UserPage() {
        return "project/user";
    }


    @Privileges(PrivilegeConstant.QUERY_USER_PROFILE)
    @RequestMapping(value = "/user_profile", method = RequestMethod.GET)
    public String UserProfilePage() {
        return "project/user_profile";
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public String verifyPage() {
        return "project/verify";
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(HttpServletRequest request, HttpServletResponse res) {
        try {
            ConfigInfo configInfo=configService.getConfig();
            if(!StringUtil.isBlank(configInfo.getUrl()))
                 res.sendRedirect(configInfo.getUrl());
        } catch (IOException e) {
            LogUtils.error(getClass(),"跳转到百度网盘失败",e);
        } catch (CommonException e) {
            LogUtils.error(getClass(),"获取配置信息失败",e);
        }
    }
}
