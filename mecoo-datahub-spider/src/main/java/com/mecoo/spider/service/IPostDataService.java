package com.mecoo.spider.service;


import com.mecoo.spider.domain.PostData;

import java.util.List;

/**
 * KOL Social Media Service
 *
 * @author: lin
 * @date: 2025-06-26 14:55
 */
public interface IPostDataService {

    int batchInsert(List<PostData> list);

}
