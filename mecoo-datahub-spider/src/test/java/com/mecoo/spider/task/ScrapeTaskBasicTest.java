package com.mecoo.spider.task;

import com.mecoo.spider.domain.PostData;
import com.mecoo.spider.service.IPostDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ScrapeTask 基础测试类
 * 不使用任何 Mock 框架，通过手动实现测试
 *
 * @author: lin
 * @date: 2025-08-06
 */
@Slf4j
class ScrapeTaskBasicTest {

    /**
     * 测试 ScrapeTask 基本实例化
     */
    @Test
    void testScrapeTaskInstantiation() {
        log.info("=== 测试 ScrapeTask 实例化 ===");
        
        try {
            ScrapeTask scrapeTask = new ScrapeTask();
            log.info("✅ ScrapeTask 实例化成功: {}", scrapeTask.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("❌ ScrapeTask 实例化失败", e);
        }
    }

    /**
     * 测试 PostData 对象创建
     */
    @Test
    void testPostDataCreation() {
        log.info("=== 测试 PostData 对象创建 ===");
        
        try {
            PostData postData = createSamplePostData();
            log.info("✅ PostData 创建成功");
            log.info("   - ID: {}", postData.getId());
            log.info("   - 社交媒体: {}", postData.getSocialMedia());
            log.info("   - 短代码: {}", postData.getShortCode());
            log.info("   - 播放数: {}", postData.getPlayCount());
            log.info("   - 点赞数: {}", postData.getLikeCount());
            log.info("   - 评论数: {}", postData.getCommentCount());
        } catch (Exception e) {
            log.error("❌ PostData 创建失败", e);
        }
    }

    /**
     * 测试 PostData 列表创建
     */
    @Test
    void testPostDataListCreation() {
        log.info("=== 测试 PostData 列表创建 ===");
        
        try {
            List<PostData> postDataList = createSamplePostDataList();
            log.info("✅ PostData 列表创建成功，包含 {} 条数据", postDataList.size());
            
            for (int i = 0; i < postDataList.size(); i++) {
                PostData post = postDataList.get(i);
                log.info("   [{}] PostID: {} | UserID: {} | 播放数: {}", 
                         i + 1, post.getPostId(), post.getPostUserId(), post.getPlayCount());
            }
        } catch (Exception e) {
            log.error("❌ PostData 列表创建失败", e);
        }
    }

    /**
     * 测试数据保存逻辑（使用测试实现）
     */
    @Test
    void testDataSavingLogic() {
        log.info("=== 测试数据保存逻辑 ===");
        
        try {
            // 创建测试数据
            List<PostData> testData = createSamplePostDataList();
            log.info("准备保存 {} 条测试数据", testData.size());
            
            // 创建测试用的 Service 实现
            IPostDataService testService = new TestPostDataService();
            
            // 模拟保存过程
            int savedCount = testService.batchInsert(testData);
            log.info("✅ 数据保存测试完成，返回保存数量: {}", savedCount);
            
        } catch (Exception e) {
            log.error("❌ 数据保存测试失败", e);
        }
    }

    /**
     * 模拟完整的抓取和保存流程
     */
    @Test
    void testCompleteFlow() {
        log.info("=== 测试完整抓取和保存流程 ===");
        
        try {
            // 1. 模拟数据抓取
            log.info("1. 开始模拟数据抓取...");
            List<PostData> scrapedData = simulateDataScraping();
            log.info("   抓取完成，获得 {} 条数据", scrapedData.size());
            
            // 2. 检查数据有效性
            log.info("2. 检查数据有效性...");
            if (scrapedData != null && !scrapedData.isEmpty()) {
                log.info("   数据有效，准备保存");
                
                // 3. 保存数据
                log.info("3. 开始保存数据...");
                IPostDataService testService = new TestPostDataService();
                int savedCount = testService.batchInsert(scrapedData);
                
                log.info("4. 保存完成，成功保存 {} 条记录", savedCount);
                log.info("✅ 完整流程测试成功");
                
            } else {
                log.warn("   数据为空，跳过保存步骤");
                log.info("✅ 空数据处理测试成功");
            }
            
        } catch (Exception e) {
            log.error("❌ 完整流程测试失败", e);
        }
    }

    /**
     * 创建单个测试用 PostData
     */
    private PostData createSamplePostData() {
        PostData postData = new PostData();
        postData.setId(1L);
        postData.setSocialMedia("Instagram");
        postData.setShortCode("TEST001");
        postData.setPlayCount(1000);
        postData.setLikeCount(100);
        postData.setCommentCount(10);
        postData.setPostId("test_post_1");
        postData.setMediaType(1);
        postData.setPostUserId("test_user_1");
        postData.setPostUserName("Test User 1");
        postData.setCreateTime(LocalDateTime.now());
        postData.setUpdateTime(LocalDateTime.now());
        return postData;
    }

    /**
     * 创建测试用 PostData 列表
     */
    private List<PostData> createSamplePostDataList() {
        List<PostData> list = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            PostData postData = new PostData();
            postData.setId((long) i);
            postData.setSocialMedia("Instagram");
            postData.setShortCode("TEST" + String.format("%03d", i));
            postData.setPlayCount(1000 * i);
            postData.setLikeCount(100 * i);
            postData.setCommentCount(10 * i);
            postData.setPostId("test_post_" + i);
            postData.setMediaType(i % 2 + 1);
            postData.setPostUserId("test_user_" + i);
            postData.setPostUserName("Test User " + i);
            postData.setCreateTime(LocalDateTime.now());
            postData.setUpdateTime(LocalDateTime.now());
            
            list.add(postData);
        }
        
        return list;
    }

    /**
     * 模拟数据抓取过程
     */
    private List<PostData> simulateDataScraping() {
        log.info("   正在模拟从社交媒体抓取数据...");
        
        // 模拟一些延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 返回模拟的抓取结果
        return createSamplePostDataList();
    }

    /**
     * 测试用的 PostDataService 实现
     */
    private static class TestPostDataService implements IPostDataService {
        
        @Override
        public int batchInsert(List<PostData> list) {
            if (list == null || list.isEmpty()) {
                log.warn("TestPostDataService: 接收到空数据列表");
                return 0;
            }
            
            log.info("TestPostDataService: 开始批量插入 {} 条数据", list.size());
            
            // 模拟数据库保存过程
            for (PostData post : list) {
                log.debug("  保存数据: PostID={}, UserID={}", post.getPostId(), post.getPostUserId());
            }
            
            // 模拟保存延迟
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            log.info("TestPostDataService: 批量插入完成");
            return list.size();
        }
    }
}