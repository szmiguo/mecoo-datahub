package com.mecoo.spider.util;

/**
 * Instagram媒体类型工具类
 */
public class MediaTypeUtil {
    
    public static final int PHOTO = 1;
    public static final int VIDEO = 2;
    public static final int CAROUSEL = 3;
    public static final int IGTV = 8;
    
    /**
     * 获取媒体类型的描述
     */
    public static String getMediaTypeDescription(int mediaType) {
        switch (mediaType) {
            case PHOTO:
                return "photo";
            case VIDEO:
                return "reel";
            case CAROUSEL:
                return "carousel";
            case IGTV:
                return "igtv";
            default:
                return "unknown(" + mediaType + ")";
        }
    }
    
    /**
     * 检查是否为视频类型
     */
    public static boolean isVideo(int mediaType) {
        return mediaType == VIDEO || mediaType == IGTV;
    }
    
    /**
     * 检查是否为Reels类型
     */
    public static boolean isReel(int mediaType) {
        return mediaType == VIDEO; // Reels通常归类为视频类型
    }
    
    /**
     * 根据媒体类型描述获取对应的整数值
     */
    public static int getMediaTypeFromDescription(String description) {
        if (description == null) {
            return 0;
        }
        switch (description.toLowerCase()) {
            case "photo":
                return PHOTO;
            case "reel":
                return VIDEO;
            case "carousel":
                return CAROUSEL;
            case "igtv":
                return IGTV;
            default:
                // 尝试解析 "unknown(数字)" 格式
                if (description.startsWith("unknown(") && description.endsWith(")")) {
                    try {
                        String numberPart = description.substring(8, description.length() - 1);
                        return Integer.parseInt(numberPart);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
                return 0;
        }
    }
}