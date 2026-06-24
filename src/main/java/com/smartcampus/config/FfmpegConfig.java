package com.smartcampus.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Getter
@Configuration
public class FfmpegConfig {

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Value("${ffmpeg.output-dir}")
    private String outputDir;

    @PostConstruct
    public void init() {
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
