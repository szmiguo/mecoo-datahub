package com.mecoo.spider.scrapers;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;

/**
 * @author: lin
 * @date: 2025-08-06 5:00 PM
 */
@Slf4j
public class InstagramWebUtil {

    //访问Instagram的时候，如果发生页面跳转，则按照如下规则处理
    public static boolean handleInstagramPageRedirect(Page page, String targetUrl) {
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


}
