package com.mecoo.operation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mecoo.operation.domain.KolPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


/**
 * KOL发布的社媒视频信息Mapper接口
 *
 * @author mecoo
 * @date 2025-06-26
 */
@Mapper
public interface KolSmVideoMapper extends BaseMapper<KolPost> {

    @Select("select * from op_kol_post where post_id = #{postId} and post_sm = #{postSm} limit 1")
    KolPost selectByPostIdAndSocialMedia(KolPost kolPost);

}
