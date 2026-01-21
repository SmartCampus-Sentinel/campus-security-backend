package com.smartcampus.websocket;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    // concurrent包的线程安全Map，用来存放每个客户端对应的WebSocketServer对象
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketSet = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收userId
    private String userId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        webSocketSet.put(userId, this); // 加入map中
        addOnlineCount(); // 在线数加1
        System.out.println("用户" + userId + "加入连接! 当前在线人数为:" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (!userId.equals("")) {
            webSocketSet.remove(this.userId); // 从set中删除
            subOnlineCount(); // 在线数减1
            System.out.println("用户" + userId + "退出连接! 当前在线人数为:" + getOnlineCount());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自用户" + userId + "的消息:" + message);

        // 群发消息
        for (String userId : webSocketSet.keySet()) {
            try {
                webSocketSet.get(userId).sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("用户" + userId + "发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送消息到指定用户
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送对象消息，自动序列化为JSON
     */
    public void sendObject(Object object) throws IOException {
        String jsonStr = JSON.toJSONString(object);
        this.session.getBasicRemote().sendText(jsonStr);
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        for (String id : webSocketSet.keySet()) {
            try {
                if (userId == null || userId.equals(id)) {
                    webSocketSet.get(id).sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向所有客户端广播消息
     */
    public static void broadcast(Object object) {
        String jsonStr = JSON.toJSONString(object);
        for (String id : webSocketSet.keySet()) {
            try {
                webSocketSet.get(id).sendMessage(jsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount.get();
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount.incrementAndGet();
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount.decrementAndGet();
    }
}
