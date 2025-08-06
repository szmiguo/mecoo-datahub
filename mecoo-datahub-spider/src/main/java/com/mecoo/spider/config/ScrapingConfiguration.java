package com.mecoo.spider.config;

import java.nio.file.Paths;

/**
 * Instagram抓取配置类
 * 统一管理所有配置参数，避免硬编码
 */
public class ScrapingConfiguration {
    
    // 浏览器配置
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36 Edg/138.0.0.0";
    public static final String USER_DATA_DIR = "/Users/lin/Documents/Playwright/persistent-session";
    public static final boolean HEADLESS_MODE = false;
    public static final String CHROME_EXECUTABLE_PATH = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
    
    // 抓取参数
    public static final int DEFAULT_MAX_COLLECTION_COUNT = 20;
    public static final int DEFAULT_MAX_SCROLL_ATTEMPTS = 50;
    //public static final String DEFAULT_USERNAME = "ting.520__";
    public static final String DEFAULT_USERNAME = "dindaalamanda_";


    // 时间配置 (毫秒)
    public static final int PAGE_LOAD_TIMEOUT = 3000;
    public static final int VIEWING_DELAY_MIN = 800;
    public static final int VIEWING_DELAY_RANGE = 1500;
    public static final int PAUSE_TIME_MIN = 2000;
    public static final int PAUSE_TIME_RANGE = 3000;
    public static final int NETWORK_IDLE_TIMEOUT = 3000;
    public static final int OBSERVE_DELAY_MIN = 1500;
    public static final int OBSERVE_DELAY_RANGE = 1000;
    
    // 概率配置 (百分比)
    public static final int PAUSE_PROBABILITY = 20;
    public static final int MOUSE_MOVE_PROBABILITY = 30;
    
    // 滚动配置
    public static final int MIN_SCROLL_STEPS = 2;
    public static final int MAX_SCROLL_STEPS = 4;
    public static final double MIN_SCROLL_HEIGHT_RATIO = 0.5;
    public static final double MAX_SCROLL_HEIGHT_RATIO = 1.3;
    
    // 私有构造函数防止实例化
    private ScrapingConfiguration() {
        throw new IllegalStateException("Configuration class should not be instantiated");
    }
    
    /**
     * 获取用户数据目录路径
     * 支持通过系统属性自定义
     */
    public static String getUserDataDir() {
        String customDir = System.getProperty("mecoo.userDataDir");
        if (customDir != null && !customDir.trim().isEmpty()) {
            return validateUserDataDir(customDir);
        }
        return USER_DATA_DIR;
    }
    
    /**
     * 获取最大收集数量
     * 支持通过系统属性自定义
     */
    public static int getMaxCollectionCount() {
        String customCount = System.getProperty("mecoo.maxCollectionCount");
        if (customCount != null) {
            try {
                int count = Integer.parseInt(customCount);
                if (count > 0 && count <= 100) {
                    return count;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid maxCollectionCount value, using default: " + DEFAULT_MAX_COLLECTION_COUNT);
            }
        }
        return DEFAULT_MAX_COLLECTION_COUNT;
    }
    
    /**
     * 获取目标用户名
     * 支持通过系统属性自定义
     */
    public static String getTargetUsername() {
        String customUsername = System.getProperty("mecoo.username");
        if (customUsername != null && !customUsername.trim().isEmpty()) {
            return validateUsername(customUsername.trim());
        }
        return DEFAULT_USERNAME;
    }
    
    /**
     * 获取Chrome可执行文件路径
     * 支持通过系统属性自定义
     */
    public static String getChromeExecutablePath() {
        String customPath = System.getProperty("mecoo.chromeExecutablePath");
        if (customPath != null && !customPath.trim().isEmpty()) {
            return validateChromeExecutablePath(customPath);
        }
        return CHROME_EXECUTABLE_PATH;
    }
    
    /**
     * 验证用户数据目录路径安全性
     */
    private static String validateUserDataDir(String path) {
        try {
            // 规范化路径，防止路径遍历攻击
            String normalizedPath = Paths.get(path).normalize().toString();
            return normalizedPath;
        } catch (Exception e) {
            System.err.println("Invalid user data directory path, using default: " + USER_DATA_DIR);
            return USER_DATA_DIR;
        }
    }
    
    /**
     * 验证用户名格式
     */
    private static String validateUsername(String username) {
        if (!username.matches("^[a-zA-Z0-9._]+$")) {
            throw new IllegalArgumentException("Invalid username format: " + username);
        }
        if (username.length() > 30) {
            throw new IllegalArgumentException("Username too long: " + username);
        }
        return username;
    }
    
    /**
     * 验证Chrome可执行文件路径
     */
    private static String validateChromeExecutablePath(String path) {
        try {
            String normalizedPath = Paths.get(path).normalize().toString();
            if (!Paths.get(normalizedPath).toFile().exists()) {
                System.err.println("Chrome executable not found at: " + normalizedPath + ", using default: " + CHROME_EXECUTABLE_PATH);
                return CHROME_EXECUTABLE_PATH;
            }
            return normalizedPath;
        } catch (Exception e) {
            System.err.println("Invalid Chrome executable path, using default: " + CHROME_EXECUTABLE_PATH);
            return CHROME_EXECUTABLE_PATH;
        }
    }
}