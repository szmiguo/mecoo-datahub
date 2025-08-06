package com.mecoo.spider.util;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

import java.util.Arrays;
import java.util.List;

/**
 * Playwright Stealth 模式工具类
 * 实现各种反检测技术，模拟真实浏览器行为
 */
public class PlaywrightStealth {
    
    /**
     * 获取Stealth模式的浏览器启动参数
     */
    public static List<String> getStealthArgs() {
        return Arrays.asList(
            // 基础反检测参数
            "--no-sandbox",
            "--disable-setuid-sandbox",
            "--disable-dev-shm-usage",
            "--disable-accelerated-2d-canvas",
            "--no-first-run",
            "--no-zygote",
            "--disable-gpu",
            "--disable-background-timer-throttling",
            "--disable-backgrounding-occluded-windows",
            "--disable-renderer-backgrounding",
            "--disable-features=TranslateUI",
            "--disable-ipc-flooding-protection",
            
            // 核心反自动化检测
            "--disable-blink-features=AutomationControlled",
            "--disable-web-security",
            "--disable-features=VizDisplayCompositor",
            
            // 性能优化（可选）
            "--disable-images",
            "--disable-javascript-harmony-shipping",
            "--disable-background-networking",
            "--disable-background-timer-throttling",
            "--disable-client-side-phishing-detection",
            "--disable-default-apps",
            "--disable-hang-monitor",
            "--disable-prompt-on-repost",
            "--disable-sync",
            
            // 隐私和安全
            "--no-default-browser-check",
            "--no-service-autorun",
            "--password-store=basic",
            "--use-mock-keychain"
        );
    }
    
    /**
     * 为浏览器上下文应用Stealth配置
     */
    public static void applyStealth(BrowserContext context) {
        // 添加初始化脚本，在每个页面加载前执行
        context.addInitScript(getStealthScript());
    }
    
    /**
     * 为单个页面应用Stealth配置
     */
    public static void applyStealth(Page page) {
        // 添加初始化脚本
        page.addInitScript(getStealthScript());
        
        // 设置额外的页面属性 (1440x900屏幕)
        page.evaluate("() => {" +
            // 设置1440x900屏幕分辨率和设备像素比
            "Object.defineProperty(screen, 'width', {get: () => 1440});" +
            "Object.defineProperty(screen, 'height', {get: () => 900});" +
            "Object.defineProperty(screen, 'availWidth', {get: () => 1440});" +
            "Object.defineProperty(screen, 'availHeight', {get: () => 860});" +
            "Object.defineProperty(window, 'devicePixelRatio', {get: () => 1});" +
            "}");
    }
    
