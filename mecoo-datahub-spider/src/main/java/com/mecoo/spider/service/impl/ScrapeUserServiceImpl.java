package com.mecoo.spider.service.impl;

import com.mecoo.spider.domain.ScrapeUser;
import com.mecoo.spider.mapper.ScrapeUserMapper;
import com.mecoo.spider.service.IScrapeUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 待抓取用户信息 Service 实现类
 */
@Slf4j
@Service
public class ScrapeUserServiceImpl implements IScrapeUserService {

    @Autowired
    private ScrapeUserMapper scrapeUserMapper;

    @Override
    public List<ScrapeUser> getValidUsers(String socialMedia, LocalDateTime currentTime) {
        log.debug("查询有效用户列表: socialMedia={}, currentTime={}", socialMedia, currentTime);
        List<ScrapeUser> users = scrapeUserMapper.selectValidUsers(socialMedia, currentTime);
        log.info("找到 {} 个有效的 {} 用户", users.size(), socialMedia);
        return users;
    }

    @Override
    public List<ScrapeUser> getValidUsers(String socialMedia) {
        return getValidUsers(socialMedia, LocalDateTime.now());
    }
}