package com.mecoo.spider.task;


import cn.hutool.core.collection.CollUtil;
import com.mecoo.spider.domain.PostData;
import com.mecoo.spider.domain.ScrapeUser;
import com.mecoo.spider.scrapers.InstagramReelScraper;
import com.mecoo.spider.service.IPostDataService;
import com.mecoo.spider.service.IScrapeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * @author: lin
 * @date: 2025-06-26 16:01
 */
@Slf4j
@Component("ScrapeTask")
public class ScrapeTask {

    @Autowired
    private IPostDataService postDataService;

    @Autowired
    private IScrapeUserService scrapeUserService;

    /**
     * 抓取社交平台帖子，该任务每天执行一次
     */
    public void scrapeSocialMediaPost() {
        log.info("开始抓取社交媒体帖子数据");

        // 从数据库读取有效的Instagram用户列表
        List<ScrapeUser> validUsers = scrapeUserService.getValidUsers("instagram");
        log.info("待抓取的用户列表为 {}",validUsers);
        int maxCollectedCount = 300;

        if (CollUtil.isEmpty(validUsers)) {
            log.warn("=== 待抓取的 Instagram 用户列表为空");
            return;
        }

        int totalCollectedCount = 0;
        Random random = new Random();
        
        for (int i = 0; i < validUsers.size(); i++) {
            ScrapeUser scrapeUser = validUsers.get(i);
            String username = scrapeUser.getUserName();
            try {
                log.info("开始抓取用户 {} 的帖子数据", username);
                List<PostData> postDataList = InstagramReelScraper.startScrapeReel(username, maxCollectedCount);

                if (CollUtil.isNotEmpty(postDataList)) {
                    log.info("成功抓取到用户{} {} 条帖子数据，开始保存到数据库", username, postDataList.size());

                    int savedCount = postDataService.batchInsert(postDataList);
                    log.info("成功保存用户{} {} 条帖子数据到数据库", username, savedCount);

                    totalCollectedCount += savedCount;
                } else {
                    log.warn("未抓取到用户{} 任何帖子数据", username);
                }
                
                // 如果不是最后一个用户，则等待5-10秒再抓取下一个用户
                if (i < validUsers.size() - 1) {
                    int delaySeconds = 5 + random.nextInt(6); // 5-10秒随机延时
                    log.info("抓取完用户 {} ，等待 {} 秒后继续抓取下一个用户", username, delaySeconds);
                    Thread.sleep(delaySeconds * 1000L);
                }
                
            } catch (InterruptedException e) {
                log.warn("等待过程被中断，停止抓取任务");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("抓取或保存用户 {} 的社交媒体帖子数据时发生错误", username, e);
                
                // 即使出错也要等待，避免连续快速请求
                if (i < validUsers.size() - 1) {
                    try {
                        int delaySeconds = 5 + random.nextInt(6);
                        log.info("出错后等待 {} 秒再继续", delaySeconds);
                        Thread.sleep(delaySeconds * 1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        log.info("社交媒体帖子抓取任务完成,本次总共抓取{}条", totalCollectedCount);
    }


}
