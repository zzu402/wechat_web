package com.hzz.service;

import com.hzz.cache.CacheManager;
import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.User;
import com.hzz.model.UserRole;
import com.hzz.model.VerifyInfo;
import com.hzz.queue.MqManager;
import com.hzz.security.PrivilegeConstant;
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
    @Autowired
    private  UserRoleService userRoleService;

    public List<VerifyInfo> getVerifyList(HttpServletRequest request) throws CommonException {
        Long userId = (Long) request.getSession().getAttribute("userId");
        User user=userService.getUserById(userId);
        VerifyInfo condition=new VerifyInfo();
        UserRole userRole=userRoleService.getRole(user.getRoleId());
        boolean getAllHistoryPrivilege=false;
        if(userRole!=null&&userRole.getId()!=null){
            getAllHistoryPrivilege=userRoleService.checkPrivilege(userRole.getId(), PrivilegeConstant.QUERY_VERIFY_HISTORY_ALL);
        }
        if(!getAllHistoryPrivilege)
            condition.setUserId(userId);
        condition.orderBy("createTime desc");
        List<VerifyInfo>list=modelDao.select(condition);
        if(list.isEmpty())
            return  null;
        else  return  list;
    }

    public void verify(String k,String phone,String code,HttpServletRequest request) throws CommonException, InterruptedException {
        User user=userService.getUserByUserKey(k);
        if(user==null)
            throw WechatExceptionHelper.wechatException("非法用户验证", null);
//        String sessionId=request.getSession().getId();
//        String expireTime=CacheManager.getCacheService().get(sessionId);
//        Long now=System.currentTimeMillis()/1000;
//        if(StringUtil.isBlank(expireTime)){
//            Long time=now+5*60;
//            CacheManager.getCacheService().set(sessionId,String.valueOf(time),5*60*1000L);
//        }else{
//             Long time= Long.parseLong(expireTime);
//             if(time>now){
//                 throw WechatExceptionHelper.wechatException("您操作太频繁，请5分钟后再试！", null);
//             }
//        }

        commonVerify(user,phone,code);
    }

    public void commonVerify(User u,String phone,String code) throws CommonException, InterruptedException {
        String clientUser = CacheManager.getCacheService().get(String.format("CLIENT_USER_%s", u.getName()));
        if (StringUtil.isBlank(clientUser)) {
            throw WechatExceptionHelper.wechatException("客户端未登陆", null);
        }
        VerifyInfo oldVerifyInfo=new VerifyInfo();
        oldVerifyInfo.setVerifyPhone(phone);
        oldVerifyInfo.orderBy("createTime desc");
        List<VerifyInfo> list=modelDao.select(oldVerifyInfo);
        if (StringUtil.isBlank(phone))
            throw WechatExceptionHelper.wechatException("手机号不能为空", null);
        if (StringUtil.isBlank(code))
            throw WechatExceptionHelper.wechatException("验证码不能为空", null);
        if(list!=null&&!list.isEmpty()){
            oldVerifyInfo=list.get(0);
            Long createTime=oldVerifyInfo.getCreateTime();
            Long now=System.currentTimeMillis()/1000;
            Long restTime=5*60L;
//            if(createTime+restTime>now&&oldVerifyInfo.getStatus()!=3){
//                throw WechatExceptionHelper.wechatException("您操作太频繁，五分钟后重试", null);
//            }
        }
        Long now = System.currentTimeMillis() / 1000;
        VerifyInfo verifyInfo = new VerifyInfo();
        verifyInfo.setUserId(u.getId());
        verifyInfo.setVerifyPhone(phone);
        verifyInfo.setVerifyCode(code);
        verifyInfo.setStatus(0);//0创建
        verifyInfo.setCreateTime(now);
        verifyInfo.setUpdateTime(now);
        verifyInfo = modelDao.insertAndReturn(verifyInfo);
        if (verifyInfo != null) {
            //发起一条消息
            Map<String, Object> verifyMap = new HashMap<>();
            verifyMap.put("userId", u.getId());
            verifyMap.put("verifyInfoId", verifyInfo.getId());
            verifyMap.put("verifyPhone", verifyInfo.getVerifyPhone());
            verifyMap.put("verifyCode", verifyInfo.getVerifyCode());
            MqManager.getMq(String.format("VERIFY_FRIEND")).push(JsonMapper.nonEmptyMapper().toJson(verifyMap));
        }
    }

    public void addFriendAndSendCode(String phone, String code, HttpServletRequest request) throws CommonException, InterruptedException {
        Long userId = (Long) request.getSession().getAttribute("userId");
        User u=userService.getUserById(userId);
        commonVerify(u,phone,code);
    }

    public  void updateVerifyStatus(Long verifyId,Integer status,String comment) throws CommonException {
        VerifyInfo condition=new VerifyInfo();
        condition.setId(verifyId);
        VerifyInfo update=new VerifyInfo();
        update.setStatus(status);
        update.setUpdateTime(System.currentTimeMillis()/1000);
        if(!StringUtil.isBlank(comment))
            update.setComments(comment);
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
                updateVerifyStatus(verifyInfoId,1,"");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (CommonException e) {
                e.printStackTrace();
            }

        }
    }


}
