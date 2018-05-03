package com.hzz.websocket;

import java.io.IOException;
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
import com.hzz.exception.CommonException;
import com.hzz.model.User;
import com.hzz.service.UserService;
import com.hzz.utils.JsonMapper;
import com.hzz.utils.LogUtils;
import com.hzz.utils.RestResultHelper;
import com.hzz.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//@ServerEndpoint(value = "/websocket")
@ServerEndpoint("/websocket/{user}")
@Component
public class WebSocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private static Map<String,WebSocketServer> webSocketMap=new ConcurrentHashMap<>();
    private Session session;
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam("user") String user, Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        webSocketMap.put(user,this);
        addOnlineCount();           //在线数加1
        LogUtils.info(WebSocketServer.class,"有新连接加入，当前在线人数:"+getOnlineCount());
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("user")String user) {
        webSocketSet.remove(this);  //从set中删除
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
        if(secretKey!=null){
            try {
                UserService userService= (UserService) SpringUtils.getBean(UserService.class);
                User u=userService.getUserByName(user);
                if(u.getSecretKey().equals(secretKey)){
                    CacheManager.getCacheService().set(String.format("CLIENT_USER_%s", user),u.getId().toString());
                    sendMessage(JsonMapper.nonDefaultMapper().toJson(RestResultHelper.success()));
                }else {
                    CacheManager.getCacheService().delete(String.format("CLIENT_USER_%s", user));
                    webSocketMap.remove(user);
                    sendMessage(JsonMapper.nonDefaultMapper().toJson(RestResultHelper.fail("300","secretKey错误")));
                }
            } catch (CommonException e) {
                webSocketMap.remove(user);
                CacheManager.getCacheService().delete(String.format("CLIENT_USER_%s", user));
                sendMessage(JsonMapper.nonDefaultMapper().toJson(RestResultHelper.fail("501","服务器异常")));
                LogUtils.error(WebSocketServer.class,"查找用户异常",e);
            }
        }else{
            CacheManager.getCacheService().delete(String.format("CLIENT_USER_%s", user));
            sendMessage(JsonMapper.nonDefaultMapper().toJson(RestResultHelper.fail("301","secretKey非法")));
            webSocketMap.remove(user);
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
    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message)  {
        for (WebSocketServer item : webSocketSet) {
            item.sendMessage(message);
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