    /**
     * 获取完整的Stealth脚本
     */
    private static String getStealthScript() {
        return "(() => {" +
            // 1. 隐藏webdriver属性
            "Object.defineProperty(navigator, 'webdriver', {" +
                "get: () => undefined" +
            "});" +
            
            // 2. 模拟Chrome运行时
            "window.chrome = {" +
                "runtime: {}," +
                "loadTimes: function() {}," +
                "csi: function() {}," +
                "app: {}" +
            "};" +
            
            // 3. 重写plugins属性
            "Object.defineProperty(navigator, 'plugins', {" +
                "get: () => {" +
                    "return {" +
                        "0: {" +
                            "0: {}," +
                            "description: 'Portable Document Format'," +
                            "filename: 'mhjfbmdgcfjbbpaeojofohoefgiehjai'," +
                            "length: 1," +
                            "name: 'Chrome PDF Plugin'" +
                        "}," +
                        "1: {" +
                            "0: {}," +
                            "description: 'Portable Document Format'," +
                            "filename: 'internal-pdf-viewer'," +
                            "length: 1," +
                            "name: 'Chrome PDF Viewer'" +
                        "}," +
                        "2: {" +
                            "0: {}," +
                            "description: 'Portable Document Format'," +
                            "filename: 'internal-pdf-viewer'," +
                            "length: 1," +
                            "name: 'Native Client'" +
                        "}," +
                        "length: 3" +
                    "};" +
                "}" +
            "});" +
            
            // 4. 设置语言 (洛杉矶英语)
            "Object.defineProperty(navigator, 'languages', {" +
                "get: () => ['en-US', 'en']" +
            "});" +
            "Object.defineProperty(navigator, 'language', {" +
                "get: () => 'en-US'" +
            "});" +
            
            // 5. 模拟真实的User Agent
            "Object.defineProperty(navigator, 'platform', {" +
                "get: () => 'MacIntel'" +
            "});" +
            
            // 6. 隐藏自动化相关属性
            "delete navigator.__proto__.webdriver;" +
            
            // 7. 重写权限查询
            "const originalQuery = window.navigator.permissions.query;" +
            "window.navigator.permissions.query = (parameters) => (" +
                "parameters.name === 'notifications' ?" +
                "Promise.resolve({ state: Notification.permission }) :" +
                "originalQuery(parameters)" +
            ");" +
            
            // 8. 模拟真实的内存信息
            "Object.defineProperty(navigator, 'deviceMemory', {" +
                "get: () => 8" +
            "});" +
            
            // 9. 模拟硬件并发
            "Object.defineProperty(navigator, 'hardwareConcurrency', {" +
                "get: () => 4" +
            "});" +
            
            // 10. 模拟连接信息
            "Object.defineProperty(navigator, 'connection', {" +
                "get: () => ({" +
                    "effectiveType: '4g'," +
                    "rtt: 100," +
                    "downlink: 10" +
                "})" +
            "});" +
            
            // 11. 隐藏自动化测试框架
            "window.domAutomation = undefined;" +
            "window.domAutomationController = undefined;" +
            "window.domAutomationControllerBrowser = undefined;" +
            
            // 12. 重写toString方法以避免检测
            "const toStringOriginal = Function.prototype.toString;" +
            "Function.prototype.toString = function() {" +
                "if (this === navigator.webdriver) {" +
                    "return 'function webdriver() { [native code] }';" +
                "}" +
                "return toStringOriginal.call(this);" +
            "};" +
            
            // 13. 模拟洛杉矶时区和地理位置
            "Object.defineProperty(Intl.DateTimeFormat.prototype, 'resolvedOptions', {" +
                "value: function() {" +
                    "return {" +
                        "locale: 'en-US'," +
                        "calendar: 'gregory'," +
                        "numberingSystem: 'latn'," +
                        "timeZone: 'America/Los_Angeles'" +
                    "};" +
                "}" +
            "});" +
            
            // 重写时区检测
            "Object.defineProperty(Date.prototype, 'getTimezoneOffset', {" +
                "value: function() {" +
                    "return 480;" + // 洛杉矶时区偏移 (UTC-8)
                "}" +
            "});" +
            
            // 伪造地理位置信息 (洛杉矶)
            "Object.defineProperty(navigator, 'geolocation', {" +
                "value: {" +
                    "getCurrentPosition: function(success, error, options) {" +
                        "setTimeout(() => {" +
                            "success({" +
                                "coords: {" +
                                    "latitude: 34.0522," +   // 洛杉矶纬度
                                    "longitude: -118.2437," + // 洛杉矶经度
                                    "accuracy: 10," +
                                    "altitude: null," +
                                    "altitudeAccuracy: null," +
                                    "heading: null," +
                                    "speed: null" +
                                "}," +
                                "timestamp: Date.now()" +
                            "});" +
                        "}, 100);" +
                    "}," +
                    "watchPosition: function() { return 1; }," +
                    "clearWatch: function() {}" +
                "}" +
            "});" +
            
            // 14. 防止帧检测
            "Object.defineProperty(window, 'outerHeight', {" +
                "get: () => window.innerHeight" +
            "});" +
            "Object.defineProperty(window, 'outerWidth', {" +
                "get: () => window.innerWidth" +
            "});" +
            
            "})();";
    }
    
    /**
     * 设置固定的1440x900屏幕分辨率
     */
    public static void setRandomViewport(Page page) {
        // 固定使用1440x900分辨率
        int width = 1440;
        int height = 900;
        
        page.setViewportSize(width, height);
        System.out.println("设置1440x900屏幕分辨率: " + width + "x" + height);
    }
    
    /**
     * 模拟真实用户的鼠标移动
     */
    public static void simulateHumanMouseMovement(Page page) {
        try {
            // 随机移动鼠标到1440x900屏幕上的几个点
            for (int i = 0; i < 3; i++) {
                int x = 100 + (int) (Math.random() * 1240); // 1440x900宽度范围
                int y = 100 + (int) (Math.random() * 700);  // 1440x900高度范围
                
                // 分步移动，更像人类行为
                page.mouse().move(x / 2, y / 2);
                Thread.sleep(50 + (int) (Math.random() * 100));
                page.mouse().move(x, y);
                Thread.sleep(100 + (int) (Math.random() * 200));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}