package com.smartcampus.websocket;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 登录相关WebSocket服务
 * 用于处理登录页面的实时消息
 */
@Component
@ServerEndpoint("/websocket/login")
public class LoginWebSocketServer {

    // 使用线程安全的集合存储登录页面的WebSocket连接
    private static final CopyOnWriteArraySet<LoginWebSocketServer> loginWebSocketServers = new CopyOnWriteArraySet<>();

    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        loginWebSocketServers.add(this);
        System.out.println("登录页面WebSocket连接建立，当前登录页面连接数：" + loginWebSocketServers.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        loginWebSocketServers.remove(this);
        System.out.println("登录页面WebSocket连接关闭，当前登录页面连接数：" + loginWebSocketServers.size());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自登录页面的消息：" + message);
        // 登录页面通常不需要处理消息，但可以保留此方法以备将来扩展
    }

    /**
     * 发送消息给所有登录页面
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 向所有登录页面广播消息
     */
    public static void broadcastToLoginPage(Object message) {
        String jsonStr = JSON.toJSONString(message);
        for (LoginWebSocketServer server : loginWebSocketServers) {
            try {
                server.sendMessage(jsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向所有登录页面发送系统通知
     */
    public static void sendSystemNotificationToLoginPage(String type, String title, String content) {
        LoginNotificationMessage notification = new LoginNotificationMessage();
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setTimestamp(System.currentTimeMillis());

        broadcastToLoginPage(notification);
    }

    /**
     * 向所有登录页面发送系统维护通知
     */
    public static void sendMaintenanceNotification(String message) {
        sendSystemNotificationToLoginPage("MAINTENANCE", "系统维护通知", message);
    }

    /**
     * 向所有登录页面发送安全警告
     */
    public static void sendSecurityAlert(String message) {
        sendSystemNotificationToLoginPage("SECURITY", "安全警告", message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("登录页面WebSocket发生错误");
        loginWebSocketServers.remove(this);
        error.printStackTrace();
    }
}

/**
 * 登录页面通知消息类
 */
class LoginNotificationMessage {
    private String type;      // 消息类型：MAINTENANCE, SECURITY, SYSTEM等
    private String title;     // 消息标题
    private String content;   // 消息内容
    private Long timestamp;   // 时间戳

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
