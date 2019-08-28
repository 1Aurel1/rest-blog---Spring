package com.restblogv2.restblog.util;

import org.imgscalr.Scalr;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;

public class ImageProcessioning extends FileStorage implements ImageProcessingProperties{


    Resource resource = new ClassPathResource("static/waterMark/watermark.jpg");

    InputStream input = null;
    File WATER_MARK_File = null;

    {
        try {
            input = resource.getInputStream();
            WATER_MARK_File = resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String proccessImage(MultipartFile file, String childPath) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

        String formatType = file.getContentType().replace("image/", "");
        MultipartFile temp = new MockMultipartFile(file.getName(), file.getOriginalFilename(), file.getContentType(), addImageWatermark(WATER_MARK_File, bufferedImage, formatType).toByteArray());
        String newPath = childPath + "/original";

        String storeagePath = storeFile(temp, newPath).replace("\\original", "");
        for( String path : ImageProcessingProperties.IMAGE_PATHS){

            if(path.equals("medium")){

                bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY,
                        Scalr.Mode.FIT_TO_WIDTH, ImageProcessingProperties.MEDIUM_SIZE, ImageProcessingProperties.MEDIUM_SIZE, Scalr.OP_ANTIALIAS);
                temp = new MockMultipartFile(file.getName(), file.getOriginalFilename(), file.getContentType(), addImageWatermark(WATER_MARK_File, bufferedImage, formatType).toByteArray());
                newPath = childPath + "/" + path;
                storeFile(temp, newPath);
            }
            else if(path.equals("thumbnail")){
                bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY,
                        Scalr.Mode.FIT_TO_WIDTH, ImageProcessingProperties.THUMBNAIL_SIZE, ImageProcessingProperties.THUMBNAIL_SIZE, Scalr.OP_ANTIALIAS);
                temp = new MockMultipartFile(file.getName(), file.getOriginalFilename(), file.getContentType(), addImageWatermark(WATER_MARK_File, bufferedImage, formatType).toByteArray());
                newPath = childPath + "/" + path;
                storeFile(temp, newPath);
            }
        }


        System.err.println(storeagePath);
        return storeagePath;
    }

    private ByteArrayOutputStream addImageWatermark(File watermarkImageFile, BufferedImage sourceImage, String formatName) {
        try {
            BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);
            watermarkImage = Scalr.resize(watermarkImage, Scalr.Mode.AUTOMATIC, sourceImage.getWidth() / 10, sourceImage.getWidth() / 10);

            ByteArrayOutputStream destImageFile = new ByteArrayOutputStream();

            Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
            g2d.setComposite(alphaChannel);

            // whater mark pos
            int topLeftX = (int) ((sourceImage.getWidth() - watermarkImage.getWidth()) / 1.1);
            int topLeftY = (int) ((sourceImage.getHeight() - watermarkImage.getHeight()) / 1.1);

            g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);

            ImageIO.write(sourceImage, formatName, destImageFile);
            g2d.dispose();
            System.out.println("The image watermark is added to the image.");

            return destImageFile;

        } catch (IOException ex) {
            System.err.println(ex);
            return null;
        }
    }


}
