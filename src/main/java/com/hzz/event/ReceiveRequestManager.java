package com.hzz.event;

import com.hzz.event.listener.ReceiveRequestListener;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class ReceiveRequestManager {

    private Collection listeners;

    public void addReceiveRequestListener(ReceiveRequestListener receiveRequestListener){
        if(listeners==null){
            synchronized (this){
                if(listeners==null)
                    listeners=new HashSet();
            }
        }
        listeners.add(receiveRequestListener);
    }

    public void receiveMessage(HttpServletRequest request){
        if(listeners==null)
            return;
        ReceiveRequestEvent event=new ReceiveRequestEvent(this,request);
        notifyListeners(event);
    }

    public void removeReceiveRequestListener(ReceiveRequestListener listener) {
        if (listeners == null)
            return;
        listeners.remove(listener);
    }

    private void notifyListeners(ReceiveRequestEvent event) {
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            ReceiveRequestListener listener = (ReceiveRequestListener) iter.next();
            listener.receiveRequest(event);
        }
    }

}
