package com.manager.appbanhang.model;

import java.util.Map;

public class NotiSendData {
    private String to;
    Map<String, String> notification;

    public NotiSendData(String to, Map<String, String> notification) {
        this.to = to;
        this.notification = notification;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setNotification(Map<String, String> notification) {
        this.notification = notification;
    }
}
