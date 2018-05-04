package com.hzz.service;

import com.hzz.cache.CacheManager;
import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.User;
import com.hzz.model.VerifyInfo;
import com.hzz.queue.MqManager;
import com.hzz.utils.JsonMapper;
import com.hzz.utils.RestResultHelper;
import com.hzz.utils.StringUtil;
import com.hzz.utils.WechatExceptionHelper;
import com.hzz.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/2
 */
@Service
public class VerifyService {

    @Autowired
    private ModelDao modelDao;
    @Autowired
    private UserService userService;

    public List<VerifyInfo> getVerifyList(HttpServletRequest request) throws CommonException {
        Long userId = (Long) request.getSession().getAttribute("userId");
        VerifyInfo condition=new VerifyInfo();
        condition.setUserId(userId);
        condition.orderBy("createTime desc");
        List<VerifyInfo>list=modelDao.select(condition);
        if(list.isEmpty())
            return  null;
        else  return  list;
    }

    public void addFriendAndSendCode(String phone, String code, HttpServletRequest request) throws CommonException, InterruptedException {
        Long userId = (Long) request.getSession().getAttribute("userId");
        User u=userService.getUserById(userId);
        String clientUser = CacheManager.getCacheService().get(String.format("CLIENT_USER_%s", u.getName()));
        if (StringUtil.isBlank(clientUser)) {
            throw WechatExceptionHelper.wechatException("客户端未登陆", null);
        }
        if (StringUtil.isBlank(phone))
            throw WechatExceptionHelper.wechatException("手机号不能为空", null);
        if (StringUtil.isBlank(code))
            throw WechatExceptionHelper.wechatException("验证码不能为空", null);
        Long now = System.currentTimeMillis() / 1000;
        VerifyInfo verifyInfo = new VerifyInfo();
        verifyInfo.setUserId(userId);
        verifyInfo.setVerifyPhone(phone);
        verifyInfo.setVerifyCode(code);
        verifyInfo.setStatus(0);//0创建
        verifyInfo.setCreateTime(now);
        verifyInfo.setUpdateTime(now);
        verifyInfo = modelDao.insertAndReturn(verifyInfo);
        if (verifyInfo != null) {
            //发起一条消息
            Map<String, Object> verifyMap = new HashMap<>();
            verifyMap.put("userId", userId);
            verifyMap.put("verifyInfoId", verifyInfo.getId());
            verifyMap.put("verifyPhone", verifyInfo.getVerifyPhone());
            verifyMap.put("verifyCode", verifyInfo.getVerifyCode());
            MqManager.getMq(String.format("VERIFY_FRIEND")).push(JsonMapper.nonEmptyMapper().toJson(verifyMap));
        }
    }

    public  void updateVerifyStatus(Long verifyId,Integer status) throws CommonException {
        VerifyInfo condition=new VerifyInfo();
        condition.setId(verifyId);
        VerifyInfo update=new VerifyInfo();
        update.setStatus(status);
        update.setUpdateTime(System.currentTimeMillis()/1000);
        modelDao.update(update,condition);
    }

    public void doVerifyOperation() {
        while (true) {//不断地处理消息队列
            try {
                String json = MqManager.getMq(String.format("VERIFY_FRIEND")).pop();
                Map<String, Object> verifyMap = JsonMapper.nonEmptyMapper().fromJson(json, Map.class);
                Long userId = (Integer) verifyMap.get("userId")*1L;
                Long verifyInfoId = (Integer) verifyMap.get("verifyInfoId")*1L;
                String phone = (String) verifyMap.get("verifyPhone");
                String code = (String) verifyMap.get("verifyCode");
                User user = userService.getUserById(userId);
                WebSocketServer socketServer=WebSocketServer.getWebSocket(user.getName());
                socketServer.sendMessage(json);
                updateVerifyStatus(verifyInfoId,1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (CommonException e) {
                e.printStackTrace();
            }

        }
    }


}
