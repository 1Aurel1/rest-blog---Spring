package com.restblogv2.restblog.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface ImageProcessingProperties {

    String[] IMAGE_PATHS = {"thumbnail", "medium", "original"};

    public final int THUMBNAIL_WIDTH = 75;
    public final int MEDIUM_WIDTH = 350;

    //public final MultipartFile WATER_MARK_FILE = (MultipartFile) new ClassPathResource("img/waterMark/watermark.png");

}
