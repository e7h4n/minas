SET mode MySQL;

SET FOREIGN_KEY_CHECKS=0;



DROP TABLE IF EXISTS `auth_group`;
CREATE TABLE `auth_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE `auth_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(128) NOT NULL,
  `last_login` datetime DEFAULT NULL,
  `is_superuser` tinyint(1) NOT NULL,
  `username` varchar(30) NOT NULL,
  `first_name` varchar(30) NOT NULL,
  `last_name` varchar(30) NOT NULL,
  `email` varchar(254) NOT NULL,
  `is_staff` tinyint(1) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `date_joined` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `auth_user_groups`;
CREATE TABLE `auth_user_groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_user_groups_user_id_94350c0c_uniq` (`user_id`,`group_id`),
  KEY `auth_user_groups_group_id_97559544_fk_auth_group_id` (`group_id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `crm_resident`;
CREATE TABLE `crm_resident` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `mobile_phone` varchar(11) NOT NULL,
  `telephone` varchar(20) NOT NULL,
  `sex` smallint(6) NOT NULL,
  `address` varchar(200) NOT NULL,
  `room_id` int(11) DEFAULT NULL,
  `wechat_user_id` int(11) DEFAULT NULL,
  `verified` tinyint(1) NOT NULL,
  `vote_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `wechat_user_id` (`wechat_user_id`),
  UNIQUE KEY `vote_id` (`vote_id`),
  KEY `crm_resident_b068931c` (`name`),
  KEY `crm_resident_d88c2754` (`mobile_phone`),
  KEY `crm_resident_b9bb7e7b` (`telephone`),
  KEY `crm_resident_8273f993` (`room_id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `crm_room`;
CREATE TABLE `crm_room` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `region` smallint(6) NOT NULL,
  `building` smallint(6) NOT NULL,
  `unit` smallint(6) NOT NULL,
  `house_number` smallint(6) NOT NULL,
  `room_area` double NOT NULL,
  `parking_space_area` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `crm_room_960db2ed` (`region`),
  KEY `crm_room_1b20e902` (`building`),
  KEY `crm_room_3e34bdeb` (`unit`),
  KEY `crm_room_8516ddd8` (`house_number`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `crm_user_config`;
CREATE TABLE `crm_user_config` (
  `userId` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `value` varchar(255) NOT NULL DEFAULT '',
  `createdTime` bigint(20) NOT NULL,
  `updatedTime` bigint(20) NOT NULL,
  UNIQUE KEY `userId_type` (`userId`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `poll_poll`;
CREATE TABLE `poll_poll` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `status` smallint(6) NOT NULL,
  `desc` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `poll_pollquestion`;
CREATE TABLE `poll_pollquestion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` longtext NOT NULL,
  `poll_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `poll_pollquestion_poll_id_68d7fcdc_fk_poll_poll_id` (`poll_id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `poll_pollquestionanswer`;
CREATE TABLE `poll_pollquestionanswer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `answer` smallint(6) NOT NULL,
  `poll_question_id` int(11) NOT NULL,
  `vote_sheet_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `poll_pollquest_poll_question_id_d920b180_fk_poll_pollquestion_id` (`poll_question_id`),
  KEY `poll_pollquestionanswer_57677f51` (`vote_sheet_id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `poll_votesheet`;
CREATE TABLE `poll_votesheet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_time` date NOT NULL,
  `vote_time` date DEFAULT NULL,
  `voted` tinyint(1) NOT NULL,
  `poll_id` int(11) NOT NULL,
  `resident_id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `poll_votesheet_poll_id_0cb18675_fk_poll_poll_id` (`poll_id`),
  KEY `poll_votesheet_resident_id_59ace1e7_fk_crm_resident_id` (`resident_id`),
  KEY `poll_votesheet_room_id_5a900e93_fk_crm_room_id` (`room_id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `sensor_datapoint`;
CREATE TABLE `sensor_datapoint` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` double NOT NULL,
  `type` varchar(100) NOT NULL,
  `time` datetime NOT NULL,
  `statisticsType` varchar(100) NOT NULL DEFAULT 'MOMENTARY',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_time_statisticsType` (`type`,`time`,`statisticsType`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `sensor_notification`;
CREATE TABLE `sensor_notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) NOT NULL,
  `flag` tinyint(4) NOT NULL,
  `createdTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `spring_session`;
CREATE TABLE `spring_session` (
  `SESSION_ID` char(36) NOT NULL DEFAULT '',
  `CREATION_TIME` bigint(20) NOT NULL,
  `LAST_ACCESS_TIME` bigint(20) NOT NULL,
  `MAX_INACTIVE_INTERVAL` int(11) NOT NULL,
  `PRINCIPAL_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`SESSION_ID`),
  KEY `SPRING_SESSION_IX1` (`LAST_ACCESS_TIME`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `spring_session_attributes`;
CREATE TABLE `spring_session_attributes` (
  `SESSION_ID` char(36) NOT NULL DEFAULT '',
  `ATTRIBUTE_NAME` varchar(100) NOT NULL DEFAULT '',
  `ATTRIBUTE_BYTES` blob,
  PRIMARY KEY (`SESSION_ID`,`ATTRIBUTE_NAME`),
  KEY `SPRING_SESSION_ATTRIBUTES_IX1` (`SESSION_ID`)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `wechat_contact_request`;
CREATE TABLE `wechat_contact_request` (
  `nickname` varchar(255) NOT NULL,
  `createdTime` bigint(20) DEFAULT NULL
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `wechat_wechatuser`;
CREATE TABLE `wechat_wechatuser` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `openId` varchar(255) NOT NULL,
  `access_token` varchar(255) NOT NULL,
  `refresh_token` varchar(255) NOT NULL,
  `expire_at` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `avatarId` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB;


SET FOREIGN_KEY_CHECKS=1;