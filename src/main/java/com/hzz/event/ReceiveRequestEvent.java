package com.hzz.event;

import javax.servlet.http.HttpServletRequest;
import java.util.EventObject;
/*
`   当收到前端发送来的微信号和验证码时候响应事件
 */
public class ReceiveRequestEvent extends EventObject{


    private HttpServletRequest request;
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ReceiveRequestEvent(Object source,HttpServletRequest request) {
        super(source);
        this.request=request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
