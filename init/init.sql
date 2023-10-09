-- argus_db.argus_job definition
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


DROP TABLE IF EXISTS `argus_job`;
CREATE TABLE `argus_job` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `seq` varchar(255) NOT NULL COMMENT 'job唯一序列号',
  `module` varchar(64) NOT NULL DEFAULT '' COMMENT 'job所属module',
  `name` varchar(30) NOT NULL DEFAULT '' COMMENT 'job名称',
  `job` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '处理器全类名',
  `params` text COMMENT '参数',
  `status` tinyint unsigned NOT NULL COMMENT '任务状态。0-待执行，1-执行中，2-已完成，3-已停止，4-失败，5-错误，6-拒绝',
  `start_time` datetime DEFAULT NULL COMMENT 'job开始时间',
  `end_time` datetime DEFAULT NULL COMMENT 'job结束时间',
  `duration` bigint unsigned DEFAULT NULL COMMENT '执行耗时(ms)',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `extra` text,
  `reason` varchar(255) DEFAULT NULL COMMENT '错误原因',
  `module_version` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_job` (`seq`,`module`) USING BTREE,
  KEY `idx_module` (`module`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;