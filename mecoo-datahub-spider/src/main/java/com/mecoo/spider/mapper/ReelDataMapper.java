package com.mecoo.spider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mecoo.spider.domain.ReelData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ReelData的Mapper接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface ReelDataMapper extends BaseMapper<ReelData> {
    
    /**
     * 根据shortCode查询Reel数据
     * @param shortCode 短码
     * @return ReelData
     */
    @Select("SELECT * FROM reel_data WHERE short_code = #{shortCode}")
    ReelData selectByShortCode(String shortCode);
    
    /**
     * 根据postUserId查询用户的所有Reel数据
     * @param postUserId 用户ID
     * @return List<ReelData>
     */
    @Select("SELECT * FROM reel_data WHERE post_user_id = #{postUserId} ORDER BY created_time DESC")
    List<ReelData> selectByUserId(String postUserId);
    
    /**
     * 查询播放量大于指定值的Reel数据
     * @param playCount 播放量阈值
     * @return List<ReelData>
     */
    @Select("SELECT * FROM reel_data WHERE play_count > #{playCount} ORDER BY play_count DESC")
    List<ReelData> selectByPlayCountGreaterThan(int playCount);
    
    /**
     * 查询点赞量大于指定值的Reel数据
     * @param likeCount 点赞量阈值
     * @return List<ReelData>
     */
    @Select("SELECT * FROM reel_data WHERE like_count > #{likeCount} ORDER BY like_count DESC")
    List<ReelData> selectByLikeCountGreaterThan(int likeCount);
}