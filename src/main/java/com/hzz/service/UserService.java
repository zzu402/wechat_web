package com.hzz.service;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/28
 */
@Service
public class UserService {

    @Autowired
    private ModelDao dao;

    public void login(String name,String password) throws CommonException {
        User user=new User();
        user.setName(name);
        List<User> list=dao.select(user);
        if(list==null||list.isEmpty()){
            user.setPassword(password);
            dao.insert(user);
        }
    }



}
