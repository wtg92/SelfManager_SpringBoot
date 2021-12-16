/*
 Navicat Premium Data Transfer

 Source Server         : scientific_manager
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : localhost:3306
 Source Schema         : scientific_manager

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 16/12/2021 11:09:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for memo
-- ----------------------------
DROP TABLE IF EXISTS `memo`;
CREATE TABLE `memo`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `hb_version` int(0) NOT NULL,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `owner_id` int(0) UNSIGNED NOT NULL,
  `note` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_memo_user1_idx`(`owner_id`) USING BTREE,
  CONSTRAINT `fk_memo_user1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of memo
-- ----------------------------

-- ----------------------------
-- Table structure for note
-- ----------------------------
DROP TABLE IF EXISTS `note`;
CREATE TABLE `note`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `hb_version` int(0) NOT NULL,
  `note_book_id` int(0) NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `with_todos` tinyint(0) UNSIGNED NOT NULL,
  `setting` int(0) UNSIGNED NULL DEFAULT NULL,
  `name` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `important` tinyint(0) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_note_note_book1_idx`(`note_book_id`) USING BTREE,
  CONSTRAINT `fk_note_note_book1` FOREIGN KEY (`note_book_id`) REFERENCES `note_book` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of note
-- ----------------------------

-- ----------------------------
-- Table structure for note_book
-- ----------------------------
DROP TABLE IF EXISTS `note_book`;
CREATE TABLE `note_book`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `hb_version` int(0) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `closed` tinyint(0) UNSIGNED NOT NULL,
  `owner_id` int(0) UNSIGNED NOT NULL,
  `setting` int(0) UNSIGNED NULL DEFAULT NULL,
  `style` tinyint(0) UNSIGNED NOT NULL,
  `seq_weight` int(0) UNSIGNED NOT NULL,
  `notes_seq` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_note_book_user1_idx`(`owner_id`) USING BTREE,
  CONSTRAINT `fk_note_book_user1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of note_book
-- ----------------------------

-- ----------------------------
-- Table structure for plan
-- ----------------------------
DROP TABLE IF EXISTS `plan`;
CREATE TABLE `plan`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `start_date` datetime(0) NOT NULL,
  `end_date` datetime(0) NOT NULL,
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `owner_id` int(0) UNSIGNED NOT NULL,
  `hb_version` int(0) UNSIGNED NOT NULL,
  `state` tinyint(0) UNSIGNED NOT NULL,
  `setting` int(0) UNSIGNED NULL DEFAULT NULL,
  `seq_weight` int(0) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_plan_user1_idx`(`owner_id`) USING BTREE,
  INDEX `state_idx`(`state`) USING BTREE,
  CONSTRAINT `fk_plan_user1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of plan
-- ----------------------------

-- ----------------------------
-- Table structure for plan_dept
-- ----------------------------
DROP TABLE IF EXISTS `plan_dept`;
CREATE TABLE `plan_dept`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `hb_version` int(0) NOT NULL,
  `owner_id` int(0) UNSIGNED NOT NULL,
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `owner_id_UNIQUE`(`owner_id`) USING BTREE,
  INDEX `fk_plan_dept_user1_idx`(`owner_id`) USING BTREE,
  CONSTRAINT `fk_plan_dept_user1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of plan_dept
-- ----------------------------

-- ----------------------------
-- Table structure for r_group_perm
-- ----------------------------
DROP TABLE IF EXISTS `r_group_perm`;
CREATE TABLE `r_group_perm`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `perm_id` int(0) UNSIGNED NOT NULL,
  `user_group_id` int(0) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_r_group_perm_user_group1_idx`(`user_group_id`) USING BTREE,
  CONSTRAINT `fk_r_group_perm_user_group1` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_group_perm
-- ----------------------------

-- ----------------------------
-- Table structure for r_user_group
-- ----------------------------
DROP TABLE IF EXISTS `r_user_group`;
CREATE TABLE `r_user_group`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(0) UNSIGNED NOT NULL,
  `user_group_id` int(0) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_r_user_group_user_idx`(`user_id`) USING BTREE,
  INDEX `fk_r_user_group_user_group1_idx`(`user_group_id`) USING BTREE,
  CONSTRAINT `fk_r_user_group_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_r_user_group_user_group1` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_user_group
-- ----------------------------

-- ----------------------------
-- Table structure for tool_record
-- ----------------------------
DROP TABLE IF EXISTS `tool_record`;
CREATE TABLE `tool_record`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `hb_version` int(0) NOT NULL,
  `tool` tinyint(0) UNSIGNED NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tool_UNIQUE`(`tool`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tool_record
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NOT NULL DEFAULT '1970-01-01 08:00:00',
  `update_time` datetime(0) NOT NULL DEFAULT '1970-01-01 08:00:00',
  `account` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `email` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `wei_xin_open_id` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gender` tinyint(0) NOT NULL,
  `nick_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `donation_amount` double NULL DEFAULT NULL,
  `setting` int(0) UNSIGNED NULL DEFAULT NULL,
  `motto` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `id_num` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `tel_num` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `hb_version` int(0) UNSIGNED NULL DEFAULT NULL,
  `pwd_salt` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id_UNIQUE`(`id`) USING BTREE,
  UNIQUE INDEX `nick_name_UNIQUE`(`nick_name`) USING BTREE,
  UNIQUE INDEX `account_UNIQUE`(`account`) USING BTREE,
  UNIQUE INDEX `email_UNIQUE`(`email`) USING BTREE,
  UNIQUE INDEX `wei_xin_open_id_UNIQUE`(`wei_xin_open_id`) USING BTREE,
  UNIQUE INDEX `tel_num_UNIQUE`(`tel_num`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '2021-12-16 11:07:46', '2021-12-16 11:07:46', 'admin', '0BD0793851199D6C', NULL, NULL, 0, 'admin', NULL, NULL, NULL, NULL, NULL, NULL, 0, '4AFA11382E36F5B3');

-- ----------------------------
-- Table structure for user_group
-- ----------------------------
DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group`  (
  `id` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `hb_version` int(0) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_group
-- ----------------------------
INSERT INTO `user_group` VALUES (1, '2021-12-16 11:07:46', '2021-12-16 11:07:46', '普通用户', 0);

-- ----------------------------
-- Table structure for work_sheet
-- ----------------------------
DROP TABLE IF EXISTS `work_sheet`;
CREATE TABLE `work_sheet`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `date` datetime(0) NOT NULL,
  `content` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `note` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `plan_id` int(0) NOT NULL,
  `owner_id` int(0) UNSIGNED NOT NULL,
  `hb_version` int(0) UNSIGNED NOT NULL,
  `plan` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `state` tinyint(0) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_work_sheet_plan1_idx`(`plan_id`) USING BTREE,
  INDEX `fk_work_sheet_user1_idx`(`owner_id`) USING BTREE,
  INDEX `date`(`date`) USING BTREE,
  CONSTRAINT `fk_work_sheet_plan1` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_work_sheet_user1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work_sheet
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
