package com.mecoo.operation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.mecoo.operation.domain.KolPost;
import com.mecoo.operation.domain.KolPostRecord;
import com.mecoo.operation.mapper.KolSmVideoMapper;
import com.mecoo.operation.service.IKoPostRecordService;
import com.mecoo.operation.service.IKolPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author: lin
 * @date: 2025-06-26 17:46
 */
@Slf4j
@Service
public class KolPostServiceImpl implements IKolPostService {

    @Autowired
    private KolSmVideoMapper kolSmVideoMapper;


    @Autowired
    private IKoPostRecordService kolSmRecordService;
    ;

    @Override
    public String hi() {
        return "";
    }

    @Override
    public String createInsReelsSracpeTask(List<String> reelsLinks) {
        return "";
    }

    @Transactional
    @Override
    public int saveKolSmVideos(List<KolPost> posts) {

        if (CollUtil.isEmpty(posts)) {
            return 0;
        }

        Date currentTime = new Date();
        List<KolPostRecord> kolPostRecords = BeanUtil.copyToList(posts, KolPostRecord.class);
        KolPostRecord.batchInitTime(kolPostRecords, currentTime);
        kolSmRecordService.saveBatch(kolPostRecords);

        log.info("videoList:{}", posts);
        log.info("videoList size:{}", posts.size());

        KolPost.batchInitTime(posts, currentTime);
        for (KolPost video : posts) {

            KolPost kv = kolSmVideoMapper.selectByPostIdAndSocialMedia(video);
            if (kv != null) {
                log.info("prepare update kv:{}", JSON.toJSONString(video));
                video.setId(kv.getId());
                video.setCreateTime(kv.getCreateTime());
                video.refreshUpdateTime();
                int updateCnt = kolSmVideoMapper.updateById(video);
                log.info("update kv:{}, detail:{}",updateCnt, JSON.toJSONString(video));
                continue;
            }

            kolSmVideoMapper.insert(video);
        }
        return 0;
    }
}
