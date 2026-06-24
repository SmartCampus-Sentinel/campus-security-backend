package com.smartcampus.service;

import com.smartcampus.config.FfmpegConfig;
import com.smartcampus.entity.DeviceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoStreamService {

    private final FfmpegConfig ffmpegConfig;
    private final IDeviceInfoService deviceInfoService;

    private final Map<Long, Process> activeStreams = new ConcurrentHashMap<>();

    public void startStream(Long deviceId, String rtspUrl) {
        if (activeStreams.containsKey(deviceId)) {
            throw new RuntimeException("设备 " + deviceId + " 的视频流已在运行");
        }

        DeviceInfo device = deviceInfoService.getById(deviceId);
        if (device == null) {
            throw new RuntimeException("设备不存在: " + deviceId);
        }

        try {
            String[] cmd = {
                    ffmpegConfig.getFfmpegPath(),
                    "-i", rtspUrl,
                    "-c:v", "copy",
                    "-f", "flv",
                    "rtmp://localhost:1935/live/stream_" + deviceId
            };

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            activeStreams.put(deviceId, process);

            log.info("[FFmpeg] 启动视频流 deviceId={}, rtspUrl={}", deviceId, rtspUrl);
        } catch (IOException e) {
            throw new RuntimeException("FFmpeg启动失败: " + e.getMessage(), e);
        }
    }

    public void stopStream(Long deviceId) {
        Process process = activeStreams.remove(deviceId);
        if (process != null) {
            process.destroyForcibly();
            log.info("[FFmpeg] 停止视频流 deviceId={}", deviceId);
        } else {
            throw new RuntimeException("设备 " + deviceId + " 没有运行中的视频流");
        }
    }

    public String captureFrame(Long deviceId) {
        DeviceInfo device = deviceInfoService.getById(deviceId);
        if (device == null) {
            throw new RuntimeException("设备不存在: " + deviceId);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String outputPath = ffmpegConfig.getOutputDir() + File.separator
                + "frame_" + deviceId + "_" + timestamp + ".jpg";

        try {
            String[] cmd = {
                    ffmpegConfig.getFfmpegPath(),
                    "-i", "rtsp://" + device.getIpAddress() + "/stream",
                    "-frames:v", "1",
                    "-q:v", "2",
                    outputPath
            };

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            boolean finished = process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("截帧超时");
            }

            if (process.exitValue() != 0) {
                throw new RuntimeException("截帧失败，FFmpeg退出码: " + process.exitValue());
            }

            log.info("[FFmpeg] 截帧成功 deviceId={}, path={}", deviceId, outputPath);
            return outputPath;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("截帧失败: " + e.getMessage(), e);
        }
    }

    public boolean isStreaming(Long deviceId) {
        Process process = activeStreams.get(deviceId);
        return process != null && process.isAlive();
    }
}
