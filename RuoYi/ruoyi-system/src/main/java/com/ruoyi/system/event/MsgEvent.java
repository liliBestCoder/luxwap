package com.ruoyi.system.event;

import org.springframework.context.ApplicationEvent;

public class MsgEvent extends ApplicationEvent {
    private String type;
    private String clientIp;
    private Object msg;

    public MsgEvent(Object source, String type, String clientIp, Object msg) {
        super(source);
        this.type = type;
        this.clientIp = clientIp;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public String getClientIp() {
        return clientIp;
    }

    public Object getMsg() {
        return msg;
    }
}
