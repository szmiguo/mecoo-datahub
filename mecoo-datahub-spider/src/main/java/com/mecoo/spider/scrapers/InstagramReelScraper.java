package com.mecoo.spider.scrapers;

import cn.hutool.core.collection.CollUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mecoo.spider.config.ScrapingConfiguration;
import com.mecoo.spider.domain.InstagramGraphQLResponse;
import com.mecoo.spider.domain.PostData;
import com.mecoo.spider.enums.SocialMediaPlatform;
import com.mecoo.spider.util.MediaTypeUtil;
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

import static com.mecoo.spider.scrapers.InstagramWebUtil.handleInstagramPageRedirect;

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
                            log.info("拦截到Reels数据响应，长度: {}", responseBody.length());
                            interceptedResponses.add(responseBody);
                        }
                    } catch (Exception e) {
                        log.warn("读取响应失败: {}", e.getMessage());
                    }
                }
            });

            String reelsUrl = "https://www.instagram.com/" + username + "/reels/";
            log.info("导航到用户Reels页面: {}", reelsUrl);
            page.navigate(reelsUrl);

            // 处理页面重定向（登录检测等）
            if (!handleInstagramPageRedirect(page, reelsUrl)) {
                page.close();
                return reelsData;
            }

            // 等待页面基本加载
            try {
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
                page.waitForTimeout(ScrapingConfiguration.PAGE_LOAD_TIMEOUT);
            } catch (Exception e) {
                log.warn("页面加载等待异常: {}", e.getMessage());
            }

            log.info("开始收集Reels数据，目标: {} 条", maxCollectedCount);

            // 滚动触发更多网络请求
            int maxScrolls = ScrapingConfiguration.DEFAULT_MAX_SCROLL_ATTEMPTS;
            int scrollCount = 0;
            int initialDataCount = reelsData.size();

            while (reelsData.size() < maxCollectedCount && scrollCount < maxScrolls) {
                scrollCount++;
                log.info("第 {} 次滚动，已收集: {} 条", scrollCount, reelsData.size());

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
                    log.debug("模拟观看内容，等待 {} 毫秒...", viewingDelay);
                    page.waitForTimeout(viewingDelay);

                    // 偶尔模拟用户暂停浏览
                    if (ThreadLocalRandom.current().nextInt(100) < ScrapingConfiguration.PAUSE_PROBABILITY) {
                        int pauseTime = ScrapingConfiguration.PAUSE_TIME_MIN +
                                ThreadLocalRandom.current().nextInt(ScrapingConfiguration.PAUSE_TIME_RANGE);
                        log.debug("模拟用户暂停浏览，等待 {} 毫秒...", pauseTime);
                        page.waitForTimeout(pauseTime);
                    }

                    // 随机选择滚动前的准备行为
                    if (ThreadLocalRandom.current().nextInt(100) < ScrapingConfiguration.MOUSE_MOVE_PROBABILITY) {
                        log.debug("模拟真实用户鼠标移动...");
                        try {
                            // 使用更真实的鼠标移动模拟
                            PlaywrightStealth.simulateHumanMouseMovement(page);
                        } catch (Exception e) {
                            log.warn("鼠标移动失败: {}", e.getMessage());
                        }
                    }

                    // 执行人性化滚动
                    log.debug("开始执行人性化滚动...");
                    performHumanLikeScroll(page);

                    // 滚动后观察新内容的等待时间
                    int observeDelay = ScrapingConfiguration.OBSERVE_DELAY_MIN +
                            ThreadLocalRandom.current().nextInt(ScrapingConfiguration.OBSERVE_DELAY_RANGE);
                    log.debug("滚动后观察新内容，等待 {} 毫秒...", observeDelay);
                    page.waitForTimeout(observeDelay);

                    // 等待网络请求完成，使用更智能的等待策略
                    try {
                        page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(ScrapingConfiguration.NETWORK_IDLE_TIMEOUT));
                        log.debug("网络请求已稳定");
                    } catch (Exception e) {
                        // 如果网络空闲等待超时，则继续等待一段时间
                        log.debug("网络空闲等待超时，继续等待...");
                        page.waitForTimeout(1000);
                    }
                }

                // 优化：如果前3次滚动都没有收集到任何数据，提前退出
                if (scrollCount >= 3 && reelsData.size() == initialDataCount) {
                    log.warn("【优化退出】前 {} 次滚动未收集到任何数据，可能用户不存在或无公开Reels，提前退出", scrollCount);
                    break;
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
            log.info("数据收集完成，共收集到 {} 条Reels数据", reelsData.size());

        } catch (Exception e) {
            log.error("获取用户Reels数据失败: {}", e.getMessage(), e);
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


    public static List<PostData> startScrapeReel(String userName, Integer maxCollectedCount) {

        if (browserContext == null) {
            initBrowser();
        }

        log.info("=== Instagram 用户 Reels 数据批量提取工具启动 ===");

        int collectedCount = maxCollectedCount == null ? ScrapingConfiguration.getMaxCollectionCount() : maxCollectedCount;
        log.info("本次收集信息条数: {}", collectedCount);

        log.info("开始使用网络请求拦截策略获取用户 @{} 的最新{}条Reels数据...", userName, collectedCount);
        List<PostData> reelsData = getUserReelsData(userName, collectedCount);

        log.info("=== 数据提取结果 ===");
        if (CollUtil.isEmpty(reelsData)) {
            log.warn("未能获取到用户 {} 任何Reels数据", userName);
        } else {
            log.info("成功获取到用户 {} , {} 条Reels数据", userName, reelsData.size());

            for (PostData reelData : reelsData) {
                reelData.setSocialMedia(SocialMediaPlatform.INSTAGRAM.val);
                reelData.setPostUserName(userName);

            }

        }
        closeBrowser();
        return reelsData;
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
                media.getMediaType() != null ? MediaTypeUtil.getMediaTypeDescription(media.getMediaType()) : "unknown",
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