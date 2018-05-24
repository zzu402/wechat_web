/*
Navicat MySQL Data Transfer

Source Server         : huang
Source Server Version : 50715
Source Host           : localhost:3306
Source Database       : wechat

Target Server Type    : MYSQL
Target Server Version : 50715
File Encoding         : 65001

Date: 2018-04-28 19:48:19
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `wechat_user`
-- ----------------------------
DROP TABLE IF EXISTS `wechat_user`;
CREATE TABLE `wechat_user` (
  `id` bigint(16) NOT NULL AUTO_INCREMENT,
  `nickName` varchar(64) DEFAULT NULL COMMENT '昵称',
  `name` varchar(64) NOT NULL COMMENT '登陆用户名',
  `password` varchar(64) NOT NULL COMMENT '密码，不存储明文，存储md5加密密文',
  `phone` varchar(11) DEFAULT '' COMMENT '手机号',
  `email` varchar(64) DEFAULT NULL,
  `sex` int(11) DEFAULT '0' COMMENT '性别 0-男 1-女 2-未知',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0-正常 1-锁定 2-注销',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '0-普通用户 1-管理员 ',
  `secretKey` varchar(128) DEFAULT NULL COMMENT '密钥',
  `userKey` varchar(128) DEFAULT NULL COMMENT '根据用户信息生成的用户key',
  `createTime` bigint(32) NOT NULL DEFAULT '0' COMMENT '注册时间 精确到秒',
  `salt` varchar(128) DEFAULT NULL COMMENT '加盐信息',
  `roleId` bigint(16) DEFAULT NULL COMMENT '角色ID',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of wechat_user
-- ----------------------------

-- ----------------------------
-- Table structure for `wechat_user_privilege`
-- ----------------------------
DROP TABLE IF EXISTS `wechat_user_privilege`;
CREATE TABLE `wechat_user_privilege` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '权限名，不允许重复',
  `categoryName` varchar(100) NOT NULL COMMENT '权限分类名称',
  `displayName` varchar(100) NOT NULL COMMENT '权限显示名称',
  `createTime` bigint(20) DEFAULT NULL COMMENT '创建时间(秒)',
  `updateTime` bigint(20) DEFAULT NULL COMMENT '上次修改时间(秒)',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户权限表';

-- ----------------------------
-- Records of wechat_user_privilege
-- ----------------------------

-- ----------------------------
-- Table structure for `wechat_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `wechat_user_role`;
CREATE TABLE `wechat_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '角色名，不允许重复',
  `displayName` varchar(100) NOT NULL COMMENT '角色显示名称',
  `privileges` varchar(1000) DEFAULT NULL COMMENT '角色权限，用逗号分隔',
  `roleType` tinyint(4) NOT NULL COMMENT '角色类型：1-普通用户，2-内部管理员，3-会员',
  `extInfo` varchar(1000) DEFAULT NULL COMMENT '额外的信息（json）',
  `comments` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `createTime` bigint(20) DEFAULT NULL COMMENT '创建时间(秒)',
  `updateTime` bigint(20) DEFAULT NULL COMMENT '上次修改时间(秒)',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户角色表';

-- ----------------------------
-- Records of wechat_user_role
-- ----------------------------

DROP TABLE IF EXISTS `wechat_verify_info`;
CREATE TABLE `wechat_verify_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `verifyPhone` varchar(20) NOT NULL COMMENT '添加好友手机号',
  `verifyCode` varchar(20) NOT NULL COMMENT '好友求助验证码',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0-创建 1-正在验证 2-验证通过 3-验证失败',
  `comments` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `createTime` bigint(20) DEFAULT NULL COMMENT '创建时间(秒)',
  `updateTime` bigint(20) DEFAULT NULL COMMENT '上次修改时间(秒)',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户验证表';

ALTER  TABLE wechat_user ADD  expireTime bigint(32) NOT NULL DEFAULT '0' COMMENT '会员到期时间 精确到秒';
ALTER  TABLE  wechat_user ADD baiduApiId varchar(64) DEFAULT NULL COMMENT '百度ocr识别apiId';
ALTER  TABLE  wechat_user ADD baiduApiKey varchar(128) DEFAULT NULL COMMENT '百度ocr识别apiKey';
ALTER  TABLE  wechat_user ADD baiduSecretKey varchar(128) DEFAULT NULL COMMENT '百度ocr识别SecretKey';

DROP TABLE IF EXISTS `wechat_config_info`;
CREATE TABLE `wechat_config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '1-有效 2-失效',
  `url` varchar(200) DEFAULT NULL COMMENT '百度下载地址',
  `password` varchar(20) DEFAULT NULL COMMENT '密码',
  `createTime` bigint(20) DEFAULT NULL COMMENT '创建时间(秒)',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置信息';