package com.mecoo.spider.service.impl;


import cn.hutool.core.collection.CollUtil;
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
    PostDataMapper postDataMapper;

    @Override
    public int batchInsert(List<PostData> list) {

        if (CollUtil.isEmpty(list)) {
            return 0;
        }

        return postDataMapper.batchInsert(list);

    }
}
