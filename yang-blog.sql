/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : yang-blog

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 06/11/2024 14:53:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for food_nutrition
-- ----------------------------
DROP TABLE IF EXISTS `food_nutrition`;
CREATE TABLE `food_nutrition`  (
  `f_id` bigint(0) NOT NULL COMMENT '编码',
  `f_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `f_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类别',
  `f_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '食物名称',
  `f_edible_section` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '可食部分',
  `f_energy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '能量',
  `f_carbon_water` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '碳水',
  `f_fat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '脂肪',
  `f_protein` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '蛋白质',
  `f_dietary_fiber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '膳食纤维',
  `f_vitaminA` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维A',
  `f_vitaminC` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维C',
  `f_vitaminD` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维D',
  `f_vitaminE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维E',
  `f_vitaminK` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维K',
  `f_vitaminB1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维B1',
  `f_vitaminB2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维B2',
  `f_vitaminB3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维B3',
  `f_vitaminB6` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维B6',
  `f_vitaminB9` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维B9',
  `f_vitaminB12` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维B12',
  `f_K` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '钾',
  `f_Na` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '钠',
  `f_Ca` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '钙',
  `f_Zn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '锌',
  `f_Se` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '硒',
  `f_Fe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '铁',
  `f_role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作用',
  `f_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `f_update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `f_note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`f_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_nutrition
-- ----------------------------
INSERT INTO `food_nutrition` VALUES (1730278328766, '', '肉类', '米饭', '100', '能量', '碳水', '脂肪', '蛋白质', '膳食纤维', '维A', '1', '2', '3', '4', '1', '2', '维B3', '6', '维B9', '12', '钾', '钠', '钙', '锌', '硒', '铁', '作用方法', NULL, '2024-10-31 08:46:21', '备注');
INSERT INTO `food_nutrition` VALUES (1730280187709, '/static/2024-10-31/49095b8e49df4d27bedd8381cf72bf7d.jpg', '测试111', '测试22', '1', '测试', '测试', '测试', '测试', '彩色', '测试', '2', '3', '4', '1', '1', '2', '3', '4', '5', '1', '1', '2', '4', '3', '4', '1', '测试11', NULL, '2024-10-31 08:44:20', '测试');

-- ----------------------------
-- Table structure for nutrition_body_composition
-- ----------------------------
DROP TABLE IF EXISTS `nutrition_body_composition`;
CREATE TABLE `nutrition_body_composition`  (
  `b_id` bigint(0) NOT NULL COMMENT '编码',
  `b_age` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年龄',
  `b_sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `b_height` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身高',
  `b_weight` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '体重',
  `b_food_intake` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '进食量',
  `b_basal_metabolism` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '基础代谢',
  `b_bmi` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'BMI',
  `b_fat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '体脂',
  `b_visceral_fat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内脏脂肪',
  `b_muscle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '肌肉',
  `b_skeletal_muscle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '骨骼肌',
  `b_bone_mass` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '骨量',
  `b_water_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '水分',
  `b_protein` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '蛋白质',
  `b_fat_free_body_weight` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '去脂体重',
  `b_muscle_mass` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '肌肉量',
  `b_water_content_1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '含水量',
  `b_fat_mass` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '脂肪量',
  `b_subcutaneous_fat_percentage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '皮下脂肪率',
  `b_weight_control_amount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '体重控制量',
  `b_fat_control_amount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '脂肪控制量',
  `b_muscle_control_volume` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '肌肉控制量',
  `b_obesity_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '肥胖等级',
  `b_standard_weight` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标准体重',
  `b_Ideal_weight` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '理想体重',
  `b_physiological_age` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生理年龄',
  `b_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `b_update_time` datetime(0) NULL DEFAULT NULL COMMENT '最后一次修改时间',
  `b_record_time` datetime(0) NULL DEFAULT NULL COMMENT '记录时间',
  `b_note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '建议',
  `u_id` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`b_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of nutrition_body_composition
-- ----------------------------
INSERT INTO `nutrition_body_composition` VALUES (1729692146194, '26', '男', '176', '68', '3', '1461.8', '21.95', '16.6', '6.6', '78.8', '40.5', '3.4', '65.4', '69.9', '56.71', '27.54', '44.47', '11.29', '10.1', '1.5', '0', '3.6', '正常', '68.5', '63.6', NULL, '2024-10-23 22:02:26', '2024-10-23 22:02:26', '2021-10-20 22:01:25', 'ssssSSSS', 1680851452956);
INSERT INTO `nutrition_body_composition` VALUES (1730107549747, '66', '男', '156', '66', '0.33', '1117.6', '27.12', '12', '22222', '33111', '44', '55', '66', '77', '58.08', '29.04', '43.56', '7.92', '88', '-14.4', '1', '2', '偏胖', '48.5', '51.6', NULL, '2024-10-28 17:25:50', '2024-10-28 20:44:07', '2023-10-18 17:25:30', '烦烦烦得到', 1700732037690);
INSERT INTO `nutrition_body_composition` VALUES (1730121326444, '200', '男', '300', '999', '', '10304.4', '111', '100', '22222', '22222', '100', '2222', '100', '2222', '22222', '999', '999', '999', '', '-861', '22222', '22222', '极重度肥胖', '192.5', '138', NULL, '2024-10-28 21:15:26', '2024-10-28 21:15:26', '2024-10-28 21:14:49', '2222', 1700732037690);
INSERT INTO `nutrition_body_composition` VALUES (1730121372932, '55', '男', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '0', '', '', '偏瘦', '', '', NULL, '2024-10-28 21:16:13', '2024-10-28 21:16:13', '2024-10-28 21:16:08', '', 1680851452956);

-- ----------------------------
-- Table structure for nutritional_recipe
-- ----------------------------
DROP TABLE IF EXISTS `nutritional_recipe`;
CREATE TABLE `nutritional_recipe`  (
  `f_id` bigint(0) NOT NULL COMMENT '编码',
  `f_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `f_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜名',
  `f_intro` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `f_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜系',
  `f_batching` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配料',
  `f_seasoning` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '调料',
  `f_made` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '做法',
  `f_tips` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '小贴士',
  `f_create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `f_update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`f_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of nutritional_recipe
-- ----------------------------
INSERT INTO `nutritional_recipe` VALUES (1730862948642, NULL, '土豆烧牛腩', '土豆烧牛腩是一道美味的家常菜，主要食材是牛腩和土豆。', '炖菜', '牛腩 500 克\n土豆 2 个\n洋葱 1 个\n生姜几片\n大蒜几瓣\n干辣椒适量\n香叶、八角适量', '食用油适量\n盐适量\n酱油两勺\n耗油一勺\n料酒适量\n冰糖5颗\n清水适量', '牛腩洗净切块用清水侵泡10分钟，土豆去皮切块备用。\n牛腩冷水下锅，倒入料酒，水开撇去浮沫，然后捞出备用。\n锅中倒入适量的食用油，加热后放入洋葱、生姜、大蒜、干辣椒煸炒出香味。\n加入牛腩煸炒至表面微黄，然后加入酱油、耗油翻炒均匀。\n加入适量的水（最好是开水），加入土豆、冰糖、盐，大火烧开后转小火炖煮。\n炖煮约 1 小时后，待牛腩炖熟，土豆软糯，开大火收汁即可。', '可以根据个人口味调整盐和酱油的量。\n如果用高压锅，上汽之后小火30分钟即可', '2024-11-06 11:15:49', '2024-11-06 13:40:02');
INSERT INTO `nutritional_recipe` VALUES (1730863203331, NULL, '皮蛋豆腐', '皮蛋豆腐是一道简单美味的凉菜，主要材料是皮蛋和嫩豆腐。', '凉菜', '嫩豆腐 1 块\n皮蛋 2 个\n小葱适量\n蒜末\n姜末\n小米辣3个\n细辣椒面', '盐少许\n鸡精少许\n酱油一勺\n耗油一勺\n香油半勺\n辣椒油适量', '嫩豆腐用开水烫一下，捞出沥干水分，切成块状。\n皮蛋去壳，清洗干净后切成小块【可以小煮一下】。\n用大碗将豆腐放中间，周边摆上皮蛋。\n另取一小碗，碗里放酱油、耗油、葱末、姜末、辣椒面、小米辣，将热油浇给~，\n然后再倒香油、鸡精、盐，搅拌均匀，淋于大碗中。\n最后撒上适量的葱花，上菜~', '根据个人口味加入适量的酱油、香油和辣椒油。\n放入冰箱冷藏 30 分钟左右，风味更佳。', '2024-11-06 11:20:03', '2024-11-06 13:53:39');
INSERT INTO `nutritional_recipe` VALUES (1730863584896, NULL, '鸡翅鸡爪煲', '鸡翅鸡爪煲是一道广受欢迎的家常菜，主要材料是鸡翅和鸡爪，通常会加入香料和调味料慢炖至入味', '煲菜', '鸡翅 500 克\n鸡爪 500 克\n洋葱半个\n生姜几片\n香叶两片\n桂皮、花椒少许\n八角两个\n', '生抽两勺\n耗油两勺\n老抽一勺\n甜面酱两勺\n黄豆酱一勺\n鸡精一勺\n胡椒粉一勺\n白砂糖一勺\n盐少许\n清水适量\n', '鸡翅和鸡爪洗净，生姜切片，大蒜拍碎备用。\n鸡翅改刀、鸡爪去掉指甲盖。\n鸡翅、鸡爪冷水下锅，加葱、姜、料酒，水开撇去浮沫，捞出备用。\n锅里热油，放洋葱、一勺红油豆瓣酱、桂皮、八角、香叶、花椒炒香，再倒入鸡翅鸡爪翻炒均匀。\n倒入【调料区的料汁】，翻炒均匀。加入适量水淹没食材。\n大火烧开，转小火炖煮20分钟。\n加入配菜，再炖煮15分钟。\n根据个人口味调整盐的量，可以适当加一些鸡精或味精提鲜。\n最后撒上葱花上菜', '可以放一些自己喜欢的配菜，比如玉米、土豆、各种丸子\n珐琅锅全程不用再加水，如果是其他铁锅、砂锅，中途看情况加水。', '2024-11-06 11:26:25', '2024-11-06 11:50:52');
INSERT INTO `nutritional_recipe` VALUES (1730873906184, '', '清蒸鲈鱼', '清蒸鲈鱼是一道非常受欢迎的粤菜，以其清淡、鲜美而著称', '粤菜', '鲈鱼 1 条（约 500 克）\n姜片适量\n葱丝适量\n红椒丝适量（可选）', '盐适量\n生抽或蒸鱼豉油适量\n食用油适量', '将鲈鱼清洗干净，在鱼身两侧各划几刀以便入味。\n盆里倒入清水【足够淹没鱼】，撒盐、姜片、葱段、料酒，搅拌几下，放入鲈鱼，侵泡15分钟。\n捞出鲈鱼，将盆里的姜片、葱段捞出放于鲈鱼腹部以及花刀处。\n锅里倒入足量清水，水开后放入鲈鱼，大火隔水蒸 8 至 10 分钟（根据鱼的大小调整时间），直到鱼熟透。\n在蒸好的鱼上均匀地倒入适量的生抽或蒸鱼豉油，再鱼身上放上葱丝、红椒丝。\n锅中倒入适量的食用油，加热至油热后，小心地浇在鱼身上，让香味充分释放。\n最后，撒上一一点葱花，上菜~', '懒人速成菜', '2024-11-06 14:18:26', '2024-11-06 14:18:26');
INSERT INTO `nutritional_recipe` VALUES (1730874263750, '', '回锅肉', '回锅肉是四川传统菜肴之一，口味独特、香气四溢，主要材料包括五花肉和蒜苗', '川菜', '五花肉 300 克\n蒜苗 100 克【约两根】\n青红椒 各 1 个\n生姜4片\n大蒜5瓣', '豆瓣酱一勺\n豆豉两勺\n料酒适量\n白糖半勺\n花椒粉少许\n食用油适量', '五花肉洗净，放入锅中加水、料酒、姜片煮至七成熟，捞出切片备用。\n蒜苗、青红椒洗净切段，姜蒜切片备用。\n锅中倒入适量的食用油，加热后放入姜蒜片爆香。\n加入豆瓣酱和豆豉炒出红油。\n放入切好的五花肉片，快速翻炒使肉片均匀裹上豆瓣酱。\n加入青红椒和蒜苗，继续翻炒。\n根据口味加入适量的白糖、花椒粉调味。\n炒至蒜苗断生，肉片出油即可。', '不爱吃肥肉的话，五花肉可以煸干一些。会很香', '2024-11-06 14:24:24', '2024-11-06 14:24:24');
INSERT INTO `nutritional_recipe` VALUES (1730874336842, '', '肉末蒸蛋', '肉末蒸蛋是一道简单美味的家常菜，适合各个年龄段的人食用', '粤菜', '猪肉末 100 克\n鸡蛋 2 个\n葱花适量', '香油适量\n食用油适量\n清水适量\n盐适量\n生抽适量\n白胡椒粉适量', '猪绞肉加入少许盐、生抽、白胡椒粉、葱花和香油，搅拌均匀腌制 10 分钟。\n鸡蛋打入碗中，加入 100 毫升清水，搅拌均匀。\n将蛋液过筛倒入深盘中，去除表面的泡沫。\n将腌好的肉泥铺在蛋液表面。\n放入蒸锅中，中火蒸 10 到 15 分钟，直到肉熟蛋凝固。\n蒸好后取出，表面撒上葱花，淋上适量的生抽和香油。', '小孩特爱吃', '2024-11-06 14:25:37', '2024-11-06 14:33:29');
INSERT INTO `nutritional_recipe` VALUES (1730875014738, '', '爆炒花蛤', '爆炒花蛤是一道美味的海鲜菜肴，口感鲜美，做法简单。', '家常菜-海鲜', '花蛤 500 克\n青红椒各 1 个\n葱 1 根\n姜 2 片\n大蒜几瓣', '豆瓣酱适量\n料酒适量\n生抽适量\n蚝油适量\n糖适量\n食用油适量', '花蛤洗净，用清水浸泡，让其吐净泥沙。\n青红椒去籽切条，葱切段，姜切丝，大蒜切片备用。\n锅中加水，放入花蛤煮至开口，捞出沥干水分。\n锅中倒入适量的食用油，加热后放入姜蒜片爆香。\n加入豆瓣酱炒出红油。\n放入青红椒条翻炒。\n加入料酒、生抽、蚝油和糖调味。\n最后放入花蛤快速翻炒，使花蛤均匀裹上酱汁。\n根据口味调整盐的量，撒上葱花炒匀即可出锅', '通过爆炒的方式快速烹饪花蛤，使其口感鲜嫩，味道丰富。这道菜肴的制作过程中，使用了豆瓣酱、生抽、蚝油等调味料，体现了中国菜肴调味的多样性。由于中国幅员辽阔，各地口味和烹饪技法差异较大，爆炒花蛤这样的菜肴可能融合了不同地区的烹饪特点，因此很难将其归入一个特定的菜系。它适合作为家庭餐桌上的美味佳肴，也常见于餐馆和小吃店', '2024-11-06 14:36:55', '2024-11-06 14:38:03');
INSERT INTO `nutritional_recipe` VALUES (1730875501078, '', '白灼菜心', '白灼菜心是一道简单却美味可口的粤菜，强调保持菜心的原味和营养。', '粤菜', '菜心 500 克\n葱丝少许', '生抽适量\n食用油适量\n盐适量', '菜心洗净，去除老叶和根部，用清水浸泡 10 分钟，然后沥干。\n锅中加水，放入适量的盐和食用油，大火烧开。\n将菜心放入沸水中焯烫 2 分钟左右【菜头先烫40秒】，直到菜心变绿且变软，捞出沥干。\n将焯烫好的菜心摆盘，淋上生抽。放上葱丝。\n最后淋上热油即可。\n', '选择新鲜的菜心是制作美味白灼菜心的关键，新鲜的菜心口感更加脆嫩。\n在焯烫菜心时加入少量的盐和食用油，可以使菜心保持绿色，更加鲜亮。\n焯烫菜心的时间不宜过长，否则菜心会变得过于软烂，失去脆嫩的口感。\n菜心含有丰富的维生素和纤维素，是一道非常健康的蔬菜。', '2024-11-06 14:45:01', '2024-11-06 14:48:07');

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
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `ext01` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '职业',
  `ext02` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1729063205200 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1680851452956, '杨华杰', '8877968d8f84bdb68f4a14f47fb0f583', '杨华杰', '/static/2024-10-30/8dcf5dd2-647f-44e5-8d3d-a98b2c2ec677.png', '1251120808@qq.com', '15723084621', '福建省福州市马尾区魁岐小区B-8栋902', '超级管理员', 'i7h20s', '2023-04-07 15:10:53', '2024-10-16 16:15:38', '超级管理员', 'IT运维', '500230199711294374');
INSERT INTO `sys_user` VALUES (1700732037690, '刘丽章', '75257cfbddff8c1dc73b2a16ee7d26ea', '匿名', '/static/2024-10-30/b343cdac-9c82-44b1-9e6b-e42374da25f3.jpg', 'undefined@qq.com', '15723084621', '福建省福州市马尾区建版新村', '超级管理员', '2nd13j', '2023-11-23 17:33:58', '2024-10-16 16:15:56', '超级管理员', '高级营养师', '500230200010224926');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `userid` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
  `roleid` bigint(0) NULL DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1729063205235 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1680851452976, 1680851452956, 1);
INSERT INTO `sys_user_role` VALUES (1700732037700, 1700732037690, 1);

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
INSERT INTO `t_blog` VALUES (1678863756424, '博客概念', '博客是什么,类型有哪些 1. 个人博客,也就是网络上的个人博客,通常在一个人拥有独立 IP地址的互联网上以电子邮件形式向公众展示自己的生活、工作和爱好,主要面... 2. 商业博客,又称企业博客,是指专门为商业机构或企业提供个性化信息服务并进行网络营销的网站; 行业博客,又称行业信息博客、垂直行业博... 3. 社交网络、博客和 SNS网站都属于社交网络(Social Network)。 SNS网站的最大特色是具有社区性质,用户通过... ', '博客，仅音译，英文名为Blogger，为Web Log的混成词。其正式名称为网络日记；又音译为部落格或部落阁等，是社会媒体网络的一部分。是使用特定的软件，在网络上出版、发表和张贴个人文章的人，或者是一种通常由个人管理、不定期张贴新的文章的网站。博客上的文章通常以网页形式出现，并根据张贴时间，以倒序排列。通常具备RSS订阅功能。\n博客是继MSN、BBS、ICQ之后出现的第4种网络交流方式，现已受到大家的欢迎，是网络时代的个人“读者文摘”，是以超链接为入口的网络日记，它代表着新的生活、工作和学习方式。许多博客专注在特定的课题上提供评论或新闻，其他则被作为个人性的日记。一个典型的博客结合了文字、图像、其他博客或网站的链接及其它与主题相关的媒体，能够让读者以互动的方式留下意见，是许多博客的重要要素。大部分的博客内容以文字为主，但仍有一些博客专注在艺术、摄影、视频、音乐、播客等各种主题。', '/static/2024-01-23/BLOG.jpeg', '转载', b'01', b'01', b'01', NULL, NULL, NULL, 1705980624172, 1680851452956, '[1705980662741, 1705980557620]', b'00', '2023-03-15 15:02:36', '2024-01-23 11:50:26', NULL, NULL);
INSERT INTO `t_blog` VALUES (1680859628760, '八重神子', '鸣神大社的大巫女、狐之血脉的延续者、「永恒」的眷属与友人', '鸣神大社的大巫女、狐之血脉的延续者、「永恒」的眷属与友人…以及，轻小说出版社「八重堂」的恐怖总编。\n\n不必追寻其中任何一面。每一面都是八重神子，每一面却也无法成为真正的她。\n\n各种姿态，都犹如镜子的碎片，映射出截然不同的她。因诸多身份包裹，她亦成为了一块被无数面御镜包围的宝钻。\n\n上百种面相，严肃或快活，悲悯或漠然。\n\n无人知晓真实，就像无人能轻易从秘林中找到一只与过客擦肩的仙狐。\n\n![1666794890983.png](1)\n「今夜的月光如此清亮，不做些什么真是浪费。随我一同去月下漫步吧，不许拒绝。」\n\n细究起来，八重神子的头衔竟与她的性情同样难测多变。\n\n出于各种目的试图搞清神子「真面目」的人，可以从天领奉行府一路排到「八重堂」门口，然而迄今为止，成功者依旧寥寥。\n\n——只需记得，她永远是那位「兼具智慧与美貌的八重神子大人」就好。\n「今夜的月光如此清亮，不做些什么真是浪费。随我一同去月下漫步吧，不许拒绝。」\n\n细究起来，八重神子的头衔竟与她的性情同样难测多变。\n\n出于各种目的试图搞清神子「真面目」的人，可以从天领奉行府一路排到「八重堂」门口，然而迄今为止，成功者依旧寥寥。\n\n——只需记得，她永远是那位「兼具智慧与美貌的八重神子大人」就好。\n「元素战技·野干役咒·杀生樱」\n\n八重神子迅速移动，并留下一株「杀生樱」。杀生樱可以间接性对周围的一名敌人降下落雷，造成雷元素伤害；附近存在其他的杀生樱时，将提升其位阶，提高上述攻击造成的伤害。\n\n杀生樱拥有3次可使用次数。最多同时存在3株杀生樱，杀生樱创造时的初始位阶为壹阶，阶级初始至多提升至叁阶。若创造的位置与已有的杀生樱距离过近，则会摧毁已有的杀生樱。\n「元素爆发·大密法·天狐显真」\n\n八重神子降下雷电，造成雷元素范围伤害。\n\n施放时，八重神子会解放附近的杀生樱，摧毁它们的形姿，化作天狐霆雷降下，造成雷元素范围伤害。每通过这种方式摧毁一株杀生樱，就能降下一道天狐霆雷。\n无人知晓真实，就像无人能轻易从秘林中找到一只与过客擦肩的仙狐。\n\n可若是狐狸注视着人…狐狸又将往何处去？', '/static/2023-04-07/1666794890983.png', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1680859288073, 1680753534930, '[1680859324288]', b'00', '2023-04-07 17:27:09', '2023-11-28 14:32:46', NULL, NULL);
INSERT INTO `t_blog` VALUES (1680860116487, '纳西妲', '纳西妲，本名布耶尔，“尘世七执政”中的草之神，所罗门72柱中第10位，被须弥人给予“小吉祥草王”的爱称  。现今七神中最年轻的一位，自诞生起已五百年   。 纳西妲曾深居净善宫内，向来不受重视，也很少被人提及。她身负重任，哪怕目睹漆黑，经历孤独，也不曾停下脚步', '## 身份背景\n   许久前，草神创造了须弥雨林，又通过教令院将智慧赐予国民。她的美名无处不在，千万个故事，只为传唱她的事迹与美德而问世。在人民眼中，草神的存在更像是一种符号化的象征——因此，他们才能确信神明的庇护自古就存在于这片土地之上。城中至贤对草神崇拜备至，民众也坚定不移地追随其后。而影响诸多的“虚空”系统，则是”小吉祥草王”的耳与目。\n它给予她遍历人们喜怒哀乐的能力，令她听见看见一切，让她理解了赞美之外的声音。见闻越是拓展，她越明白自己必须不断学习。她唯有尽快成长，才能面对来自世界最深处的威胁。无法逃离，那是她无法回避的使命。即便没多少人对现状不满,纳西妲依旧坚定不移。她的顽强来源于信念,她比任何人都明白——在这里，她将是所有人的寄托与依靠\n## 相貌衣着\n纳西妲外表是一位年幼的女孩，有着一双尖耳朵。灰白色的长发扎成侧马尾，发尾有明显的绿色渐变色。纳西妲的虹膜是深绿色，而她的十字形瞳孔是浅绿色，周围有一圈深绿色的纹路。纳西妲身披绿色的披风，以及身穿白色花苞裙，裙表面上有着一条条金色纹路，内侧则是绿色。双腿没有衣物，脚上穿着白色踩脚袜。\n## 性格特点\n在纳西妲的观点中，万事万物都存在着相互联系，一切事物都可以指向“命运”   。纳西妲很喜欢使用比喻，她认为比喻是可以用已知知识来理解未知知识的奇妙工具  。虽然会很多大道理，但对常识却不怎么了解 。\n对比起全知全能的大慈树王，纳西妲自认还远远担不起“智慧之神”的名号，对国家的治理也是教令院更加驾轻就熟，她的存在并没有那么大的意义。所以她在净善宫的每一刻都不停在学习，希望尽快成长为一位合格的神明。虽然一直在观察人们，但对模仿一直很不擅长。', '/static/2023-04-07/1666794830957.png', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1680859288073, 1680851452956, '[1680859324288]', b'00', '2023-04-07 17:35:16', '2023-11-22 14:01:12', NULL, NULL);
INSERT INTO `t_blog` VALUES (1705979964267, 'node.js安装踩坑记录', 'node安装记录', '一、修改全局模块下载路径 （下载 | Node.js 中文网 (nodejs.cn)）\n1.在Node下新建【node_global】及【node_cache】文件夹\n2.cmd输入：npm config set prefix \"D:\\Nodejs\\node_global\"\n  npm config set cache \"D:\\Nodejs\\node_cache\"\n3.将【用户变量】下的【Path】修改为【D:\\Nodejs\\node_global】，在【系统变量】下新建【NODE_PATH】【D:\\Nodejs\\node_global\\node_modules】，在【系统变量】下的【Path】新建添加node全局文件夹【D:\\Nodejs\\node_global】\n4.测试：npm install express -g\n5.若报错，是由于权限的原因，右击Nodejs文件夹->属性->安全，点击编辑，将所有权限都✔即可\n二、更换npm源为淘宝镜像\n1.查看初始npm源：npm config get registry\n2.更换镜像为淘宝镜像：npm config set registry https://registry.npm.taobao.org/\n三、全局安装基于淘宝源的cnpm\n1.全局安装基于淘宝源的cnpm：npm install -g cnpm --registry=https://registry.npm.taobao.org\n2.执行命令查看cnpm是否安装成功：cnpm -v', '/static/2024-01-23/nodes.jpeg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1678852528115, 1680851452956, '[1678852680689]', b'00', '2024-01-23 11:19:24', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1705981493568, 'GIT相关', 'GIT的一些常见问题', '## Q：git ssh 配置以及公钥\n#### 一、查看git配置：git config --list\n#### 二、配置用户和邮箱\ngit config --global user.nme \"用户名\"\ngit config --global user.email \"邮箱\"\n#### 三、生成本地SSH密钥，一直Enter到结束：ssh-keygen -t rsa -C \'邮箱\'\n#### 四、查看密钥：cat ~/.ssh/id_rsa.pub\n#### 五、将密钥添加到github\n## Q：github相关命令\n1.克隆GitHub项目到本地：git clone SSH地址\n2.添加项目文件：git add .\n3.输入提交信息：git commit -m \"提交信息\"\n4.上传：git push -u origin mian\n## Q：github无法上传更新代码\n1.拉取最新更新内容：git pull --rebase origin master\n2.上传：git push origin master', '/static/2024-01-23/GIT.jpg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1678852528115, 1680851452956, '[1705981102799, 1705980662741]', b'00', '2024-01-23 11:44:54', '2024-03-21 09:02:18', NULL, NULL);
INSERT INTO `t_blog` VALUES (1705981803285, '个人账号密码', '杨华杰的各种账号密码记录-备忘', '小米账号:2180219485          密码:yang554330\n秒滴账号:15723084621        密码:yangHUA123#\n\n网易邮箱：yhj1251120808@163.com  yang554330\n\nCSDN账号：15723084621   yanghuajie1251120808\n\nGitHub账号：\nyang554    yang554330\nyang554-gt     yang554330\n\nXIAOI账号：yangSir123		yang123456\n图灵机器人：15723084621	yang123456\n\n淘宝沙箱帐号：\n    商家帐号：nhandc2926@sandbox.com\n    密码：111111\n    \n    买家账号：otmprx4986@sandbox.com\n    密码：111111 \n    \n    PID : 2088122710074521\n    \n    APPID : 2016091300500798\n    \n    支付宝网关 ： https://openapi.alipaydev.com/gateway.do\n云服务器登录账号：root 密码：yanghj123!@#\n\n中国大陆区：steam账户名jmyfl05879密码wzei87295G邮箱801ctldj0@230.wwkesi.club邮箱密码gg1gw40h邮箱登录网址：http://mail.wwkesi.club/\n\n卡号：中国大陆区：steam账户名jmyfl05879密码Yang554330邮箱801ctldj0@230.wwkesi.club邮箱密码Yang554330邮箱登录网址：http://mail.wwkesi.club/', '/static/2024-01-23/user-pad.png', '原创', b'00', b'00', b'01', NULL, NULL, NULL, 1705981684647, 1680851452956, '[1705981691917]', b'00', '2024-01-23 11:50:03', '2024-03-21 16:23:19', NULL, NULL);
INSERT INTO `t_blog` VALUES (1706000333103, 'VUE常见概念', 'Vue (发音为 /vjuː/，类似 view) 是一款用于构建用户界面的 JavaScript 框架。它基于标准 HTML、CSS 和 JavaScript 构建，并提供了一套声明式的、组件化的编程模型，帮助你高效地开发用户界面。无论是简单还是复杂的界面，Vue 都可以胜任。', '### 1. 什么是MVVM?\n	在MVVM框架下 视图和模型是不能直接通信 的，只能通过ViewModel进行交互，它能够监\n	听到数据的变化，然后通知视图进行自动更新，而当用户操作视图时，VM也能监听到视图\n	的变化，然后通知数据做相应改动，这实际上就实现了数据的 双向绑定 。并且V和VM可以进行通信。\n	Model（模型）\n	    模型是指代表真实状态内容的领域模型（面向对象），或指代表内容的数据访问层（以数据为中心）。\n	View（视图）\n	    就像在MVC和MVP模式中一样，视图是用户在屏幕上看到的结构、布局和外观（UI）。\n	ViewModel（视图模型）\n	    视图模型是暴露公共属性和命令的视图的抽象。MVVM没有MVC模式的控制器，也没有MVP模式的\n	presenter,有的是一个绑定器。在视图模型中，绑定器在视图和数据绑定器之间进行通信。\n	优点:\n	低耦合 :View可以独立于Model变化和修改,一个ViewModel可以绑定到不同的View上,当View变化\n	的时候Model可以不变,当Model变化的时候View也可以不变。\n	可重用性 : 可以把一些视图逻辑放在一个ViewModel里面,让很多View重用这段视图逻辑。\n	独立开发 : 开发人员可以专注于业务逻辑和数据的开发,设计人员可以专注于页面的设计。\n### 2. 什么是MVC?\n	MVC是应用最广泛的软件架构之一,一般MVC分为:Model(模型),View(视图),Controller(控制器)。 \n	这主要是基于分层的目的,让彼此的职责分开.View一般用过Controller来和Model进行联系。			 \n	Controller是Model和View的协调者,View和Model不直接联系。基本都是单向联系。M和V指的意思和\n	MVVM中的M和V意思一样。C即Controller指的是页面业务逻辑。MVC是单向通信。也就是View跟Model，\n	必须通过Controller来承上启下。\n	Model（模型）表示应用程序核心（如数据库）。\n	View（视图）显示效果（HTML页面）。\n	Controller（控制器）处理输入（业务逻辑）。\n	MVC 模式同时提供了对 HTML、CSS 和 JavaScript 的完全控制。\n	Model（模型）是应用程序中用于处理应用程序数据逻辑的部分。 　　\n	通常模型对象负责在数据库中存取数据。\n	View（视图）是应用程序中处理数据显示的部分。 　\n	通常视图是依据模型数据创建的。\n	Controller（控制器）是应用程序中处理用户交互的部分。 　　\n	通常控制器负责从视图读取数据，控制用户输入，并向模型发送数据。\n	优点:\n	    低耦合\n	    重用性高\n	    生命周期成本低\n	    部署快\n	    可维护性高\n	    有利软件工程化管理', '/static/2024-01-23/vue.jpeg', '转载', b'01', b'01', b'01', NULL, NULL, NULL, 1678852528115, 1680851452956, '[1678852680689]', b'00', '2024-01-23 16:58:53', '2024-02-18 10:55:54', NULL, NULL);
INSERT INTO `t_blog` VALUES (1708224918901, '前端理论题', '集合前端技术栈常见题型', '### Q：ES5和ES6继承有什么区别\nA：ES5是通过构造函数实现，ES6是通过extend继承。', '/static/2024-02-18/前端png.jpg', '原创', b'01', b'01', b'00', NULL, NULL, NULL, 1678852528115, 1700732037690, '[1678852680689]', b'00', '2024-02-18 10:55:19', '2024-02-18 13:47:28', NULL, NULL);
INSERT INTO `t_blog` VALUES (1708414578236, 'vue-cli安装失败', 'npm的证书失效了，它不再支持自签名证书 ', '### 一、报错情况\n```language\nnpm ERR! code CERT_HAS_EXPIRED\nnpm ERR! errno CERT_HAS_EXPIRED\nnpm ERR! request to https://registry.npm.taobao.org/vue-cli failed, reason: certificate has expired\n```\n### 二、解决方案\n1. 查看自己的下载源：npm config get registry，\n   发现我们之前配置了下载镜像源为https开头的：https://registry.npm.taobao.org/\n2. 执行命令清除npm缓存：npm cache clean --force\n3. 执行命令取消ssl验证：npm config set strict-ssl false\n4. 再次执行npm install ***，还不行可以尝试设置你的npm镜像源为http开头的，不使用https：npm config set registry http://registry.npm.taobao.org\n', '/static/2024-10-30/ba402601-d444-44b6-b32a-fcc4366a1e7c.png', '原创', b'01', b'01', b'01', '99999', '999', '66', 1678852528115, 1700732037690, '[1678852680689]', b'00', '2024-02-20 15:36:18', '2024-02-20 15:37:54', NULL, NULL);
INSERT INTO `t_blog` VALUES (1730080351642, '咸鱼之王', '满级梦魇水晶属性', '‌攻击水晶‌：攻击+11200，攻击+31%\n‌精准水晶‌：攻击+6600，精准+21%，攻击+18%\n‌破甲水晶‌：攻击+6600，破甲+30%，攻击+18%\n‌暴击水晶‌：暴击+15%，爆伤+54%，攻击+4%\n‌技能伤害水晶‌：精准+23%，技能伤害+50%\n‌减伤水晶‌：攻击+6600，减伤+18%，血量+22%\n‌生命水晶‌：血量+12.8万，血量+37%\n‌格挡水晶‌：格挡+18%，血量+24%\n‌生命暴击水晶‌：暴击+15%，血量+24%\n‌速度水晶‌：血量+23%，速度+200\n‌免控水晶‌：血量+12.8万，血量+33%，免控+12%‌12', '/static/2024-10-28/5a5feb89-4627-4232-81a7-41eb93a8de17.png', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1680859288073, 1680851452956, '[1705981691917]', b'00', '2024-10-28 09:52:32', '2024-10-28 10:05:35', NULL, NULL);

-- ----------------------------
-- Table structure for t_remark
-- ----------------------------
DROP TABLE IF EXISTS `t_remark`;
CREATE TABLE `t_remark`  (
  `id` bigint(0) NOT NULL COMMENT '评论ID',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '评论人ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容',
  `blog_id` bigint(0) NULL DEFAULT NULL COMMENT '博客ID',
  `parent_content_id` bigint(0) NULL DEFAULT NULL COMMENT '父评论ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `root_parent` bigint(0) NULL DEFAULT NULL COMMENT '顶级评论',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
INSERT INTO `t_tag` VALUES (1678852629023, 'Java', NULL);
INSERT INTO `t_tag` VALUES (1678852635754, 'C++', NULL);
INSERT INTO `t_tag` VALUES (1678852644907, 'Python', NULL);
INSERT INTO `t_tag` VALUES (1678852650732, 'PHP', NULL);
INSERT INTO `t_tag` VALUES (1678852655881, '.NET', NULL);
INSERT INTO `t_tag` VALUES (1678852675943, '后端开发', NULL);
INSERT INTO `t_tag` VALUES (1678852680689, '前端开发', 'danger');
INSERT INTO `t_tag` VALUES (1678852708414, 'Android', NULL);
INSERT INTO `t_tag` VALUES (1678852715454, 'ios', NULL);
INSERT INTO `t_tag` VALUES (1678852722320, 'U3D', NULL);
INSERT INTO `t_tag` VALUES (1678852742171, '软件测试', NULL);
INSERT INTO `t_tag` VALUES (1680859324288, '原神', NULL);
INSERT INTO `t_tag` VALUES (1705980557620, '网络词', NULL);
INSERT INTO `t_tag` VALUES (1705980662741, '学术词', NULL);
INSERT INTO `t_tag` VALUES (1705981102799, 'git', NULL);
INSERT INTO `t_tag` VALUES (1705981691917, '其他', NULL);

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
INSERT INTO `t_type` VALUES (1678852275997, '计算机', NULL);
INSERT INTO `t_type` VALUES (1678852429243, '营养科学', NULL);
INSERT INTO `t_type` VALUES (1678852528115, 'IT互联网技术', NULL);
INSERT INTO `t_type` VALUES (1678852545202, '电子/通信', NULL);
INSERT INTO `t_type` VALUES (1678852556591, '运营/客服', NULL);
INSERT INTO `t_type` VALUES (1678852569652, '生产制造', NULL);
INSERT INTO `t_type` VALUES (1678852574553, '餐饮', NULL);
INSERT INTO `t_type` VALUES (1678852586349, '设计', NULL);
INSERT INTO `t_type` VALUES (1678852601598, '供应链/物流', NULL);
INSERT INTO `t_type` VALUES (1678852612777, '医疗健康', NULL);
INSERT INTO `t_type` VALUES (1680859288073, '游戏', NULL);
INSERT INTO `t_type` VALUES (1681893851454, '动漫', NULL);
INSERT INTO `t_type` VALUES (1705980624172, '概念神', '各种词汇的概念');
INSERT INTO `t_type` VALUES (1705981684647, '其他', NULL);

-- ----------------------------
-- Table structure for u_aunt
-- ----------------------------
DROP TABLE IF EXISTS `u_aunt`;
CREATE TABLE `u_aunt`  (
  `id` bigint(0) NOT NULL,
  `uID` bigint(0) NULL DEFAULT NULL COMMENT '用户',
  `startDate` date NULL DEFAULT NULL,
  `endDate` date NULL DEFAULT NULL,
  `cycle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '周期',
  `duration` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '经期持续时间',
  `bloodVolume` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出血量',
  `symptom` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '症状',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `uStatus` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态（0为历史记录，1为当前记录，2为系统推测记录）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of u_aunt
-- ----------------------------
INSERT INTO `u_aunt` VALUES (1700732181451, 1700732037690, '2023-04-25', '2023-04-29', '29天', '4天', '24', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732472483, 1700732037690, '2023-05-25', '2023-05-29', '28天', '4天', '44', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732530768, 1700732037690, '2023-06-22', '2023-06-26', '28天', '4天', '28', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732555723, 1700732037690, '2023-07-20', '2023-07-24', '28天', '4天', '35', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732585816, 1700732037690, '2023-08-16', '2023-08-20', '28天', '4天', '45', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732644765, 1700732037690, '2023-09-13', '2023-09-17', '28天', '4天', '56', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732675946, 1700732037690, '2023-10-12', '2023-10-16', '28天', '4天', '34', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732732619, 1700732037690, '2023-11-09', '2023-11-14', '28天', '5天', '36', '', '', '0');
INSERT INTO `u_aunt` VALUES (1702257418452, 1700732037690, '2023-12-08', '2023-12-13', '28天', '5天', '34', '疲惫', '13号早上走的', '0');
INSERT INTO `u_aunt` VALUES (1704683011725, 1700732037690, '2024-01-07', '2024-01-13', '29天', '6天', '86', '无', '7号下午来的，13号中午走的', '0');
INSERT INTO `u_aunt` VALUES (1707271345913, 1700732037690, '2024-02-05', '2024-02-10', '28天', '5天', '36', '', '5号傍晚来的，10号下午走的', '0');
INSERT INTO `u_aunt` VALUES (1709600276025, 1700732037690, '2024-03-05', '2024-03-11', '28天', '6天', '48', '吃了点辣椒，着了点凉', '5号6点20来的\n11号早上走', '0');
INSERT INTO `u_aunt` VALUES (1712483742886, 1700732037690, '2024-04-04', '2024-04-09', '29天', '5天', '23', '无', '4号早上来的\n9号晚上', '0');
INSERT INTO `u_aunt` VALUES (1714911979451, 1700732037690, '2024-05-01', '2024-05-06', '26天', '5天', '55', '无症状', '1号下午\n6号下午', '0');
INSERT INTO `u_aunt` VALUES (1718534655141, 1700732037690, '2024-05-30', '2024-06-04', '28天', '5天', '37', '这次最痛', '30号下午来，4号中午走', '0');
INSERT INTO `u_aunt` VALUES (1719632696820, 1700732037690, '2024-06-26', '2024-07-02', '26天', '6天', '40', '', '26晚上8点来的\n2号走', '0');
INSERT INTO `u_aunt` VALUES (1721885792536, 1700732037690, '2024-07-25', '2024-07-31', '29天', '6天', '53', '痛', '25号上午10点来的\n31号晚上结束', '0');
INSERT INTO `u_aunt` VALUES (1725095578017, 1700732037690, '2024-08-25', '2024-08-30', '30天', '5天', '45', '非常痛', '25下午4点\n30晚上，31已经没有了', '0');
INSERT INTO `u_aunt` VALUES (1727077406726, 1700732037690, '2024-09-22', '2024-09-27', '27天', '5天', '66', '', '22号中午来的\n27号下午走的', '0');
INSERT INTO `u_aunt` VALUES (1729130408781, 1680851452956, '2024-07-01', '2024-07-06', '28天', '5天', '23', '', '', '0');
INSERT INTO `u_aunt` VALUES (1729130463619, 1680851452956, '2024-07-29', '2024-08-03', '28天', '5天', '36', '', '', '0');
INSERT INTO `u_aunt` VALUES (1729130493865, 1680851452956, '2024-08-26', '2024-08-31', '28天', '5天', '36', '', '', '0');
INSERT INTO `u_aunt` VALUES (1729130518268, 1680851452956, '2024-09-23', '2024-09-28', '28天', '5天', '65', '', '', '0');
INSERT INTO `u_aunt` VALUES (1729130545236, 1680851452956, '2024-10-21', NULL, '27天', '', '', '', '21', '1');
INSERT INTO `u_aunt` VALUES (1729567026181, 1700732037690, '2024-10-21', '2024-10-26', '28天', '5天', '55', '正常', '21号中午来的\n26号下午没的', '0');

-- ----------------------------
-- Table structure for u_even
-- ----------------------------
DROP TABLE IF EXISTS `u_even`;
CREATE TABLE `u_even`  (
  `e_id` bigint(0) NOT NULL,
  `e_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '事件标题',
  `e_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容',
  `e_startDate` datetime(0) NULL DEFAULT NULL COMMENT '事件开始时间',
  `e_endDate` datetime(0) NULL DEFAULT NULL COMMENT '时间结束时间',
  `e_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '事件当事人',
  `e_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '事件发生地',
  `e_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '事件类型（0：普通，1：生日，2：纪念日，3：倒数日，4：其他）',
  `isNotice` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否发送短信通知（0：否，1：是）',
  `e_createDate` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `e_createNameID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `e_note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`e_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of u_even
-- ----------------------------
INSERT INTO `u_even` VALUES (1705915262834, '开始-出发-福州-昆明-斗南花市', '福州-昆明', '2023-08-17 08:00:00', '2023-08-17 23:00:00', '杨华杰', '云南昆明斗南花市', '普通', '0', '2024-01-22 17:19:23', '1680851452956', '');
INSERT INTO `u_even` VALUES (1705915461227, '游玩-昆明-红树林', '红树林-划船-漫步', '2023-08-18 08:00:00', '2023-08-18 23:00:00', '杨华杰', '云南昆明', '普通', '0', '2024-01-22 17:23:15', '1680851452956', '');
INSERT INTO `u_even` VALUES (1705915588751, '游玩-昆明-大理-洱海-古城', '洱海骑行-大理古城', '2023-08-20 08:00:00', '2023-08-20 23:00:00', '杨华杰', '云南大理', '普通', '0', '2024-01-22 17:24:55', '1680851452956', '');
INSERT INTO `u_even` VALUES (1705916105077, '回程-大理-菌菌火锅-回昆明', '大理-吃菌菌火锅-回昆明', '2023-08-20 08:00:00', '2023-08-20 23:00:00', '杨华杰', '云南', '普通', '0', '2024-01-22 17:32:56', '1680851452956', '');
INSERT INTO `u_even` VALUES (1705916348945, '结束-回程-昆明-重庆-福州', '昆明出发重庆，中转飞机福州', '2023-08-21 08:00:00', '2023-08-21 23:00:00', '杨华杰', '福州', '普通', '0', '2024-01-22 17:37:27', '1680851452956', '');
INSERT INTO `u_even` VALUES (1706667721552, '国庆-F-C', '放假啦', '2023-09-30 23:00:00', '2023-10-07 08:00:00', '杨华杰', '福州-重庆', '普通', '0', '2024-01-31 10:19:15', '1680851452956', '');
INSERT INTO `u_even` VALUES (1706667863827, '检查-C-F', '突击检查，好突然，措手不及', '2023-11-03 22:00:00', '2023-11-09 08:00:00', '柳莉莉', '重庆-福州', '普通', '0', '2024-01-31 10:22:36', '1680851452956', '');
INSERT INTO `u_even` VALUES (1706668387605, '游玩-福道', '柳莉莉&杨华杰，福道溜达，11点半出发，先去派出所处理身份证过期事情，后坐车直达福道', '2023-11-08 08:00:00', '2023-11-08 23:00:00', '杨华杰', '福州', '普通', '0', '2024-01-31 10:26:31', '1680851452956', '身份证处理失败，需要居住证，而居住证过期未办理，等后续再说了');
INSERT INTO `u_even` VALUES (1706668632015, '元旦-湖北浠水-重庆', '放元旦啦', '2023-12-29 22:00:00', '2024-01-09 08:00:00', '杨华杰', '重庆', '普通', '0', '2024-01-31 10:35:50', '1680851452956', '');
INSERT INTO `u_even` VALUES (1706669050026, '参加桂稀婚礼', '参加婚礼，充当伴郎，计划2号一早从重庆到丰都，2号晚上所有小伙伴集结，3号一早接亲', '2024-01-02 08:00:00', '2024-01-03 23:00:00', '杨华杰', '重庆市丰都县天水村', '普通', '1', '2024-01-31 10:38:32', '1680851452956', '');
INSERT INTO `u_even` VALUES (1706669467540, '压力山大-F-C', '5号被放小鸽子，记小本本，6号终于见到啦，开心', '2024-01-05 22:00:00', '2024-01-07 21:00:00', '杨华杰', '福州-重庆', '普通', '0', '2024-01-31 10:48:15', '1680851452956', '');
INSERT INTO `u_even` VALUES (1708247651534, '过年啦', '过年啦过年啦', '2024-02-08 08:00:00', '2024-02-17 08:00:00', '柳莉莉', '福州', '普通', '0', '2024-02-18 17:13:31', '1700732037690', '');
INSERT INTO `u_even` VALUES (1709882666204, '来咯来咯FC', '巴啦啦小能量，呜呼拉呼', '2024-03-02 20:00:00', '2024-03-04 20:20:00', '柳莉莉', '福州', '普通', '0', '2024-03-08 15:18:10', '1700732037690', '');
INSERT INTO `u_even` VALUES (1710744381036, '南京周末聚', '15号请假两小时，从福州南出发，到达南京南，然后南京站汇合，再根据第二天出行决定住宿问题', '2024-03-15 16:00:00', '2024-03-17 16:00:00', '杨华杰', '福州出发，目的南京南', '普通', '1', '2024-03-18 14:41:07', '1680851452956', '本次出行\n路费：930.5\n吃喝：505.3\n玩：130\n总计：1565.8');

-- ----------------------------
-- Table structure for u_source
-- ----------------------------
DROP TABLE IF EXISTS `u_source`;
CREATE TABLE `u_source`  (
  `S_ID` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `S_NAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `S_PIN_YIN` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名拼音',
  `S_BIRTHDAY` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生日',
  `S_SEX` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `S_ADDRESS_NEW` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '现住地址',
  `S_ADDRESS_OLD` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '户口地址',
  `S_PHONE` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
  `S_EMIL` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `S_LOVE` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '第几代',
  `S_FID` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父ID',
  `S_MID` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '母ID',
  `S_IMG_URL` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `S_IS_SURVIVAL` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '是否存活（‘1’为存活，‘0’为死亡）',
  `S_DIE_DAY` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '死亡日期',
  `S_MATE_ID` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配偶ID',
  `S_NOTE` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '座右铭',
  PRIMARY KEY (`S_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of u_source
-- ----------------------------
INSERT INTO `u_source` VALUES ('00zz86t0jj', '杨淑会', 'yangshuhui', '1973-09-17', '女', '福建省福州市马尾区', '重庆市丰都县龙孔镇', '', '', '3', '7hdj73e2m3', 'a05zuimvb1', NULL, '1', '', '35ke236146', '');
INSERT INTO `u_source` VALUES ('14y30y4v6w', '冉仁杰', 'ranrenjie', '1992-05-21', '男', '重庆市长寿县', '重庆市丰都县高家镇', '', '', '4', NULL, NULL, NULL, '1', '', 'b3v3p0tisd', '');
INSERT INTO `u_source` VALUES ('35ke236146', '刘长明', 'liuzhangming', '1967-02-21', '男', '重庆市丰都县高家镇', '', '', '', '3', NULL, NULL, NULL, '1', '', '00zz86t0jj', '');
INSERT INTO `u_source` VALUES ('3aii36iyaw', '秦淑英', 'qinshuying', '1964-08-28', '女', '重庆市丰都县高家镇', '重庆市丰都县龙孔镇', '', '', '3', NULL, NULL, NULL, '1', '', 'ko6n6notqf', '');
INSERT INTO `u_source` VALUES ('3rbgzsk4vd', '冉小希', 'ranxiaoxi', '2018-07-29', '女', '重庆市垫江县', '', '', '', '5', '14y30y4v6w', 'b3v3p0tisd', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('530j02ggwt', '杨本山', 'yangbenshan', '1963-04-20', '男', '重庆市丰都县龙孔镇', '重庆市丰都县龙孔镇', '', '', '3', '7hdj73e2m3', 'a05zuimvb1', '/static/2024-10-30/54c96569-f2fb-4cf2-98a6-b5e0b08560a1.png', '0', '2005-04-04', 'zhvoh3r230', '');
INSERT INTO `u_source` VALUES ('5t27ia95vvy', '？', '？', '2022-03-27', '女', '', '', '', '', '2', NULL, NULL, NULL, '0', '2022-03-27', 'd4cemr6j9n', '');
INSERT INTO `u_source` VALUES ('74vcjuwhec', '杨涛睿', 'yangtaorui', '2014-04-25', '男', '重庆市云阳县', '', '', '', '5', 'qg0t2x3kvn', '93juwbxv64', '/static/2023-04-07/原神.jpg', '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('7hdj73e2m3', '杨仁清', 'yangrenqing', '1939-04-25', '男', '重庆市丰都县龙孔镇', '重庆市丰都县龙孔镇', '15023938647', '', '2', 'd4cemr6j9n', '5t27ia95vvy', '/static/2024-10-30/d72f4e5c-b38a-44f9-904b-69e6cf2d5c8f.png', '1', '', 'a05zuimvb1', '︿(￣︶￣)︿');
INSERT INTO `u_source` VALUES ('93juwbxv64', '姚兴红', 'yaoxinghong', '1988-12-24', '女', '重庆市云阳县', '', '', '', '4', NULL, NULL, NULL, '1', '', 'qg0t2x3kvn', '');
INSERT INTO `u_source` VALUES ('94ou95a7iq', '杨紫明', 'yangziming', '2022-03-27', '男', '', '', '', '', '4', 'yuwxtp3o56', 'skmuhxp59w', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('a05zuimvb1', '秦光芳', 'qinguangfang', '1941-03-23', '女', '重庆市丰都县龙孔镇', '重庆市丰都县龙孔镇', '15723084621', '', '2', NULL, NULL, '/static/2024-10-30/d16dbb41-b18d-43e6-8006-8f3b5b2192ef.png', '1', '', '7hdj73e2m3', '零零');
INSERT INTO `u_source` VALUES ('ar7qcxejqo', '谭志宏', 'tanzhihong', '2006-07-01', '男', '重庆市丰都县高家镇', '', '', '', '4', 'ym7pqv3fo4', 'v1x6hbpn0k', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('b3v3p0tisd', '谭琪', 'tanqi', '1991-12-27', '女', '重庆市长寿县', '', '', '', '4', 'ym7pqv3fo4', 'v1x6hbpn0k', NULL, '1', '', '14y30y4v6w', '');
INSERT INTO `u_source` VALUES ('b3yifraw1v', '杨歆禾', 'yangxinhe', '2021-04-16', '男', '', '', '', '', '5', 'ccz0ganf1z', 'xy1b43fgds', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('ccz0ganf1z', '杨紫维', 'yangziwei', '1989-05-23', '男', '', '', '', '', '4', 'ko6n6notqf', '3aii36iyaw', NULL, '1', '', 'xy1b43fgds', '');
INSERT INTO `u_source` VALUES ('d4cemr6j9n', '杨文进', 'yangwenjin', '2022-03-27', '男', '', '', '', '', '1', NULL, NULL, NULL, '0', '2022-03-27', '5t27ia95vvy', '');
INSERT INTO `u_source` VALUES ('d9167tv1w6', '刘丽章', 'liulizhang', '2000-09-25', '女', '北京市', '', '', '', '4', '35ke236146', '00zz86t0jj', NULL, '1', '', '', '');
INSERT INTO `u_source` VALUES ('dv3sndwq44', '谭香敏', 'tanxiangmin', '1997-09-21', '女', '重庆市丰都县高家镇', '', '', '', '4', 'ym7pqv3fo4', 'v1x6hbpn0k', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('e468bpxsvy', '杨华杰', 'yanghuajie', '1997-10-29', '男', '福建福州马尾区', '重庆重庆市丰都', '15723084621', '12@qq.com', '4', 'oi1frjtngz', 'wsjtvd8b4f', NULL, '1', '', '', '测试座右铭法法师范德萨发顺丰');
INSERT INTO `u_source` VALUES ('gvoqe4wg6e', '杨淑珍', 'yangshuzhen', '2022-03-27', '女', '', '', '', '', '3', 'u0gcc8un61', 'mb996bx7jy', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('ko6n6notqf', '杨本林', 'yangbenlin', '1964-12-24', '男', '重庆市丰都县高家镇', '重庆市丰都县高家镇', '', '', '3', '7hdj73e2m3', 'a05zuimvb1', NULL, '1', '', '3aii36iyaw', '');
INSERT INTO `u_source` VALUES ('mb996bx7jy', '秦大莲', 'qindalian', '2022-03-27', '女', '', '', '', '', '1', NULL, NULL, NULL, '0', '2022-03-27', 'u0gcc8un61', '');
INSERT INTO `u_source` VALUES ('mmcei69soi', '杨紫东', 'yangzidong', '2001-02-15', '男', '', '', '', '', '4', 'ko6n6notqf', '3aii36iyaw', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('mz1182s8bg', '刘润康', 'liurunkang', '2007-03-29', '男', '重庆市丰都县高家镇', '', '', '', '4', '35ke236146', '00zz86t0jj', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('oi1frjtngz', '杨本贵', 'yangbengui', '1971-10-03', '男', '重庆市丰都县龙孔镇', '重庆市丰都县龙孔镇', '', '', '3', '7hdj73e2m3', 'a05zuimvb1', NULL, '0', '2003-04-04', 'wsjtvd8b4f', '');
INSERT INTO `u_source` VALUES ('qg0t2x3kvn', '杨志华', 'yangzhihua', '1987-07-19', '男', '重庆市云阳县', '重庆市丰都县龙孔镇', '', '', '4', '530j02ggwt', 'zhvoh3r230', NULL, '1', '', '93juwbxv64', '');
INSERT INTO `u_source` VALUES ('rjat9enqmi', '杨茂江', 'yangmaojiang', '1999-01-07', '男', '重庆市丰都县高家镇', '重庆市丰都县龙孔镇', '', '', '4', '530j02ggwt', 'zhvoh3r230', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('skmuhxp59w', '姚源菊', 'yaoyuanju', '2022-03-27', '女', '', '', '', '', '3', NULL, NULL, NULL, '1', '', 'yuwxtp3o56', '');
INSERT INTO `u_source` VALUES ('u0gcc8un61', '杨仁海', 'yangrenhai', '2022-03-27', '男', '', '', '', '', '2', 'd4cemr6j9n', '5t27ia95vvy', NULL, '1', '', 'mb996bx7jy', '');
INSERT INTO `u_source` VALUES ('v1x6hbpn0k', '杨淑琼', 'yangshuqiong', '1969-10-10', '女', '重庆市丰都县高家镇', '重庆市丰都县龙孔镇', '', '', '3', '7hdj73e2m3', 'a05zuimvb1', NULL, '1', '', 'ym7pqv3fo4', '');
INSERT INTO `u_source` VALUES ('wsjtvd8b4f', '王道芳', 'wangdaofang', '1969-01-29', '女', '福建省福州市马尾区魁岐小区B8-902', '重庆市石柱县流水村', '', '', '3', NULL, NULL, NULL, '1', '', 'oi1frjtngz', '');
INSERT INTO `u_source` VALUES ('x2qt0j3h0q', '杨利洁', 'yanglijie', '1996-10-06', '男', '福建省福州市马尾区魁岐小区B8-902', '重庆市石柱县流水村', '15960188443', '', '4', 'oi1frjtngz', 'wsjtvd8b4f', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('xy1b43fgds', '何丹', 'hedan', '1994-04-08', '女', '', '', '', '', '4', NULL, NULL, NULL, '1', '', 'ccz0ganf1z', '');
INSERT INTO `u_source` VALUES ('ym7pqv3fo4', '谭正华', 'tanzhenghua', '1967-07-17', '男', '重庆市丰都县高家镇', '', '', '', '3', NULL, NULL, NULL, '1', '', 'v1x6hbpn0k', '');
INSERT INTO `u_source` VALUES ('yrryfvarvz', '杨姚姚', 'yangyaoyao', '2023-01-11', '女', '', '', '', '', '4', 'yuwxtp3o56', 'skmuhxp59w', '/static/2024-02-07/Screenshot_2024-01-07-21-02-37-565_com.miHoYo.Yuanshen.jpg', '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('yuwxtp3o56', '杨本成', 'yangbencheng', '2022-03-27', '男', '', '', '', '', '3', 'u0gcc8un61', 'mb996bx7jy', NULL, '1', '', 'skmuhxp59w', '');
INSERT INTO `u_source` VALUES ('zhvoh3r230', '何素珍', 'hesuzhen', '1967-07-18', '女', '重庆市丰都县高家镇', '重庆市丰都县龙孔镇', '', '', '3', NULL, NULL, NULL, '1', '', '530j02ggwt', '');
INSERT INTO `u_source` VALUES ('zpipa5qxqg', '杨博尧', 'yangboyao', '2018-08-13', '男', '重庆市云阳县', '', '', '', '5', 'qg0t2x3kvn', '93juwbxv64', NULL, '1', '', '', '');

SET FOREIGN_KEY_CHECKS = 1;
