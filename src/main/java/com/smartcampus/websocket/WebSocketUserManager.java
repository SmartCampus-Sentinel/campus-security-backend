package com.smartcampus.websocket;

import com.smartcampus.entity.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket用户连接管理器
 * 用于管理登录用户与其WebSocket连接的映射关系
 */
@Component
public class WebSocketUserManager {

    // 存储用户ID与WebSocket连接的映射关系
    private static final ConcurrentHashMap<Integer, WebSocketServer> userConnectionMap = new ConcurrentHashMap<>();

    // 存储用户ID与Session ID的映射关系
    private static final ConcurrentHashMap<String, Integer> sessionIdUserIdMap = new ConcurrentHashMap<>();

    /**
     * 添加用户连接
     */
    public static void addUserConnection(Integer userId, WebSocketServer webSocketServer) {
        userConnectionMap.put(userId, webSocketServer);
    }

    /**
     * 移除用户连接
     */
    public static void removeUserConnection(Integer userId) {
        userConnectionMap.remove(userId);
    }

    /**
     * 根据用户ID获取WebSocket连接
     */
    public static WebSocketServer getUserConnection(Integer userId) {
        return userConnectionMap.get(userId);
    }

    /**
     * 向指定用户发送消息
     */
    public static boolean sendMessageToUser(Integer userId, String message) {
        WebSocketServer server = userConnectionMap.get(userId);
        if (server != null) {
            try {
                server.sendMessage(message);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 向指定用户发送对象消息
     */
    public static boolean sendObjectToUser(Integer userId, Object object) {
        WebSocketServer server = userConnectionMap.get(userId);
        if (server != null) {
            try {
                server.sendObject(object);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 检查用户是否在线
     */
    public static boolean isUserOnline(Integer userId) {
        return userConnectionMap.containsKey(userId);
    }

    /**
     * 获取在线用户数量
     */
    public static int getOnlineUserCount() {
        return userConnectionMap.size();
    }
}
