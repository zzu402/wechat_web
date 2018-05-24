package com.hzz.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.hzz.cache.CacheManager;
import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.User;
import com.hzz.service.UserService;
import com.hzz.service.VerifyService;
import com.hzz.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//@ServerEndpoint(value = "/websocket")
@ServerEndpoint("/websocket/{user}")
@Component
public class WebSocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private static Map<String,WebSocketServer> webSocketMap=new ConcurrentHashMap<>();
    private Session session;
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam("user") String user, Session session) {
        this.session = session;
        webSocketMap.put(user,this);
        addOnlineCount();           //在线数加1
        LogUtils.info(WebSocketServer.class,"有新连接加入，当前在线人数:"+getOnlineCount());
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("user")String user) {
        CacheManager.getCacheService().delete(String.format("CLIENT_USER_%s", user));
        webSocketMap.remove(user);
        subOnlineCount();           //在线数减1
        LogUtils.info(WebSocketServer.class,"有连接退出，当前在线人数:"+getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(@PathParam("user")String user,String message, Session session) {
        LogUtils.info(WebSocketServer.class,"接收到来自 "+user+" 的消息:"+message);
        Map<String,Object> verifyMap= JsonMapper.nonEmptyMapper().fromJson(message, Map.class);
        String secretKey= (String) verifyMap.get("secretkey");
        String resultCode= (String) verifyMap.get("resultCode");
        String errorMsg= (String) verifyMap.get("errorMsg");
        if(!StringUtil.isBlank(resultCode)){
            Long verifyInfoId = (Integer) verifyMap.get("verifyInfoId")*1L;
            Integer status =3;
            if(resultCode.equals("success")){
                status=2;
            }
            VerifyService verifyService= (VerifyService) SpringUtils.getBean(VerifyService.class);
            try {
                verifyService.updateVerifyStatus(verifyInfoId,status,errorMsg);
            } catch (CommonException e) {
                LogUtils.error(WebSocketServer.class,"更新验证表单失败",e);
            }
        }else {
            if (!StringUtil.isBlank(secretKey)) {
                try {
                    UserService userService = (UserService) SpringUtils.getBean(UserService.class);
                    User u = userService.getUserByName(user);
                    Long expireTime=u.getExpireTime();
                    Long now=System.currentTimeMillis()/1000;
                    if (u.getSecretKey().equals(secretKey)) {
                        if(now<expireTime) {
                            CacheManager.getCacheService().set(String.format("CLIENT_USER_%s", user), u.getId().toString());
                            Map<String, Object> result = RestResultHelper.success();
                            if (!StringUtil.isBlank(u.getBaiduApiId()))
                                result.put("baiduApiId", u.getBaiduApiId());
                            else {
                                result.put("baiduApiId", "11164162");
                            }
                            if (!StringUtil.isBlank(u.getBaiduApiKey()))
                                result.put("baiduApiKey", u.getBaiduApiKey());
                            else {
                                result.put("baiduApiKey", "QsXNTssGCtucKWtaSQa8fHwv");
                            }
                            if (!StringUtil.isBlank(u.getBaiduSecretKey()))
                                result.put("baiduApiSecretKey", u.getBaiduSecretKey());
                            else {
                                result.put("baiduApiSecretKey", "z5KVwRWuwWYYhTVeGDB4jWDeosYxb66G");
                            }
                            sendMessage(JsonMapper.nonDefaultMapper().toJson(result));
                        }else {
                            CacheManager.getCacheService().delete(String.format("CLIENT_USER_%s", user));
                            webSocketMap.remove(user);
                            sendMessage(JsonMapper.nonDefaultMapper().toJson(RestResultHelper.fail("302", "客户端到期,请联系Q：415354918")));
                        }
                    } else {
                        CacheManager.getCacheService().delete(String.format("CLIENT_USER_%s", user));
                        webSocketMap.remove(user);
                        sendMessage(JsonMapper.nonDefaultMapper().toJson(RestResultHelper.fail("300", "secretKey错误")));
                    }
                } catch (CommonException e) {
                    webSocketMap.remove(user);
                    CacheManager.getCacheService().delete(String.format("CLIENT_USER_%s", user));
                    sendMessage(JsonMapper.nonDefaultMapper().toJson(RestResultHelper.fail("501", "服务器异常")));
                    LogUtils.error(WebSocketServer.class, "查找用户异常", e);
                }
            } else {
                CacheManager.getCacheService().delete(String.format("CLIENT_USER_%s", user));
                sendMessage(JsonMapper.nonDefaultMapper().toJson(RestResultHelper.fail("301", "secretKey非法")));
                webSocketMap.remove(user);
            }
        }
    }
    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(@PathParam("user")String user,Session session, Throwable error) {
        CacheManager.getCacheService().delete(String.format("CLIENT_USER_%s", user));
        webSocketMap.remove(user);
        LogUtils.error(WebSocketServer.class,"webSocket连接出错",new Exception(error));
    }
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LogUtils.error(WebSocketServer.class,"发送消息异常",e);
        }
    }
    public static WebSocketServer getWebSocket(String userName){
        return  webSocketMap.get(userName);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}