/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : yang-blog

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 23/02/2023 17:55:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` bigint(0) NOT NULL COMMENT '编码',
  `source_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '源文件',
  `new_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '新文件',
  `suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '后缀名',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路径',
  `size` varchar(0) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '大小',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型 1图片2文档3音频4视频5压缩包6其他',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户编码',
  `down_count` int(0) NULL DEFAULT NULL COMMENT '下载次数',
  `upload_time` datetime(0) NULL DEFAULT NULL COMMENT '上传时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名',
  `name_zh` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '中文名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'admin', '超级管理员');
INSERT INTO `sys_role` VALUES (2, 'user_manager', '用户管理员');
INSERT INTO `sys_role` VALUES (3, 'blog_manager', '博客管理员');
INSERT INTO `sys_role` VALUES (4, 'user', '普通用户');
INSERT INTO `sys_role` VALUES (5, 't', 't');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `description` varchar(512) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '用户描述',
  `salt` varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '密码加',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `etx01` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备用字段',
  `etx02` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备用字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '111111', '测试专员', NULL, 'admin@qq.com', '15723084621', '福建福州', '测试测试', NULL, '2023-02-03 17:58:30', '2023-02-03 17:58:30', NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (2, '测试注册', 'a25a73885befa61fb482e47110e44d1b', '', '', NULL, NULL, NULL, NULL, '2zpu19', '2023-02-08 11:37:29', '2023-02-08 11:37:29', NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (3, '测试注册1', '0cd80225e0158e4f5eb21e24241182da', NULL, NULL, NULL, NULL, NULL, NULL, 'vqtzmi', '2023-02-05 17:21:11', '2023-02-05 17:21:11', NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (4, '测试注册2', '5835c6f388485547552d4111cc506f88', NULL, NULL, NULL, NULL, NULL, NULL, 'ulm7fq', '2023-02-09 16:06:31', '2023-02-09 16:06:31', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `userid` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
  `roleid` bigint(0) NULL DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2, 1);
INSERT INTO `sys_user_role` VALUES (3, 3, 2);

-- ----------------------------
-- Table structure for t_blog
-- ----------------------------
DROP TABLE IF EXISTS `t_blog`;
CREATE TABLE `t_blog`  (
  `id` bigint(0) NOT NULL COMMENT '博客编号',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面',
  `isoriginal` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否原创',
  `ispublic` bit(2) NOT NULL COMMENT '是否公开(1公开)',
  `iscomment` bit(2) NOT NULL COMMENT '是否开启评论(1开启)',
  `issue` bit(2) NOT NULL COMMENT '是否发布(1发布/0草稿)',
  `browsecount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '浏览量',
  `likes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '点赞量',
  `collection` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收藏量',
  `typeid` bigint(0) NULL DEFAULT NULL COMMENT '分类编码',
  `userid` bigint(0) NULL DEFAULT NULL COMMENT '用户编码',
  `tagjson` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签',
  `isdel` bit(2) NULL DEFAULT b'0' COMMENT '是否物理删除(1删除)',
  `createtime` timestamp(0) NOT NULL COMMENT '创建时间',
  `updtime` timestamp(0) NULL DEFAULT NULL COMMENT '更新时间',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预留',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_blog
-- ----------------------------
INSERT INTO `t_blog` VALUES (1677123255566, '法大师傅大师傅', '范德萨发', '的发生法发', '/static/2023-02-23/yl.jpg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1676428762152, 2, '[1676451373157]', b'00', '2023-02-23 11:34:16', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677123502823, '测试封面', '打发十分', '范德萨发顺丰大师傅', '/static/2023-02-23/花木兰刘亦菲手绘版4k电脑壁纸3840x2160_彼岸图网.jpg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1676427833355, 2, '[1676451373157]', b'00', '2023-02-23 11:38:23', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677123621391, '的发生飞洒发', '范德萨发', '的萨芬萨芬', '/static/2023-02-23/lol-辉女郎.jpg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1676428762152, 2, '[1676451373157]', b'00', '2023-02-23 11:40:21', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677123675813, '范德萨发', '发士大夫', '大发萨法沙发', '/static/2023-02-23/lol-阿卡丽.jpg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1676428762152, 2, '[1676451379400, 1676451373157]', b'00', '2023-02-23 11:41:16', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677123715131, '发的撒发射点发', '范德萨发', '方式打发十分', '/static/2023-02-23/诛仙陆雪琪4k电脑壁纸_彼岸图网.jpg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1676427833355, 2, '[1676451373157, 1676451364398]', b'00', '2023-02-23 11:41:55', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677123730748, '范德萨发', '飞洒发', '范德萨发', '/static/2023-02-23/原神.jpg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1676428762152, 2, '[1676451373157]', b'00', '2023-02-23 11:42:11', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677123797869, '范德萨发', '范德萨发', '范德萨发', '/static/2023-02-23/睁一只眼闭一只眼动漫人物女孩尖耳朵 棕黄色头发 蓝色眼睛 蝴蝶 花瓣 感受阳光 4k动漫壁纸_彼岸图网.jpg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1676428762152, 2, '[1676451379400]', b'00', '2023-02-23 11:43:18', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677123821539, '范德萨发撒法', '范德萨发发', '范德萨发', '/static/2023-02-23/原神 刻晴 甘雨 ɡ 裤袜 黑裤袜 4k壁纸_彼岸图网.jpg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1676428762152, 2, '[1676451379400]', b'01', '2023-02-23 11:43:42', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677137612272, '放大沙发沙发', '的萨芬', '范德萨发', '/static/2023-02-23/lol-娑娜.jpg', '原创', b'01', b'01', b'00', NULL, NULL, NULL, 1676427833355, 2, '[1676451373157]', b'00', '2023-02-23 15:33:32', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677137634744, '幅度萨芬萨芬告诉对方', '打发十分', '范德萨发生', '/static/2023-02-23/LOL-kda.jpg', '转载', b'01', b'01', b'01', NULL, NULL, NULL, 1676427833355, 2, '[1676451370677, 1676451359873]', b'00', '2023-02-23 15:33:55', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677137655923, '范德萨发撒法', '的飞洒发地方', '发撒地方大师傅', '/static/2023-02-23/1666794830957.png', '原创', b'00', b'00', b'01', NULL, NULL, NULL, 1676427776409, 2, '[1676451370677]', b'00', '2023-02-23 15:34:16', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1677138437947, '测试测试', '吃的啥啊', '范德萨发', '/static/2023-02-23/1666794890983.png', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1676428762152, 2, '[1676451373157, 1676451370677]', b'00', '2023-02-23 15:47:18', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag`  (
  `id` bigint(0) NOT NULL COMMENT '编码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签名称',
  `color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签颜色',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_tag
-- ----------------------------
INSERT INTO `t_tag` VALUES (1676450088668, 'ffdsa', 'primary');
INSERT INTO `t_tag` VALUES (1676450098886, 'java1', 'warning');
INSERT INTO `t_tag` VALUES (1676450112847, 'fasf', 'primary');
INSERT INTO `t_tag` VALUES (1676450170900, 'afs', 'success');
INSERT INTO `t_tag` VALUES (1676451260267, 'PHP', NULL);
INSERT INTO `t_tag` VALUES (1676451270646, 'java', 'success');
INSERT INTO `t_tag` VALUES (1676451307568, 'f', NULL);
INSERT INTO `t_tag` VALUES (1676451310452, 'fs', NULL);
INSERT INTO `t_tag` VALUES (1676451317190, 'fsfsa', NULL);
INSERT INTO `t_tag` VALUES (1676451322593, 'fsa发生', NULL);
INSERT INTO `t_tag` VALUES (1676451329247, '发生', NULL);
INSERT INTO `t_tag` VALUES (1676451331516, '发生的', NULL);
INSERT INTO `t_tag` VALUES (1676451335201, '发生的发', NULL);
INSERT INTO `t_tag` VALUES (1676451337152, '飞洒', NULL);
INSERT INTO `t_tag` VALUES (1676451339204, '发顺丰', NULL);
INSERT INTO `t_tag` VALUES (1676451340984, '飞洒发', NULL);
INSERT INTO `t_tag` VALUES (1676451346361, '发顺丰发', NULL);
INSERT INTO `t_tag` VALUES (1676451349669, '发士大夫', NULL);
INSERT INTO `t_tag` VALUES (1676451355679, '发顺丰发发', NULL);
INSERT INTO `t_tag` VALUES (1676451357715, '法大师傅', NULL);
INSERT INTO `t_tag` VALUES (1676451359873, '法大使夫妇', NULL);
INSERT INTO `t_tag` VALUES (1676451364398, '发士大夫1', NULL);
INSERT INTO `t_tag` VALUES (1676451370677, '法大师傅飞洒发发撒打发', NULL);
INSERT INTO `t_tag` VALUES (1676451373157, '感到附属国', NULL);
INSERT INTO `t_tag` VALUES (1676451379400, '范德萨干撒', NULL);
INSERT INTO `t_tag` VALUES (1676451382164, '范德萨给', 'info');

-- ----------------------------
-- Table structure for t_type
-- ----------------------------
DROP TABLE IF EXISTS `t_type`;
CREATE TABLE `t_type`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_type
-- ----------------------------
INSERT INTO `t_type` VALUES (1676343937603, 'ces', NULL);
INSERT INTO `t_type` VALUES (1676344004904, 'ceshi', NULL);
INSERT INTO `t_type` VALUES (1676344012134, 'cehsi', NULL);
INSERT INTO `t_type` VALUES (1676344034900, 'bootsrt', NULL);
INSERT INTO `t_type` VALUES (1676344041857, 'fsafsadf', NULL);
INSERT INTO `t_type` VALUES (1676344044954, 'fdsafsaf', NULL);
INSERT INTO `t_type` VALUES (1676344046890, 'fdsafas', NULL);
INSERT INTO `t_type` VALUES (1676344049418, 'fsdafasdf', NULL);
INSERT INTO `t_type` VALUES (1676344054449, 'fdsafasf', NULL);
INSERT INTO `t_type` VALUES (1676344056954, 'fdasfasf', NULL);
INSERT INTO `t_type` VALUES (1676344077821, 'fsa', NULL);
INSERT INTO `t_type` VALUES (1676344183153, 'cds', NULL);
INSERT INTO `t_type` VALUES (1676344194889, '吃撒', NULL);
INSERT INTO `t_type` VALUES (1676344715191, '成都市', NULL);
INSERT INTO `t_type` VALUES (1676344765567, '士大夫', NULL);
INSERT INTO `t_type` VALUES (1676345918275, '擦掉', NULL);
INSERT INTO `t_type` VALUES (1676345950268, '擦速度', NULL);
INSERT INTO `t_type` VALUES (1676346262448, '是否', NULL);
INSERT INTO `t_type` VALUES (1676346477560, '的', NULL);
INSERT INTO `t_type` VALUES (1676346522567, '方式', NULL);
INSERT INTO `t_type` VALUES (1676346529426, '方式发', NULL);
INSERT INTO `t_type` VALUES (1676346854699, '方式1', NULL);
INSERT INTO `t_type` VALUES (1676346860257, '方式12', NULL);
INSERT INTO `t_type` VALUES (1676346872291, '方式123', NULL);
INSERT INTO `t_type` VALUES (1676352916113, '方式4', NULL);
INSERT INTO `t_type` VALUES (1676352919390, '方式4', NULL);
INSERT INTO `t_type` VALUES (1676352922222, '方式4', NULL);
INSERT INTO `t_type` VALUES (1676352925703, '方式4', NULL);
INSERT INTO `t_type` VALUES (1676352981273, '发', NULL);
INSERT INTO `t_type` VALUES (1676353359177, '法国', NULL);
INSERT INTO `t_type` VALUES (1676353368712, '法国', NULL);
INSERT INTO `t_type` VALUES (1676353403676, '是', NULL);
INSERT INTO `t_type` VALUES (1676353407936, '是1', NULL);
INSERT INTO `t_type` VALUES (1676353411416, '是12', NULL);
INSERT INTO `t_type` VALUES (1676353442532, '是123', NULL);
INSERT INTO `t_type` VALUES (1676353453649, '是1231', NULL);
INSERT INTO `t_type` VALUES (1676353588354, '12', '测试百年祭');
INSERT INTO `t_type` VALUES (1676357379778, '1234', '的萨芬fsaf');
INSERT INTO `t_type` VALUES (1676427756988, 'ce', NULL);
INSERT INTO `t_type` VALUES (1676427763626, 'ce123', NULL);
INSERT INTO `t_type` VALUES (1676427776409, 'ce123f1', NULL);
INSERT INTO `t_type` VALUES (1676427833355, 'fsdaf', NULL);
INSERT INTO `t_type` VALUES (1676428762152, 'sf', '测试服');
INSERT INTO `t_type` VALUES (1676429194007, 'sfs', 'fsafd');
INSERT INTO `t_type` VALUES (1676456386463, '汉字', NULL);

SET FOREIGN_KEY_CHECKS = 1;
