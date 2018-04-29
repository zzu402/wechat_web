package com.hzz.event.listener;

import com.hzz.event.ReceiveRequestEvent;

import java.util.EventListener;

public interface ReceiveRequestListener extends EventListener {

    public void receiveRequest(ReceiveRequestEvent receiveRequestEvent);


}
