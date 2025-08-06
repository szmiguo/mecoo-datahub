package com.mecoo.spider.task;


import com.mecoo.spider.scrapers.InstagramScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: lin
 * @date: 2025-06-26 16:01
 */
@Slf4j
@Component("ScrapeTask")
public class ScrapeTask {


    /**
     * 抓取社交平台帖子，该任务每天执行一次
     */
    public void scrapeSocialMediaPost() {

        InstagramScraper.startScrape();

    }



}
