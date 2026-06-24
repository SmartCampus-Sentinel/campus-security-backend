package com.smartcampus.controller;

import com.smartcampus.common.Result;
import com.smartcampus.service.VideoStreamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/videoStream")
@RequiredArgsConstructor
@Api(tags = "视频流管理")
public class VideoStreamController {

    private final VideoStreamService videoStreamService;

    @Data
    public static class StartStreamRequest {
        @NotNull
        @ApiParam("设备ID")
        private Long deviceId;

        @NotBlank
        @ApiParam("RTSP地址")
        private String rtspUrl;
    }

    @PostMapping("/start")
    @ApiOperation("启动视频流转换")
    public Result<Void> start(@Valid @RequestBody StartStreamRequest request) {
        videoStreamService.startStream(request.getDeviceId(), request.getRtspUrl());
        return Result.success(null, "视频流启动成功");
    }

    @PostMapping("/stop/{deviceId}")
    @ApiOperation("停止视频流")
    public Result<Void> stop(@PathVariable Long deviceId) {
        videoStreamService.stopStream(deviceId);
        return Result.success(null, "视频流已停止");
    }

    @PostMapping("/capture/{deviceId}")
    @ApiOperation("截取当前帧")
    public Result<String> capture(@PathVariable Long deviceId) {
        String path = videoStreamService.captureFrame(deviceId);
        return Result.success(path, "截帧成功");
    }

    @GetMapping("/status/{deviceId}")
    @ApiOperation("查询视频流状态")
    public Result<Boolean> status(@PathVariable Long deviceId) {
        return Result.success(videoStreamService.isStreaming(deviceId));
    }
}
