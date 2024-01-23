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

 Date: 23/01/2024 17:27:37
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
  `etx01` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备用字段',
  `etx02` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备用字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1702280669346 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1680851452956, '杨华杰', '8877968d8f84bdb68f4a14f47fb0f583', '杨华杰', '/static/2024-01-23/原神 雷电将军 大腿 星空4k动漫壁纸_彼岸图网.jpg', '1251120808@qq.com', '15723084621', NULL, '超级管理员', 'i7h20s', '2023-04-07 15:10:53', '2024-01-23 11:01:31', NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (1700732037690, '柳莉莉', 'b1813ecdfd874e2c54f54363397b77d7', '匿名', '/static/2023-12-12/lol-辉女郎.jpg', 'undefined@qq.com', '15723084621', NULL, '', 'tswkqn', '2023-11-23 17:33:58', '2024-01-22 17:44:07', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `userid` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
  `roleid` bigint(0) NULL DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1702280669357 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1680833921750, 1680753534930, 4);
INSERT INTO `sys_user_role` VALUES (1680834664851, 1680834664832, 4);
INSERT INTO `sys_user_role` VALUES (1680834712500, 1680834712493, 2);
INSERT INTO `sys_user_role` VALUES (1680851452976, 1680851452956, 1);
INSERT INTO `sys_user_role` VALUES (1680851751611, 1680851751604, 3);
INSERT INTO `sys_user_role` VALUES (1680852122279, 1680852122265, 2);
INSERT INTO `sys_user_role` VALUES (1700043999898, 1700043999892, 4);
INSERT INTO `sys_user_role` VALUES (1700113956371, 1700113956361, 4);
INSERT INTO `sys_user_role` VALUES (1700732037700, 1700732037690, 1);
INSERT INTO `sys_user_role` VALUES (1702272943519, 1702272943511, 4);
INSERT INTO `sys_user_role` VALUES (1702277364303, 1702277364288, 4);
INSERT INTO `sys_user_role` VALUES (1702277533003, 1702277532988, 4);
INSERT INTO `sys_user_role` VALUES (1702277615049, 1702277615042, 1);
INSERT INTO `sys_user_role` VALUES (1702280669356, 1702280669345, 4);

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
INSERT INTO `t_blog` VALUES (1705981493568, 'GIT相关', 'GIT的一些常见问题', '## Q：git ssh 配置以及公钥\n#### 一、查看git配置：git config --list\n#### 二、配置用户和邮箱\ngit config --global user.nme \"用户名\"\ngit config --global user.email \"邮箱\"\n#### 三、生成本地SSH密钥，一直Enter到结束：ssh-keygen -t rsa -C \'邮箱\'\n#### 四、查看密钥：cat ~/.ssh/id_rsa.pub\n#### 五、将密钥添加到github\n## Q：github相关命令\n1.克隆GitHub项目到本地：git clone SSH地址\n2.添加项目文件：git add .\n3.输入提交信息：git commit -m \"提交信息\"\n4.上传：git push -u origin mian', '/static/2024-01-23/GIT.jpg', '原创', b'01', b'01', b'01', NULL, NULL, NULL, 1678852528115, 1680851452956, '[1705981102799, 1705980662741]', b'00', '2024-01-23 11:44:54', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1705981803285, '个人账号密码', '杨华杰的各种账号密码记录-备忘', '小米账号:2180219485          密码:yang554330\n秒滴账号:15723084621        密码:yangHUA123#\n\n网易邮箱：yhj1251120808@163.com  yang554330\n\nCSDN账号：15723084621   yanghuajie1251120808\n\nGitHub账号：\nyang554    yang554330\nyang554-gt     yang554330\n\nXIAOI账号：yangSir123		yang123456\n图灵机器人：15723084621	yang123456\n\n淘宝沙箱帐号：\n    商家帐号：nhandc2926@sandbox.com\n    密码：111111\n    \n    买家账号：otmprx4986@sandbox.com\n    密码：111111 \n    \n    PID : 2088122710074521\n    \n    APPID : 2016091300500798\n    \n    支付宝网关 ： https://openapi.alipaydev.com/gateway.do', '/static/2024-01-23/user-pad.png', '原创', b'00', b'00', b'01', NULL, NULL, NULL, 1705981684647, 1680851452956, '[1705981691917]', b'00', '2024-01-23 11:50:03', NULL, NULL, NULL);
INSERT INTO `t_blog` VALUES (1706000333103, 'VUE面试题', 'Vue (发音为 /vjuː/，类似 view) 是一款用于构建用户界面的 JavaScript 框架。它基于标准 HTML、CSS 和 JavaScript 构建，并提供了一套声明式的、组件化的编程模型，帮助你高效地开发用户界面。无论是简单还是复杂的界面，Vue 都可以胜任。', '### 1. 什么是MVVM?\n	在MVVM框架下 视图和模型是不能直接通信 的，只能通过ViewModel进行交互，它能够监\n	听到数据的变化，然后通知视图进行自动更新，而当用户操作视图时，VM也能监听到视图\n	的变化，然后通知数据做相应改动，这实际上就实现了数据的 双向绑定 。并且V和VM可以进行通信。\n	Model（模型）\n	    模型是指代表真实状态内容的领域模型（面向对象），或指代表内容的数据访问层（以数据为中心）。\n	View（视图）\n	    就像在MVC和MVP模式中一样，视图是用户在屏幕上看到的结构、布局和外观（UI）。\n	ViewModel（视图模型）\n	    视图模型是暴露公共属性和命令的视图的抽象。MVVM没有MVC模式的控制器，也没有MVP模式的\n	presenter,有的是一个绑定器。在视图模型中，绑定器在视图和数据绑定器之间进行通信。\n	优点:\n	低耦合 :View可以独立于Model变化和修改,一个ViewModel可以绑定到不同的View上,当View变化\n	的时候Model可以不变,当Model变化的时候View也可以不变。\n	可重用性 : 可以把一些视图逻辑放在一个ViewModel里面,让很多View重用这段视图逻辑。\n	独立开发 : 开发人员可以专注于业务逻辑和数据的开发,设计人员可以专注于页面的设计。\n### 2. 什么是MVC?\n	MVC是应用最广泛的软件架构之一,一般MVC分为:Model(模型),View(视图),Controller(控制器)。 \n	这主要是基于分层的目的,让彼此的职责分开.View一般用过Controller来和Model进行联系。			 \n	Controller是Model和View的协调者,View和Model不直接联系。基本都是单向联系。M和V指的意思和\n	MVVM中的M和V意思一样。C即Controller指的是页面业务逻辑。MVC是单向通信。也就是View跟Model，\n	必须通过Controller来承上启下。\n	Model（模型）表示应用程序核心（如数据库）。\n	View（视图）显示效果（HTML页面）。\n	Controller（控制器）处理输入（业务逻辑）。\n	MVC 模式同时提供了对 HTML、CSS 和 JavaScript 的完全控制。\n	Model（模型）是应用程序中用于处理应用程序数据逻辑的部分。 　　\n	通常模型对象负责在数据库中存取数据。\n	View（视图）是应用程序中处理数据显示的部分。 　\n	通常视图是依据模型数据创建的。\n	Controller（控制器）是应用程序中处理用户交互的部分。 　　\n	通常控制器负责从视图读取数据，控制用户输入，并向模型发送数据。\n	优点:\n	    低耦合\n	    重用性高\n	    生命周期成本低\n	    部署快\n	    可维护性高\n	    有利软件工程化管理', '/static/2024-01-23/vue.jpeg', '转载', b'01', b'01', b'00', NULL, NULL, NULL, 1678852528115, 1680851452956, '[1678852680689]', b'00', '2024-01-23 16:58:53', '2024-01-23 17:12:41', NULL, NULL);

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
  `uName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
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
INSERT INTO `u_aunt` VALUES (1700732181451, '柳莉莉', '2023-04-25', '2023-04-29', '29天', '4天', '24', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732472483, '柳莉莉', '2023-05-25', '2023-05-29', '28天', '4天', '44', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732530768, '柳莉莉', '2023-06-22', '2023-06-26', '28天', '4天', '28', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732555723, '柳莉莉', '2023-07-20', '2023-07-24', '28天', '4天', '35', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732585816, '柳莉莉', '2023-08-16', '2023-08-20', '28天', '4天', '45', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732644765, '柳莉莉', '2023-09-13', '2023-09-17', '28天', '4天', '56', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732675946, '柳莉莉', '2023-10-12', '2023-10-16', '28天', '4天', '34', '', '', '0');
INSERT INTO `u_aunt` VALUES (1700732732619, '柳莉莉', '2023-11-09', '2023-11-14', '28天', '5天', '36', '', '', '0');
INSERT INTO `u_aunt` VALUES (1702257418452, '柳莉莉', '2023-12-08', '2023-12-13', '28天', '5天', '34', '疲惫', '13号早上走的', '0');
INSERT INTO `u_aunt` VALUES (1704683011725, '柳莉莉', '2024-01-07', '2024-01-13', '29天', '6天', '86', '无', '7号下午来的，13号中午走的', '0');

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
INSERT INTO `u_source` VALUES ('0g1rqfk8crr', '胡永珍', 'huyongzhen', '1937-03-08', '女', '重庆市', '', '15723084621', '11@qq.com', '2', NULL, NULL, '/static/2023-12-14/1666794901605.png', '1', '', '9p3k9fas1i', '');
INSERT INTO `u_source` VALUES ('14y30y4v6w', '冉仁杰', 'ranrenjie', '1992-05-21', '男', '重庆市长寿县', '重庆市丰都县高家镇', '', '', '4', NULL, NULL, NULL, '1', '', 'b3v3p0tisd', '');
INSERT INTO `u_source` VALUES ('1hh7v9z640', '胡剑肖', 'hujianxiao', '2022-03-27', '男', '', '', '', '', '4', 'b4zdv8baxv', 'tjgt221res', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('35ke236146', '刘长明', 'liuzhangming', '1967-02-21', '男', '重庆市丰都县高家镇', '', '', '', '3', '9p3k9fas1i', '0g1rqfk8crr', NULL, '1', '', '00zz86t0jj', '');
INSERT INTO `u_source` VALUES ('3aii36iyaw', '秦淑英', 'qinshuying', '1964-08-28', '女', '重庆市丰都县高家镇', '重庆市丰都县龙孔镇', '', '', '3', NULL, NULL, NULL, '1', '', 'ko6n6notqf', '');
INSERT INTO `u_source` VALUES ('3rbgzsk4vd', '冉小希', 'ranxiaoxi', '2018-07-29', '女', '重庆市垫江县', '', '', '', '5', '14y30y4v6w', 'b3v3p0tisd', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('4e1ia2s0on', '刘淑兰', 'liushulan', '2022-03-26', '女', '', '', '', '', '3', '9p3k9fas1i', '0g1rqfk8crr', NULL, '1', '', '6ozfdv1wzx', '');
INSERT INTO `u_source` VALUES ('4hh8h70y3e', '陈文香', 'chenwenxiang', '2022-03-26', '女', '', '', '', '', '3', NULL, NULL, NULL, '1', '', 'wbgqyr1qko', '');
INSERT INTO `u_source` VALUES ('530j02ggwt', '杨本山', 'yangbenshan', '1963-04-20', '男', '重庆市丰都县龙孔镇', '重庆市丰都县龙孔镇', '', '', '3', '7hdj73e2m3', 'a05zuimvb1', '/static/2023-12-12/甘雨 原神 4k高清壁纸3840x2160_彼岸图网.jpg', '0', '2005-04-04', 'zhvoh3r230', '');
INSERT INTO `u_source` VALUES ('58gqn9ktbv', '1', '1', '2023-12-19', '男', '', '', '', '', '5', 'msa3w8kcwp', 'dqpo1dmqxk1', NULL, '1', '', 'y10ubewhj8', '');
INSERT INTO `u_source` VALUES ('5t27ia95vvy', '？', '？', '2022-03-27', '女', '', '', '', '', '2', NULL, NULL, NULL, '0', '2022-03-27', 'd4cemr6j9n', '');
INSERT INTO `u_source` VALUES ('6ozfdv1wzx', '陶一平', 'taoyiping', '2022-03-26', '男', '重庆江津', '', '', '', '3', NULL, NULL, NULL, '1', '', '4e1ia2s0on', '');
INSERT INTO `u_source` VALUES ('74vcjuwhec', '杨涛睿', 'yangtaorui', '2014-04-25', '男', '重庆市云阳县', '', '', '', '5', 'qg0t2x3kvn', '93juwbxv64', '/static/2023-04-07/原神.jpg', '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('7hdj73e2m3', '杨仁清', 'yangrenqing', '1939-04-25', '男', '重庆市丰都县龙孔镇', '重庆市丰都县龙孔镇', '15023938647', '', '2', 'd4cemr6j9n', '5t27ia95vvy', '/static/2023-12-12/1666794906748.png', '1', '', 'a05zuimvb1', '︿(￣︶￣)︿');
INSERT INTO `u_source` VALUES ('93juwbxv64', '姚兴红', 'yaoxinghong', '1988-12-24', '女', '重庆市云阳县', '', '', '', '4', NULL, NULL, NULL, '1', '', 'qg0t2x3kvn', '');
INSERT INTO `u_source` VALUES ('94ou95a7iq', '杨紫明', 'yangziming', '2022-03-27', '男', '', '', '', '', '4', 'yuwxtp3o56', 'skmuhxp59w', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('99yydauop5', '胡彪', 'hubiao', '2022-03-27', '男', '', '', '', '', '4', 'b4zdv8baxv', 'tjgt221res', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('9p3k9fas1i', '刘明福', 'liumingfu', '1937-11-26', '男', '', '重庆', '15723084621', '', '2', NULL, NULL, '/static/2023-12-12/1666794890983.png', '0', '2000-07-15', '0g1rqfk8crr', '');
INSERT INTO `u_source` VALUES ('a05zuimvb1', '秦光芳', 'qinguangfang', '1941-03-23', '女', '重庆市丰都县龙孔镇', '重庆市丰都县龙孔镇', '15723084621', '', '2', NULL, NULL, '/static/2023-12-12/lol-辉女郎.jpg', '1', '', '7hdj73e2m3', '零零');
INSERT INTO `u_source` VALUES ('ar7qcxejqo', '谭志宏', 'tanzhihong', '2006-07-01', '男', '重庆市丰都县高家镇', '', '', '', '4', 'ym7pqv3fo4', 'v1x6hbpn0k', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('b3v3p0tisd', '谭琪', 'tanqi', '1991-12-27', '女', '重庆市长寿县', '', '', '', '4', 'ym7pqv3fo4', 'v1x6hbpn0k', NULL, '1', '', '14y30y4v6w', '');
INSERT INTO `u_source` VALUES ('b3yifraw1v', '杨歆禾', 'yangxinhe', '2021-04-16', '男', '', '', '', '', '5', 'ccz0ganf1z', 'xy1b43fgds', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('b4zdv8baxv', '胡道宏', 'hudaohong', '2022-03-26', '男', '', '', '', '', '3', NULL, NULL, NULL, '1', '', 'tjgt221res', '');
INSERT INTO `u_source` VALUES ('ccz0ganf1z', '杨紫维', 'yangziwei', '1989-05-23', '男', '', '', '', '', '4', 'ko6n6notqf', '3aii36iyaw', NULL, '1', '', 'xy1b43fgds', '');
INSERT INTO `u_source` VALUES ('d4cemr6j9n', '杨？', 'yang？', '2022-03-27', '男', '', '', '', '', '1', NULL, NULL, NULL, '0', '2022-03-27', '5t27ia95vvy', '');
INSERT INTO `u_source` VALUES ('d9167tv1w6', '刘丽章', 'liulizhang', '2000-09-25', '女', '北京市', '', '', '', '4', '35ke236146', '00zz86t0jj', NULL, '1', '', '', '');
INSERT INTO `u_source` VALUES ('dqpo1dmqxk1', '1-M', '1-M', '2023-12-19', '女', '', '', '', '', '4', NULL, NULL, NULL, '1', '', 'msa3w8kcwp', '');
INSERT INTO `u_source` VALUES ('dv3sndwq44', '谭香敏', 'tanxiangmin', '1997-09-21', '女', '重庆市丰都县高家镇', '', '', '', '4', 'ym7pqv3fo4', 'v1x6hbpn0k', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('e468bpxsvy', '杨华杰', 'yanghuajie', '1997-10-29', '男', '福建福州马尾区', '重庆重庆市丰都', '15723084621', '12@qq.com', '4', 'oi1frjtngz', 'wsjtvd8b4f', NULL, '1', '', '', '测试座右铭法法师范德萨发顺丰');
INSERT INTO `u_source` VALUES ('fa7t2rpbt7', '玉华', 'yuhua', '2022-03-27', '女', '', '', '', '', '4', NULL, NULL, NULL, '1', '', 'x8ekpafbid', '');
INSERT INTO `u_source` VALUES ('ffv4rintqp', '徐于磊', 'xuyulei', '2022-03-27', '女', '', '', '', '', '4', NULL, NULL, NULL, '1', '', 'fmbdwq381n', '');
INSERT INTO `u_source` VALUES ('fmbdwq381n', '刘波', 'liubo', '2022-03-27', '男', '重庆市丰都县', '', '', '', '4', 'wbgqyr1qko', '4hh8h70y3e', NULL, '1', '', 'ffv4rintqp', '');
INSERT INTO `u_source` VALUES ('gvoqe4wg6e', '杨淑？', 'yangshu？', '2022-03-27', '女', '', '', '', '', '3', 'u0gcc8un61', 'mb996bx7jy', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('h04k86of0b', '陶禹臣', 'taoyuchen', '2022-03-27', '男', '', '', '', '', '5', 'x8ekpafbid', 'fa7t2rpbt7', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('kcw2i5oc6h', '刘志恒', 'liuzhiheng', '2022-03-27', '男', '', '', '', '', '5', 'fmbdwq381n', 'ffv4rintqp', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('ko6n6notqf', '杨本林', 'yangbenlin', '1964-12-24', '男', '重庆市丰都县高家镇', '重庆市丰都县高家镇', '', '', '3', '7hdj73e2m3', 'a05zuimvb1', NULL, '1', '', '3aii36iyaw', '');
INSERT INTO `u_source` VALUES ('mb996bx7jy', '？', '？', '2022-03-27', '女', '', '', '', '', '1', NULL, NULL, NULL, '0', '2022-03-27', 'u0gcc8un61', '');
INSERT INTO `u_source` VALUES ('mmcei69soi', '杨紫东', 'yangzidong', '2001-02-15', '男', '', '', '', '', '4', 'ko6n6notqf', '3aii36iyaw', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('msa3w8kcwp', '1-F', '1-F', '2023-12-19', '男', '', '', '', '', '4', NULL, NULL, NULL, '1', '', 'dqpo1dmqxk1', '');
INSERT INTO `u_source` VALUES ('mz1182s8bg', '刘润康', 'liurunkang', '2007-03-29', '男', '重庆市丰都县高家镇', '', '', '', '4', '35ke236146', '00zz86t0jj', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('oi1frjtngz', '杨本贵', 'yangbengui', '1971-10-03', '男', '重庆市丰都县龙孔镇', '重庆市丰都县龙孔镇', '', '', '3', '7hdj73e2m3', 'a05zuimvb1', NULL, '0', '2003-04-04', 'wsjtvd8b4f', '');
INSERT INTO `u_source` VALUES ('qg0t2x3kvn', '杨志华', 'yangzhihua', '1987-07-19', '男', '重庆市云阳县', '重庆市丰都县龙孔镇', '', '', '4', '530j02ggwt', 'zhvoh3r230', NULL, '1', '', '93juwbxv64', '');
INSERT INTO `u_source` VALUES ('rjat9enqmi', '杨茂江', 'yangmaojiang', '1999-01-07', '男', '重庆市丰都县高家镇', '重庆市丰都县龙孔镇', '', '', '4', '530j02ggwt', 'zhvoh3r230', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('skmuhxp59w', '姚源菊', 'yaoyuanju', '2022-03-27', '女', '', '', '', '', '3', NULL, NULL, NULL, '1', '', 'yuwxtp3o56', '');
INSERT INTO `u_source` VALUES ('tjgt221res', '刘淑英', 'liushuying', '2022-03-26', '女', '', '', '', '', '3', '9p3k9fas1i', '0g1rqfk8crr', NULL, '1', '', 'b4zdv8baxv', '');
INSERT INTO `u_source` VALUES ('u0gcc8un61', '杨仁？', 'yangren？', '2022-03-27', '男', '', '', '', '', '2', 'd4cemr6j9n', '5t27ia95vvy', NULL, '1', '', 'mb996bx7jy', '');
INSERT INTO `u_source` VALUES ('udxpi2sme7', '陶成东', 'taochengdong', '2022-03-27', '男', '', '', '', '', '4', '6ozfdv1wzx', '4e1ia2s0on', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('v1x6hbpn0k', '杨淑琼', 'yangshuqiong', '1969-10-10', '女', '重庆市丰都县高家镇', '重庆市丰都县龙孔镇', '', '', '3', '7hdj73e2m3', 'a05zuimvb1', NULL, '1', '', 'ym7pqv3fo4', '');
INSERT INTO `u_source` VALUES ('wbgqyr1qko', '刘国生', 'liuguosheng', '2022-03-26', '男', '', '', '', '', '3', '9p3k9fas1i', '0g1rqfk8crr', NULL, '1', '', '4hh8h70y3e', '');
INSERT INTO `u_source` VALUES ('ws1atrvwrg', '刘桂佳', 'liuguijia', '2001-03-25', '女', '', '', '', '', '4', 'wbgqyr1qko', '4hh8h70y3e', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('wsjtvd8b4f', '王道芳', 'wangdaofang', '1969-01-29', '女', '福建省福州市马尾区魁岐小区B8-902', '重庆市石柱县流水村', '', '', '3', NULL, NULL, NULL, '1', '', 'oi1frjtngz', '');
INSERT INTO `u_source` VALUES ('x2qt0j3h0q', '杨利洁', 'yanglijie', '1996-10-06', '男', '福建省福州市马尾区魁岐小区B8-902', '重庆市石柱县流水村', '15960188443', '', '4', 'oi1frjtngz', 'wsjtvd8b4f', NULL, '1', '', NULL, '');
INSERT INTO `u_source` VALUES ('x8ekpafbid', '陶毅', 'taoyi', '2022-03-26', '男', '', '', '', '', '4', '6ozfdv1wzx', '4e1ia2s0on', NULL, '1', '', 'fa7t2rpbt7', '');
INSERT INTO `u_source` VALUES ('xy1b43fgds', '何丹', 'hedan', '1994-04-08', '女', '', '', '', '', '4', NULL, NULL, NULL, '1', '', 'ccz0ganf1z', '');
INSERT INTO `u_source` VALUES ('y10ubewhj8', '杨姚姚', 'yangyaoyao', '2022-03-27', '女', '', '', '', '', '5', 'yuwxtp3o56', 'skmuhxp59w', NULL, '1', '', '58gqn9ktbv', '');
INSERT INTO `u_source` VALUES ('ym7pqv3fo4', '谭正华', 'tanzhenghua', '1967-07-17', '男', '重庆市丰都县高家镇', '', '', '', '3', NULL, NULL, NULL, '1', '', 'v1x6hbpn0k', '');
INSERT INTO `u_source` VALUES ('yuwxtp3o56', '杨本成', 'yangbencheng', '2022-03-27', '男', '', '', '', '', '3', 'u0gcc8un61', 'mb996bx7jy', NULL, '1', '', 'skmuhxp59w', '');
INSERT INTO `u_source` VALUES ('zhvoh3r230', '何素珍', 'hesuzhen', '1967-07-18', '女', '重庆市丰都县高家镇', '重庆市丰都县龙孔镇', '', '', '3', NULL, NULL, NULL, '1', '', '530j02ggwt', '');
INSERT INTO `u_source` VALUES ('zpipa5qxqg', '杨博尧', 'yangboyao', '2018-08-13', '男', '重庆市云阳县', '', '', '', '5', 'qg0t2x3kvn', '93juwbxv64', NULL, '1', '', '', '');

SET FOREIGN_KEY_CHECKS = 1;
