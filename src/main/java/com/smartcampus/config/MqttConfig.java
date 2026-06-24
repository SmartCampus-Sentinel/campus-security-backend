package com.smartcampus.config;

import com.smartcampus.service.MqttMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class MqttConfig {

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.topics}")
    private String topics;

    @Bean
    public MqttClient mqttClient(MqttMessageHandler messageHandler) throws Exception {
        MqttClient client = new MqttClient(brokerUrl, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(30);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                log.warn("[MQTT] 连接断开，等待自动重连: {}", cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                log.info("[MQTT] 收到消息 topic={}, payload={}", topic, payload);
                try {
                    messageHandler.handle(topic, payload);
                } catch (Exception e) {
                    log.error("[MQTT] 消息处理失败 topic={}", topic, e);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        try {
            client.connect(options);
            log.info("[MQTT] 连接成功: {}", brokerUrl);

            for (String topic : topics.split(",")) {
                String trimmed = topic.trim();
                client.subscribe(trimmed, 1);
                log.info("[MQTT] 已订阅主题: {}", trimmed);
            }
        } catch (Exception e) {
            log.error("[MQTT] 连接失败: {}，将在消息发送时重试", e.getMessage());
        }

        return client;
    }
}
