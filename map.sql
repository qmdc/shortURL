/*
 Navicat Premium Data Transfer

 Source Server         : mysql-mac-shell-3306
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : localhost:3306
 Source Schema         : homework

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 21/09/2023 09:47:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(255) NOT NULL COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Table structure for map
-- ----------------------------
DROP TABLE IF EXISTS `map`;
CREATE TABLE `map` (
  `id` varchar(255) NOT NULL COMMENT '主键ID',
  `user_id` varchar(255) DEFAULT NULL COMMENT '用户ID',
  `url` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `md5` varchar(5000) DEFAULT NULL,
  `random_url` varchar(100) DEFAULT NULL,
  `email_num` int DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_random_url` (`random_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='短链接映射表';

-- ----------------------------
-- Table structure for visit_record
-- ----------------------------
DROP TABLE IF EXISTS `visit_record`;
CREATE TABLE `visit_record` (
  `id` varchar(255) NOT NULL COMMENT '主键ID',
  `map_id` varchar(255) DEFAULT NULL COMMENT '短链接映射ID',
  `url` varchar(5000) DEFAULT NULL COMMENT '原URL',
  `ip` varchar(64) DEFAULT NULL COMMENT '访问IP',
  `user_agent` varchar(2000) DEFAULT NULL COMMENT '用户代理',
  `referer` varchar(1000) DEFAULT NULL COMMENT '来源页面',
  `country` varchar(50) DEFAULT NULL COMMENT '国家',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `device_type` varchar(32) DEFAULT NULL COMMENT '设备类型',
  `browser` varchar(64) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(64) DEFAULT NULL COMMENT '操作系统',
  `is_unique` tinyint DEFAULT '1' COMMENT '是否唯一访问(同一IP5分钟内多次访问算一次)',
  `create_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`id`),
  KEY `idx_map_id` (`map_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_ip` (`ip`),
  KEY `idx_map_create` (`map_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='访问记录表';

-- ----------------------------
-- Table structure for visit_record_archive
-- ----------------------------
DROP TABLE IF EXISTS `visit_record_archive`;
CREATE TABLE `visit_record_archive` (
  `id` varchar(255) NOT NULL COMMENT '主键ID',
  `map_id` varchar(255) DEFAULT NULL COMMENT '短链接映射ID',
  `url` varchar(5000) DEFAULT NULL COMMENT '原URL',
  `ip` varchar(64) DEFAULT NULL COMMENT '访问IP',
  `user_agent` varchar(2000) DEFAULT NULL COMMENT '用户代理',
  `referer` varchar(1000) DEFAULT NULL COMMENT '来源页面',
  `country` varchar(50) DEFAULT NULL COMMENT '国家',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `device_type` varchar(32) DEFAULT NULL COMMENT '设备类型',
  `browser` varchar(64) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(64) DEFAULT NULL COMMENT '操作系统',
  `is_unique` tinyint DEFAULT '1' COMMENT '是否唯一访问',
  `create_time` datetime DEFAULT NULL COMMENT '访问时间',
  `archive_time` datetime DEFAULT NULL COMMENT '归档时间',
  PRIMARY KEY (`id`),
  KEY `idx_map_id` (`map_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='访问记录归档表';

SET FOREIGN_KEY_CHECKS = 1;
