package com.mecoo.spider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mecoo.spider.domain.PostData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * PostData的Mapper接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface PostDataMapper extends BaseMapper<PostData> {

    /**
     * 批量插入PostData数据
     * @param list 要插入的PostData列表
     * @return 插入成功的记录数
     */
    int batchInsert(@Param("list") List<PostData> list);
}