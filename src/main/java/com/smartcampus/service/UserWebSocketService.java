package com.smartcampus.service;

import com.smartcampus.websocket.WebSocketUserManager;
import org.springframework.stereotype.Service;

/**
 * 用户WebSocket连接服务
 */
@Service
public class UserWebSocketService {

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Integer userId) {
        return WebSocketUserManager.isUserOnline(userId);
    }

    /**
     * 向指定用户发送消息
     */
    public boolean sendNotificationToUser(Integer userId, String title, String content) {
        // 创建通知消息对象
        NotificationMessage notification = new NotificationMessage();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setTimestamp(System.currentTimeMillis());

        return WebSocketUserManager.sendObjectToUser(userId, notification);
    }

    /**
     * 向指定用户发送系统消息
     */
    public boolean sendSystemMessageToUser(Integer userId, String type, Object data) {
        SystemMessage message = new SystemMessage();
        message.setType(type);
        message.setData(data);
        message.setTimestamp(System.currentTimeMillis());

        return WebSocketUserManager.sendObjectToUser(userId, message);
    }

    /**
     * 通知用户登录成功
     */
    public boolean notifyUserLoginSuccess(Integer userId) {
        return sendNotificationToUser(userId, "登录成功", "您已成功登录校园安全系统");
    }

    /**
     * 通知用户有新的警报事件
     */
    public boolean notifyNewAlarmEvent(Integer userId, Object alarmData) {
        return sendSystemMessageToUser(userId, "ALARM_EVENT", alarmData);
    }

    /**
     * 通知用户有设备状态变更
     */
    public boolean notifyDeviceStatusChange(Integer userId, Object deviceData) {
        return sendSystemMessageToUser(userId, "DEVICE_STATUS", deviceData);
    }
}

/**
 * 通知消息类
 */
class NotificationMessage {
    private String title;
    private String content;
    private Long timestamp;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

/**
 * 系统消息类
 */
class SystemMessage {
    private String type;
    private Object data;
    private Long timestamp;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
