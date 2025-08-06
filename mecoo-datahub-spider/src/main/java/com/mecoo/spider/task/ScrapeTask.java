package com.mecoo.spider.task;


import cn.hutool.core.collection.CollUtil;
import com.mecoo.spider.domain.PostData;
import com.mecoo.spider.scrapers.InstagramReelScraper;
import com.mecoo.spider.service.IPostDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author: lin
 * @date: 2025-06-26 16:01
 */
@Slf4j
@Component("ScrapeTask")
public class ScrapeTask {

    @Autowired
    private IPostDataService postDataService;

    /**
     * 抓取社交平台帖子，该任务每天执行一次
     */
    public void scrapeSocialMediaPost() {
        log.info("开始抓取社交媒体帖子数据");

        List<String> instagramUserNames = Arrays.asList( "dindaalamanda_", "mecoo.id_official", "alnaycakery");
        int maxCollectedCount = 10;

        if (CollUtil.isEmpty(instagramUserNames)) {
            log.warn("=== 待抓取的 Instagram 用户列表为空");
            return;
        }
        int totalCollectedCount = 0;
        for (String username : instagramUserNames) {
            try {
                List<PostData> postDataList = InstagramReelScraper.startScrapeReel(username, maxCollectedCount);

                if (CollUtil.isNotEmpty(postDataList)) {
                    log.info("成功抓取到用户{} {} 条帖子数据，开始保存到数据库", username, postDataList.size());

                    int savedCount = postDataService.batchInsert(postDataList);
                    log.info("成功保存用户{} {} 条帖子数据到数据库", username, savedCount);

                    totalCollectedCount += savedCount;
                } else {
                    log.warn("未抓取到用户{} 任何帖子数据", username);
                }
            } catch (Exception e) {
                log.error("抓取或保存社交媒体帖子数据时发生错误", e);
            }

        }
        log.info("社交媒体帖子抓取任务完成,本次总共抓取{}条", totalCollectedCount);
    }


}
