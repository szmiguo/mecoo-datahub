package com.mecoo.spider.task;


import com.mecoo.spider.domain.PostData;
import com.mecoo.spider.scrapers.InstagramScraper;
import com.mecoo.spider.service.IPostDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        
        try {
            List<PostData> postDataList = InstagramScraper.startScrape();
            
            if (postDataList != null && !postDataList.isEmpty()) {
                log.info("成功抓取到 {} 条帖子数据，开始保存到数据库", postDataList.size());
                
                int savedCount = postDataService.batchInsert(postDataList);
                log.info("成功保存 {} 条帖子数据到数据库", savedCount);
            } else {
                log.warn("未抓取到任何帖子数据");
            }
        } catch (Exception e) {
            log.error("抓取或保存社交媒体帖子数据时发生错误", e);
        }
        
        log.info("社交媒体帖子抓取任务完成");
    }



}
