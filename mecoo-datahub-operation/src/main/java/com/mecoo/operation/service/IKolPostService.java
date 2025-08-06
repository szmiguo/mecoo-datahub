package com.mecoo.operation.service;

import com.mecoo.operation.domain.KolPost;

import java.util.List;

/**
 * KOL Social Media Service
 *
 * @author: lin
 * @date: 2025-06-26 14:55
 */
public interface IKolPostService {

    String hi();


    String createInsReelsSracpeTask(List<String> reelsLinks);


    int saveKolSmVideos(List<KolPost> posts);


}
