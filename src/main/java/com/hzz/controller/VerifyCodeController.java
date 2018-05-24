package com.hzz.controller;

import com.hzz.cache.CacheManager;
import com.hzz.exception.CommonException;
import com.hzz.model.VerifyInfo;
import com.hzz.security.PrivilegeConstant;
import com.hzz.security.annotation.Privileges;
import com.hzz.service.VerifyService;
import com.hzz.utils.RestResultHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/2
 */
@RestController
@RequestMapping(value = "/verify")
public class VerifyCodeController {

    @Autowired
    private VerifyService verifyService;

    @Privileges(PrivilegeConstant.LOGIN_ADMIN)
    @RequestMapping(value ="/addFriendAndSendCode", method = RequestMethod.POST)
    public Map<String,Object> addFriendAndSendCode(@RequestParam String phone, @RequestParam String code,HttpServletRequest request) throws CommonException, InterruptedException {
        Map<String,Object>result= RestResultHelper.success();
        verifyService.addFriendAndSendCode(phone,code,request);
        return result;
    }

    @RequestMapping(value ="/common_verify", method = RequestMethod.POST)
    public Map<String,Object> verify(@RequestParam String k,@RequestParam String phone, @RequestParam String code,HttpServletRequest request) throws CommonException, InterruptedException {
        Map<String,Object>result= RestResultHelper.success();
        verifyService.verify(k,phone,code,request);
        return result;
    }


    @Privileges(PrivilegeConstant.QUERY_VERIFY_HISTORY)
    @RequestMapping(value ="/getVerifyList", method = RequestMethod.POST)
    public Map<String,Object> getVerifyList(HttpServletRequest request) throws CommonException, InterruptedException {
        Map<String,Object>result= RestResultHelper.success();
        List<VerifyInfo> list=verifyService.getVerifyList(request);
        if(list!=null){
            result.put("verifyList",list);
        }
        return result;
    }


}
