-- argus_db.argus_job definition
SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `argus_job`;
CREATE TABLE `argus_job`
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `seq`            varchar(255) NOT NULL COMMENT 'job sequence, unique',
    `module`         varchar(64)  NOT NULL DEFAULT '' COMMENT 'job module',
    `name`           varchar(30)  NOT NULL DEFAULT '' COMMENT 'job name',
    `job`            varchar(256) NOT NULL DEFAULT '' COMMENT 'job handler name',
    `status`         tinyint unsigned NOT NULL COMMENT 'job status. 0-pending，1-processing，2-finished，3-stopped，4-fail，5-error，6-reject',
    `params`         text                  DEFAULT NULL COMMENT 'job params',
    `start_time`     datetime              DEFAULT NULL COMMENT 'start time of job',
    `end_time`       datetime              DEFAULT NULL COMMENT 'end time of job',
    `duration`       bigint unsigned DEFAULT NULL COMMENT 'execution time (in ms)',
    `reason`         varchar(255)          DEFAULT NULL COMMENT 'error message',
    `module_version` varchar(32)           DEFAULT NULL COMMENT 'module version',
    `extra`          text                  DEFAULT NULL COMMENT 'extra information',
    `created_time`   datetime              DEFAULT NULL COMMENT 'create time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_job` (`seq`,`module`) USING BTREE,
    KEY              `idx_module` (`module`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
