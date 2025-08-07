package com.mecoo.spider.service;

import com.mecoo.spider.domain.ScrapeUser;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 待抓取用户信息 Service 接口
 */
public interface IScrapeUserService {

    /**
     * 根据社交媒体平台和当前时间查询有效的用户列表
     * @param socialMedia 社交媒体平台
     * @param currentTime 当前时间
     * @return 有效用户列表
     */
    List<ScrapeUser> getValidUsers(String socialMedia, LocalDateTime currentTime);

    /**
     * 根据社交媒体平台查询当前有效的用户列表
     * @param socialMedia 社交媒体平台
     * @return 有效用户列表
     */
    List<ScrapeUser> getValidUsers(String socialMedia);
}