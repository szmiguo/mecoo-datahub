package com.mecoo.spider.scrapers;

import cn.hutool.core.collection.CollUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mecoo.spider.config.ScrapingConfiguration;
import com.mecoo.spider.domain.InstagramGraphQLResponse;
import com.mecoo.spider.domain.PostData;
import com.mecoo.spider.enums.SocialMediaPlatform;
import com.mecoo.spider.util.PlaywrightStealth;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class InstagramReelScraper {

    private static BrowserContext browserContext;
    private static Playwright playwright;
    private static final Gson gson = new Gson();


    /**
     * 获取用户最新N条Reels数据 (基于网络请求拦截)
     *
     * @param username          Instagram用户名
     * @param maxCollectedCount 最大收集数量
     * @return Reels数据列表
     */
    public static List<PostData> getUserReelsData(String username, int maxCollectedCount) {
        List<PostData> reelsData = new ArrayList<>(maxCollectedCount);
        Set<String> collectedShortCodes = new HashSet<>();
        List<String> interceptedResponses = new ArrayList<>();

        try {
            Page page = browserContext.newPage();

            // 为页面应用额外的Stealth配置
            PlaywrightStealth.applyStealth(page);
            PlaywrightStealth.setRandomViewport(page);

            // 设置网络请求拦截器
            page.route("**/graphql/query", route -> {
                // 继续请求，但拦截响应
                route.resume();
            });

            // 监听响应
            page.onResponse(response -> {
                if (response.url().contains("/graphql/query")) {
                    try {
                        String responseBody = response.text();
                        if (isReelsDataResponse(responseBody)) {
                            log.info("【新方法】拦截到Reels数据响应，长度: {}", responseBody.length());
                            interceptedResponses.add(responseBody);
                        }
                    } catch (Exception e) {
                        log.warn("【新方法】读取响应失败: {}", e.getMessage());
                    }
                }
            });

            String reelsUrl = "https://www.instagram.com/" + username + "/reels/";
            log.info("【新方法】导航到用户Reels页面: {}", reelsUrl);
            page.navigate(reelsUrl);

            // 处理页面重定向（登录检测等）
            if (!handlePageRedirect(page, reelsUrl)) {
                page.close();
                return reelsData;
            }

            // 等待页面基本加载
            try {
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
                page.waitForTimeout(ScrapingConfiguration.PAGE_LOAD_TIMEOUT);
            } catch (Exception e) {
                log.warn("【新方法】页面加载等待异常: {}", e.getMessage());
            }

            log.info("【新方法】开始收集Reels数据，目标: {} 条", maxCollectedCount);

            // 滚动触发更多网络请求
            int maxScrolls = ScrapingConfiguration.DEFAULT_MAX_SCROLL_ATTEMPTS;
            int scrollCount = 0;

            while (reelsData.size() < maxCollectedCount && scrollCount < maxScrolls) {
                scrollCount++;
                log.info("【新方法】第 {} 次滚动，已收集: {} 条", scrollCount, reelsData.size());

                // 处理已拦截的响应
                for (String responseBody : interceptedResponses) {
                    List<PostData> newReels = parseReelsFromResponse(responseBody);
                    for (PostData reel : newReels) {
                        if (collectedShortCodes.add(reel.getShortCode()) && reelsData.size() < maxCollectedCount) {
                            reelsData.add(reel);
                            log.info("成功收集第 " + reelsData.size() + " 条: " + reel.getShortCode());
                        }
                    }
                }
                interceptedResponses.clear(); // 清空已处理的响应

                if (reelsData.size() < maxCollectedCount) {
                    // 模拟人类浏览行为：观看内容的随机停顿
                    int viewingDelay = ScrapingConfiguration.VIEWING_DELAY_MIN +
                            ThreadLocalRandom.current().nextInt(ScrapingConfiguration.VIEWING_DELAY_RANGE);
                    log.debug("【新方法】模拟观看内容，等待 {} 毫秒...", viewingDelay);
                    page.waitForTimeout(viewingDelay);

                    // 偶尔模拟用户暂停浏览
                    if (ThreadLocalRandom.current().nextInt(100) < ScrapingConfiguration.PAUSE_PROBABILITY) {
                        int pauseTime = ScrapingConfiguration.PAUSE_TIME_MIN +
                                ThreadLocalRandom.current().nextInt(ScrapingConfiguration.PAUSE_TIME_RANGE);
                        log.debug("【新方法】模拟用户暂停浏览，等待 {} 毫秒...", pauseTime);
                        page.waitForTimeout(pauseTime);
                    }

                    // 随机选择滚动前的准备行为
                    if (ThreadLocalRandom.current().nextInt(100) < ScrapingConfiguration.MOUSE_MOVE_PROBABILITY) {
                        log.debug("【新方法】模拟真实用户鼠标移动...");
                        try {
                            // 使用更真实的鼠标移动模拟
                            PlaywrightStealth.simulateHumanMouseMovement(page);
                        } catch (Exception e) {
                            log.warn("【新方法】鼠标移动失败: {}", e.getMessage());
                        }
                    }

                    // 执行人性化滚动
                    log.debug("【新方法】开始执行人性化滚动...");
                    performHumanLikeScroll(page);

                    // 滚动后观察新内容的等待时间
                    int observeDelay = ScrapingConfiguration.OBSERVE_DELAY_MIN +
                            ThreadLocalRandom.current().nextInt(ScrapingConfiguration.OBSERVE_DELAY_RANGE);
                    log.debug("【新方法】滚动后观察新内容，等待 {} 毫秒...", observeDelay);
                    page.waitForTimeout(observeDelay);

                    // 等待网络请求完成，使用更智能的等待策略
                    try {
                        page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(ScrapingConfiguration.NETWORK_IDLE_TIMEOUT));
                        log.debug("【新方法】网络请求已稳定");
                    } catch (Exception e) {
                        // 如果网络空闲等待超时，则继续等待一段时间
                        log.debug("【新方法】网络空闲等待超时，继续等待...");
                        page.waitForTimeout(1000);
                    }
                }
            }

            // 处理剩余的响应
            for (String responseBody : interceptedResponses) {
                List<PostData> newReels = parseReelsFromResponse(responseBody);
                for (PostData reel : newReels) {
                    if (collectedShortCodes.add(reel.getShortCode()) && reelsData.size() < maxCollectedCount) {
                        reelsData.add(reel);
                        log.info("成功收集第 " + reelsData.size() + " 条: " + reel.getShortCode());
                    }
                }
            }

            page.close();
            log.info("【新方法】数据收集完成，共收集到 {} 条Reels数据", reelsData.size());

        } catch (Exception e) {
            System.err.println("【新方法】获取用户Reels数据失败: " + e.getMessage());
        }

        return reelsData;
    }


    /**
     * 执行类似人类的滚动行为
     * 包括渐进式滚动、随机滚动距离、滚动速度变化等
     */
    private static void performHumanLikeScroll(Page page) {
        try {
            // 获取当前页面高度和窗口高度
            Integer pageHeight = (Integer) page.evaluate("document.body.scrollHeight");
            Integer windowHeight = (Integer) page.evaluate("window.innerHeight");
            Integer currentScrollY = (Integer) page.evaluate("window.scrollY");

            log.debug("执行人性化滚动: 当前位置={}, 页面高度={}, 窗口高度={}", currentScrollY, pageHeight, windowHeight);

            // 随机选择滚动策略
            int scrollStrategy = ThreadLocalRandom.current().nextInt(3);

            switch (scrollStrategy) {
                case 0:
                    // 策略1: 渐进式多步滚动
                    performProgressiveScroll(page, windowHeight);
                    break;
                case 1:
                    // 策略2: 随机距离滚动
                    performRandomDistanceScroll(page, windowHeight);
                    break;
                case 2:
                    // 策略3: 模拟真实用户滚动（快-慢-快）
                    performRealisticScroll(page, windowHeight);
                    break;
            }

        } catch (Exception e) {
            log.warn("人性化滚动执行出错，使用默认滚动: {}", e.getMessage());
            // 降级到默认滚动
            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        }
    }

    /**
     * 渐进式多步滚动
     */
    private static void performProgressiveScroll(Page page, int windowHeight) {
        log.debug("使用渐进式滚动策略");
        int steps = ScrapingConfiguration.MIN_SCROLL_STEPS + ThreadLocalRandom.current().nextInt(ScrapingConfiguration.MAX_SCROLL_STEPS - ScrapingConfiguration.MIN_SCROLL_STEPS + 1); // 2-4步
        int totalScrollDistance = (int) (windowHeight * (ScrapingConfiguration.MIN_SCROLL_HEIGHT_RATIO + ThreadLocalRandom.current().nextDouble() * (ScrapingConfiguration.MAX_SCROLL_HEIGHT_RATIO - ScrapingConfiguration.MIN_SCROLL_HEIGHT_RATIO))); // 70%-120%窗口高度

        for (int i = 0; i < steps; i++) {
            int stepDistance = totalScrollDistance / steps;
            if (i == steps - 1) {
                // 最后一步滚动剩余距离
                stepDistance = totalScrollDistance - (stepDistance * (steps - 1));
            }

            // 添加一些随机变化
            stepDistance += ThreadLocalRandom.current().nextInt(100) - 50; // ±50px随机变化

            log.debug("第{}步滚动: {}px", (i + 1), stepDistance);
            page.evaluate("window.scrollBy(0, " + stepDistance + ")");

            // 步骤间随机停顿
            if (i < steps - 1) {
                int pauseTime = 150 + ThreadLocalRandom.current().nextInt(300); // 150-450ms
                page.waitForTimeout(pauseTime);
            }
        }
    }

    /**
     * 随机距离滚动
     */
    private static void performRandomDistanceScroll(Page page, int windowHeight) {
        log.debug("使用随机距离滚动策略");
        int scrollDistance = (int) (windowHeight * (ScrapingConfiguration.MIN_SCROLL_HEIGHT_RATIO + ThreadLocalRandom.current().nextDouble() * (ScrapingConfiguration.MAX_SCROLL_HEIGHT_RATIO - ScrapingConfiguration.MIN_SCROLL_HEIGHT_RATIO))); // 50%-130%窗口高度
        log.debug("滚动距离: {}px", scrollDistance);
        page.evaluate("window.scrollBy(0, " + scrollDistance + ")");
    }

    /**
     * 处理页面重定向（登录检测、安全验证等）
     *
     * @param page      页面对象
     * @param targetUrl 目标URL
     * @return true表示处理成功可以继续，false表示需要终止程序
     */
    private static boolean handlePageRedirect(Page page, String targetUrl) {
        try {
            // 检测是否发生重定向
            String currentUrl = page.url();
            if (currentUrl.equals(targetUrl)) {
                log.info("【重定向】未发生重定向，直接访问目标页面");
                return true;
            }

            log.info("【重定向】检测到页面重定向: {} -> {}", targetUrl, currentUrl);

            // 检查重定向类型并处理
            if (currentUrl.contains("/accounts/login")) {
                return handleLoginRedirect(page, targetUrl);
            } else if (currentUrl.contains("/challenge/")) {
                log.warn("【重定向】检测到安全验证页面，程序终止");
                return false;
            } else if (!currentUrl.contains("instagram.com")) {
                log.warn("【重定向】页面被重定向到非Instagram域名，程序终止");
                return false;
            } else {
                log.info("【重定向】检测到其他类型重定向，继续执行...");
                return true;
            }

        } catch (Exception e) {
            log.error("【重定向】处理重定向时发生异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 处理登录页面重定向
     *
     * @param page      页面对象
     * @param targetUrl 目标URL
     * @return true表示登录成功，false表示登录失败
     */
    private static boolean handleLoginRedirect(Page page, String targetUrl) {
        try {
            log.info("【登录重定向】检测到登录页面，开始自动登录流程...");

            // 对登录页面截图分析
            byte[] screenshot = page.screenshot();
            String screenshotPath = "/tmp/instagram_login.png";
            try {
                java.nio.file.Files.write(Paths.get(screenshotPath), screenshot);
                log.info("【登录重定向】已保存登录页面截图: {}", screenshotPath);
            } catch (Exception e) {
                log.warn("【登录重定向】截图保存失败: {}", e.getMessage());
            }

            // 尝试自动登录
            boolean loginSuccess = performAutoLogin(page);
            if (!loginSuccess) {
                log.error("【登录重定向】自动登录失败");
                return false;
            }

            // 登录成功后重新导航到目标页面
            log.info("【登录重定向】登录成功，重新导航到目标页面...");
            page.navigate(targetUrl);
            page.waitForTimeout(5000); // 等待页面加载

            // 检查是否成功到达目标页面
            String finalUrl = page.url();
            log.info("【登录重定向】导航后的最终URL: {}", finalUrl);

            if (finalUrl.contains("/accounts/login")) {
                log.error("【登录重定向】重新导航后仍在登录页面，登录验证失败");
                return false;
            }

            log.info("【登录重定向】登录流程完成，成功到达目标页面");
            return true;

        } catch (Exception e) {
            log.error("【登录重定向】处理登录重定向时发生异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 执行自动登录
     */
    private static boolean performAutoLogin(Page page) {
        try {
            log.info("【登录】开始分析登录页面...");

            // 等待页面加载完成
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(2000);

            // 查找用户名输入框
            String usernameSelector = "input[name='username'], input[aria-label*='用户名'], input[placeholder*='用户名'], input[placeholder*='Phone number, username, or email']";
            if (!page.isVisible(usernameSelector)) {
                log.error("【登录】未找到用户名输入框");
                return false;
            }

            // 查找密码输入框
            String passwordSelector = "input[name='password'], input[type='password'], input[aria-label*='密码'], input[placeholder*='密码']";
            if (!page.isVisible(passwordSelector)) {
                log.error("【登录】未找到密码输入框");
                return false;
            }

            log.info("【登录】找到登录表单，开始填写...");

            // 模拟人类输入行为
            page.click(usernameSelector);
            page.waitForTimeout(500);
            page.fill(usernameSelector, "sz.miguo@gmail.com");
            page.waitForTimeout(800);

            page.click(passwordSelector);
            page.waitForTimeout(500);
            page.fill(passwordSelector, "JujYK8_SpZeZca.");
            page.waitForTimeout(1000);

            // 查找登录按钮
            String loginButtonSelector = "button[type='submit'], button:has-text('登录'), button:has-text('Log in'), button:has-text('Log In')";
            if (!page.isVisible(loginButtonSelector)) {
                log.error("【登录】未找到登录按钮");
                return false;
            }

            log.info("【登录】点击登录按钮...");
            page.click(loginButtonSelector);

            // 等待登录完成，检查是否重定向
            log.info("【登录】等待登录响应...");
            page.waitForTimeout(3000);

            // 检查登录是否成功 - 循环检查最多10秒
            boolean loginSuccess = false;
            for (int i = 0; i < 10; i++) {
                String currentUrl = page.url();
                log.debug("【登录】检查URL ({}/10): {}", (i + 1), currentUrl);

                if (!currentUrl.contains("/accounts/login")) {
                    loginSuccess = true;
                    log.info("【登录】登录成功，当前页面: {}", currentUrl);
                    break;
                }

                // 检查是否有错误提示
                try {
                    if (page.isVisible("div:has-text('抱歉，您的密码不正确'), div:has-text('Sorry, your password was incorrect')")) {
                        log.error("【登录】密码错误");
                        return false;
                    }
                    if (page.isVisible("div:has-text('用户不存在'), div:has-text('The username you entered doesn')")) {
                        log.error("【登录】用户名不存在");
                        return false;
                    }
                } catch (Exception e) {
                    // 忽略检查异常
                }

                page.waitForTimeout(1000);
            }

            if (loginSuccess) {
                // 处理可能的后续步骤（如保存登录信息提示等）
                try {
                    // 等待可能的弹窗或提示
                    page.waitForTimeout(3000);

                    // 首先尝试保存登录信息
                    String saveLoginSelector = "button:has-text('保存信息'), button:has-text('Save Info'), button:has-text('保存'), button:has-text('Save')";
                    if (page.isVisible(saveLoginSelector)) {
                        log.info("【登录】找到保存登录信息选项，点击保存...");
                        page.click(saveLoginSelector);
                        page.waitForTimeout(2000);
                    }

                    // 如果没有保存选项，尝试点击"稍后再说"
                    String laterButtonSelector = "button:has-text('稍后再说'), button:has-text('Not Now'), button:has-text('以后再说'), button:has-text('现在不')";
                    if (page.isVisible(laterButtonSelector)) {
                        log.info("【登录】点击稍后再说按钮...");
                        page.click(laterButtonSelector);
                        page.waitForTimeout(2000);
                    }

                    // 再次检查是否有保存登录信息的弹窗
                    page.waitForTimeout(1000);
                    if (page.isVisible(saveLoginSelector)) {
                        log.info("【登录】再次找到保存登录信息选项，点击保存...");
                        page.click(saveLoginSelector);
                        page.waitForTimeout(2000);
                    }

                    // 处理通知权限弹窗
                    String notificationCloseSelector = "button:has-text('现在不'), button[aria-label='Close'], button:has-text('Not Now'), button:has-text('关闭')";
                    if (page.isVisible(notificationCloseSelector)) {
                        log.info("【登录】处理通知权限弹窗...");
                        page.click(notificationCloseSelector);
                        page.waitForTimeout(1000);
                    }

                    // 最后检查是否还有其他弹窗需要处理
                    page.waitForTimeout(1000);
                    log.info("【登录】登录后处理完成，当前URL: {}", page.url());

                } catch (Exception e) {
                    log.warn("【登录】处理登录后步骤时出现异常: {}", e.getMessage());
                }

                return true;
            } else {
                log.error("【登录】登录失败，仍在登录页面");
                return false;
            }

        } catch (Exception e) {
            log.error("【登录】自动登录过程中出现异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 模拟真实用户滚动行为（快-慢-快的节奏）
     */
    private static void performRealisticScroll(Page page, int windowHeight) {
        log.debug("使用真实用户滚动策略");
        int totalDistance = (int) (windowHeight * (0.8 + ThreadLocalRandom.current().nextDouble() * 0.4)); // 80%-120%窗口高度

        // 分3段：快滚动-慢滚动-快滚动
        int[] phases = {
                (int) (totalDistance * 0.4), // 40%
                (int) (totalDistance * 0.3), // 30%
                (int) (totalDistance * 0.3)  // 30%
        };

        int[] delays = {50, 200, 80}; // 快-慢-快的延迟时间

        for (int phase = 0; phase < 3; phase++) {
            int phaseDistance = phases[phase];
            int phaseSteps = phase == 1 ? 3 : 2; // 慢滚动阶段分更多步

            for (int step = 0; step < phaseSteps; step++) {
                int stepDistance = phaseDistance / phaseSteps;
                log.debug("阶段{}步骤{}: {}px", (phase + 1), (step + 1), stepDistance);
                page.evaluate("window.scrollBy(0, " + stepDistance + ")");

                if (step < phaseSteps - 1 || phase < 2) {
                    page.waitForTimeout(delays[phase] + ThreadLocalRandom.current().nextInt(50));
                }
            }
        }
    }


    /**
     * 判断响应是否为Reels数据
     */
    private static boolean isReelsDataResponse(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return false;
        }

        try {
            // 检查响应是否包含我们需要的关键字段
            return responseBody.contains("xdt_api__v1__clips__user__connection_v2") &&
                    responseBody.contains("play_count") &&
                    responseBody.contains("like_count") &&
                    responseBody.contains("comment_count") &&
                    responseBody.contains("\"code\":");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从GraphQL响应中解析Reels数据 (使用Gson优化)
     */
    public static List<PostData> parseReelsFromResponse(String responseBody) {
        List<PostData> reels = new ArrayList<>();

        try {
            // 使用Gson解析JSON响应
            InstagramGraphQLResponse response = gson.fromJson(responseBody, InstagramGraphQLResponse.class);

            if (response != null &&
                    response.getData() != null &&
                    response.getData().getConnection() != null &&
                    response.getData().getConnection().getEdges() != null) {

                for (InstagramGraphQLResponse.Edge edge : response.getData().getConnection().getEdges()) {
                    if (edge.getNode() != null && edge.getNode().getMedia() != null) {
                        PostData reel = createReelFromMedia(edge.getNode().getMedia());
                        reels.add(reel);
                    }
                }

                log.info("【Gson解析】成功解析到 {} 条Reels数据", reels.size());
            }

        } catch (JsonSyntaxException e) {
            log.error("【Gson解析】JSON格式错误: {}", e.getMessage());
        } catch (Exception e) {
            log.error("【Gson解析】解析响应失败: {}", e.getMessage());
        }

        return reels;
    }

    public static List<PostData> startScrapeReel(List<String> InstagramUserNames, Integer maxCollectedCount) {

        if (browserContext == null) {
            initBrowser();
        }

        List<PostData> dataList = new ArrayList<>();
        log.info("=== Instagram 用户 Reels 数据批量提取工具启动 ===");

        if (CollUtil.isEmpty(InstagramUserNames)) {
            log.warn("=== 待抓取的 Instagram 用户列表为空");
            return dataList;
        }

        int collectedCount = maxCollectedCount == null ? ScrapingConfiguration.getMaxCollectionCount() : maxCollectedCount;
        log.info("收集信息条数: {}", collectedCount);


        for (String username : InstagramUserNames) {

            log.info("开始使用网络请求拦截策略获取用户 @{} 的最新{}条Reels数据...", username, collectedCount);
            List<PostData> reelsData = getUserReelsData(username, collectedCount);

            log.info("=== 数据提取结果 ===");
            if (CollUtil.isEmpty(reelsData)) {
                log.warn("未能获取到用户 {} 任何Reels数据", username);
            } else {
                log.info("成功获取到用户 {} , {} 条Reels数据", username, reelsData.size());

                for (PostData reelData : reelsData) {
                    reelData.setSocialMedia(SocialMediaPlatform.INSTAGRAM.val);
                    reelData.setPostUserName(username);
                    dataList.add(reelData);
                }

            }
        }
        closeBrowser();
        return dataList;

    }

    /**
     * 从媒体数据创建ReelData对象
     */
    private static PostData createReelFromMedia(InstagramGraphQLResponse.Media media) {
        return new PostData(
                media.getCode() != null ? media.getCode() : "",
                media.getPlayCount() != null ? media.getPlayCount() : -1,
                media.getLikeCount() != null ? media.getLikeCount() : -1,
                media.getCommentCount() != null ? media.getCommentCount() : -1,
                media.getPk() != null ? media.getPk() : "",
                media.getMediaType() != null ? media.getMediaType() : -1,
                (media.getUser() != null && media.getUser().getPk() != null) ? media.getUser().getPk() : ""
        );
    }

    /**
     * 初始化浏览器
     */
    private static void initBrowser() {
        playwright = Playwright.create();

        BrowserType.LaunchPersistentContextOptions options = new BrowserType.LaunchPersistentContextOptions()
                .setHeadless(ScrapingConfiguration.HEADLESS_MODE)
                .setUserAgent(ScrapingConfiguration.USER_AGENT)
                .setExecutablePath(Paths.get(ScrapingConfiguration.getChromeExecutablePath()));

        // 应用Stealth模式配置
        if (ScrapingConfiguration.HEADLESS_MODE) {
            log.info("启用无头模式 + Stealth配置...");
            options.setArgs(PlaywrightStealth.getStealthArgs());
        }

        browserContext = playwright.chromium().launchPersistentContext(
                Paths.get(ScrapingConfiguration.getUserDataDir()), options);

        // 为浏览器上下文应用Stealth配置
        PlaywrightStealth.applyStealth(browserContext);

        log.info("浏览器初始化完成，使用本地Chrome: {}", ScrapingConfiguration.getChromeExecutablePath());
        log.info("Stealth模式已启用");
    }

    /**
     * 关闭浏览器资源
     */
    public static void closeBrowser() {
        if (browserContext != null) {
            browserContext.close();
            browserContext = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}