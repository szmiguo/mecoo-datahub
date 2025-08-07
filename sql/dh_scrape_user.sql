-- ----------------------------
-- Table structure for dh_scrape_user
-- ----------------------------
DROP TABLE IF EXISTS `dh_scrape_user`;
CREATE TABLE `dh_scrape_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `social_media` varchar(50) NOT NULL COMMENT '社交平台名称（如Instagram、tiktok等）',
  `user_name` varchar(100) NOT NULL COMMENT '社交平台用户名',
  `start_time` datetime NOT NULL COMMENT '开始抓取时间',
  `end_time` datetime NOT NULL COMMENT '结束抓取时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_social_time` (`social_media`, `start_time`, `end_time`) COMMENT '社交平台和时间复合索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待抓取用户信息表';

-- ----------------------------
-- Sample data for dh_scrape_user
-- ----------------------------
INSERT INTO `dh_scrape_user` (`social_media`, `user_name`, `start_time`, `end_time`) VALUES
('instagram', 'dindaalamanda_', '2024-01-01 00:00:00', '2025-12-31 23:59:59'),
('instagram', 'mecoo.id_official', '2024-01-01 00:00:00', '2025-12-31 23:59:59'),
('instagram', 'alnaycakery', '2024-01-01 00:00:00', '2025-12-31 23:59:59');