package com.mecoo.spider.service.impl;


import com.mecoo.spider.domain.PostData;
import com.mecoo.spider.mapper.PostDataMapper;
import com.mecoo.spider.service.IPostDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: lin
 * @date: 2025-06-26 17:46
 */
@Slf4j
@Service
public class PostDataServiceImpl implements IPostDataService {

    @Autowired
    PostDataMapper postDataMapper ;

    @Override
    public int batchInsert(List<PostData> list) {
        if (list == null || list.isEmpty()) {
            log.warn("批量插入PostData失败：数据列表为空");
            return 0;
        }
        
        try {
            log.info("开始批量插入PostData，数据量：{}", list.size());
            int result = postDataMapper.batchInsert(list);
            log.info("批量插入PostData完成，成功插入：{} 条记录", result);
            return result;
        } catch (Exception e) {
            log.error("批量插入PostData时发生异常", e);
            throw new RuntimeException("批量插入PostData失败", e);
        }
    }
}
