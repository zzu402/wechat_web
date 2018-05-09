package com.hzz.controller;

import com.hzz.security.PrivilegeConstant;
import com.hzz.security.annotation.Privileges;
import com.hzz.utils.LogUtils;
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
        String fileName = "wechat.zip";
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = res.getOutputStream();
            String root=new File(System.getProperty("user.dir")).getParent();
            LogUtils.info(getClass(),"root url:"+root);
            bis = new BufferedInputStream(new FileInputStream(new File(root+"//download//"
                    + fileName)));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            LogUtils.error(getClass(),"下载出错",e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LogUtils.info(getClass(),"下载成功");
    }
}
