package com.mecoo.spider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mecoo.spider.domain.ScrapeUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 待抓取用户信息 Mapper 接口
 */
@Mapper
public interface ScrapeUserMapper extends BaseMapper<ScrapeUser> {

    /**
     * 根据社交媒体平台和时间范围查询有效的用户列表
     * @param socialMedia 社交媒体平台
     * @param currentTime 当前时间
     * @return 有效用户列表
     */
    @Select("SELECT id, social_media, user_name, start_time, end_time, create_time, update_time " +
            "FROM dh_scrape_user " +
            "WHERE social_media = #{socialMedia} " +
            "AND #{currentTime} >= start_time " +
            "AND #{currentTime} <= end_time " +
            "ORDER BY id ASC")
    List<ScrapeUser> selectValidUsers(@Param("socialMedia") String socialMedia, @Param("currentTime") LocalDateTime currentTime);
}