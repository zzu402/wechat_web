package com.hzz.service;

import com.hzz.cache.CacheManager;
import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.User;
import com.hzz.utils.StringUtil;
import com.hzz.utils.WechatExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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

    @Transactional(rollbackFor = {CommonException.class, RuntimeException.class, Error.class})
    public User login(String userName, String password, HttpServletRequest request) throws CommonException {
        User user = null;
        user = getUserByName(userName);
        if (null == user){
            throw WechatExceptionHelper.userException("账号不存在，请联系QQ:415354918获取账号", null);
        }
        if(StringUtil.isBlank(password) || !password.equalsIgnoreCase(user.getPassword())){
            throw WechatExceptionHelper.wechatRuntimeException("账号或密码错误", null);
        }
        request.getSession().setAttribute("userId",user.getId());
        return user;
    }

    @Transactional(rollbackFor = {CommonException.class, RuntimeException.class, Error.class})
    public User register(String userName, String nickName, int sex, String phone, String password) throws CommonException {
        User condition = null;
        User oldUser = null;
        if (!StringUtil.isBlank(userName)) {
            condition = new User();
            condition.setName(userName);
            List<User> list = dao.select(condition);
            if (list.size() > 0)
                oldUser = list.get(0);
        }
        if (!StringUtil.isBlank(phone)) {
            condition = new User();
            condition.setPhone(phone);
            List<User> list = dao.select(condition);
            if (list.size() > 0) {
                oldUser = list.get(0);
            }
        }
        User user = null;
        long now = System.currentTimeMillis() / 1000;
        if (null != oldUser) {
            if (oldUser.getStatus()==1) {
                user = oldUser;
                User updateUser = new User();
                updateUser.setCreateTime(now);
                updateUser.setName(userName);
                updateUser.setNickName(nickName);
                updateUser.setSex(sex);
                updateUser.setPhone(phone);
                updateUser.setPassword(password);
                updateUser.setStatus(2);

                User userCondition = new User();
                userCondition.setId(user.getId());
                userCondition.setVersion(user.getVersion());
                int count = dao.update(updateUser, userCondition, true);
//                if (count <= 0)
//                    throw InsuranceExceptionHelper.userException("用户信息发生变化，请稍后重试", null);
            }
        } else {
            User newUser = new User();
            newUser.setName(userName);
            newUser.setNickName(nickName);
            newUser.setSex(sex);
            newUser.setCreateTime(now);
            newUser.setPhone(phone);
            newUser.setPassword(password);
            newUser.setStatus(2);
            newUser.setRoleId(2L);

            user = dao.insertAndReturn(newUser);
            User updateUser = new User();


            User userCondition = new User();
            userCondition.setId(user.getId());
            userCondition.setVersion(user.getVersion());
            int count = dao.update(updateUser, userCondition, true);
//            if (count <= 0)
//                throw InsuranceExceptionHelper.userException("用户信息发生变化，请稍后重试", null);
        }
        return user;
    }

    public User getUserByName(String userName) throws CommonException {
        if (StringUtil.isBlank(userName))
            return null;
        User condition = new User();
        condition.setName(userName);
        List<User> list = dao.select(condition);
        if (list.isEmpty())
            return null;
        return list.get(0);
    }

    public User getUserById(Long userId) throws CommonException{
        if(userId==null||userId<=0)
            return  null;
        User condition=new User();
        condition.setId(userId);
        List<User> list = dao.select(condition);
        if (list.isEmpty())
            return null;
        return list.get(0);
    }



}
