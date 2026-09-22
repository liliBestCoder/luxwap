-- ========================================================
-- Luxwap / RuoYi Local H2 Database Initialization Script
-- Exported from online database xray_partner (101.201.215.20)
-- Specially adapted for H2 (MODE=MySQL)
-- ========================================================
SET FOREIGN_KEY_CHECKS = 0;

-- MySQL dump 10.13  Distrib 8.0.46, for Linux (x86_64)
--
-- Host: localhost    Database: xray_partner
-- ------------------------------------------------------
-- Server version	8.0.46-0ubuntu0.24.04.4


--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200)   DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500)   DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64)   DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64)   DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100)   DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200)   DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作 sub主子表操作）',
  `package_name` varchar(100)   DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30)   DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30)   DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50)   DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50)   DEFAULT NULL COMMENT '生成功能作者',
  `form_col_num` int DEFAULT '1' COMMENT '表单布局（单列 双列 三列）',
  `gen_type` char(1)   DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200)   DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000)   DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500)   DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
);

--
-- Dumping data for table `gen_table`
--


--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200)   DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500)   DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100)   DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500)   DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200)   DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1)   DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1)   DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1)   DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1)   DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1)   DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1)   DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1)   DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200)   DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200)   DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200)   DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
);

--
-- Dumping data for table `gen_table_column`
--


--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`)
);

--
-- Dumping data for table `qrtz_blob_triggers`
--


--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200)   NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
);

--
-- Dumping data for table `qrtz_calendars`
--


--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200)   NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80)   DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`)
);

--
-- Dumping data for table `qrtz_cron_triggers`
--


--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95)   NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200)   NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16)   NOT NULL COMMENT '状态',
  `job_name` varchar(200)   DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200)   DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1)   DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1)   DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
);

--
-- Dumping data for table `qrtz_fired_triggers`
--


--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `job_name` varchar(200)   NOT NULL COMMENT '任务名称',
  `job_group` varchar(200)   NOT NULL COMMENT '任务组名',
  `description` varchar(250)   DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250)   NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1)   NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1)   NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1)   NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1)   NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
);

--
-- Dumping data for table `qrtz_job_details`
--


--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40)   NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
);

--
-- Dumping data for table `qrtz_locks`
--


--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
);

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--


--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200)   NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
);

--
-- Dumping data for table `qrtz_scheduler_state`
--


--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`)
);

--
-- Dumping data for table `qrtz_simple_triggers`
--


--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200)   NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512)   DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512)   DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512)   DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1)   DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1)   DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`)
);

--
-- Dumping data for table `qrtz_simprop_triggers`
--


--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120)   NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200)   NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200)   NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200)   NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200)   NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250)   DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16)   NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8)   NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200)   DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `qrtz_triggers_sched_name` (`sched_name`,`job_name`,`job_group`)
);

--
-- Dumping data for table `qrtz_triggers`
--


--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100)   DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100)   DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500)   DEFAULT '' COMMENT '参数键值',
  `config_type` char(1)   DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500)   DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
);

--
-- Dumping data for table `sys_config`
--

INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2025-09-18 13:44:00','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2025-09-18 13:44:00','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2025-09-18 13:44:00','',NULL,'深黑主题theme-dark，浅色主题theme-light，深蓝主题theme-blue'),(4,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2025-09-18 13:44:00','',NULL,'是否开启注册用户功能（true开启，false关闭）'),(5,'用户管理-密码字符范围','sys.account.chrtype','0','Y','admin','2025-09-18 13:44:00','',NULL,'默认任意字符范围，0任意（密码可以输入任意字符），1数字（密码只能为0-9数字），2英文字母（密码只能为a-z和A-Z字母），3字母和数字（密码必须包含字母，数字）,4字母数字和特殊字符（目前支持的特殊字符包括：~!@#$%^&*()-=_+）'),(6,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2025-09-18 13:44:00','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),(7,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2025-09-18 13:44:00','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框'),(8,'主框架页-菜单导航显示风格','sys.index.menuStyle','default','Y','admin','2025-09-18 13:44:00','',NULL,'菜单导航显示风格（default为左侧导航菜单，topnav为顶部导航菜单）'),(9,'主框架页-是否开启页脚','sys.index.footer','true','Y','admin','2025-09-18 13:44:00','',NULL,'是否开启底部页脚显示（true显示，false隐藏）'),(10,'主框架页-是否开启页签','sys.index.tagsView','true','Y','admin','2025-09-18 13:44:00','',NULL,'是否开启菜单多页签显示（true显示，false隐藏）'),(11,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2025-09-18 13:44:00','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50)   DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30)   DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20)   DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11)   DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50)   DEFAULT NULL COMMENT '邮箱',
  `status` char(1)   DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1)   DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
);

--
-- Dumping data for table `sys_dept`
--

INSERT INTO `sys_dept` VALUES (100,0,'0','若依科技',0,'若依','15888888888','ry@qq.com','0','0','admin','2025-09-18 13:43:59','',NULL),(101,100,'0,100','深圳总公司',1,'若依','15888888888','ry@qq.com','0','0','admin','2025-09-18 13:43:59','',NULL),(102,100,'0,100','长沙分公司',2,'若依','15888888888','ry@qq.com','0','0','admin','2025-09-18 13:43:59','',NULL),(103,101,'0,100,101','研发部门',1,'若依','15888888888','ry@qq.com','0','0','admin','2025-09-18 13:43:59','',NULL),(104,101,'0,100,101','市场部门',2,'若依','15888888888','ry@qq.com','0','0','admin','2025-09-18 13:43:59','',NULL),(105,101,'0,100,101','测试部门',3,'若依','15888888888','ry@qq.com','0','0','admin','2025-09-18 13:43:59','',NULL),(106,101,'0,100,101','财务部门',4,'若依','15888888888','ry@qq.com','0','0','admin','2025-09-18 13:43:59','',NULL),(107,101,'0,100,101','运维部门',5,'若依','15888888888','ry@qq.com','0','0','admin','2025-09-18 13:43:59','',NULL),(108,102,'0,100,102','市场部门',1,'若依','15888888888','ry@qq.com','0','0','admin','2025-09-18 13:43:59','',NULL),(109,102,'0,100,102','财务部门',2,'若依','15888888888','ry@qq.com','0','0','admin','2025-09-18 13:43:59','',NULL);

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100)   DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100)   DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100)   DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100)   DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100)   DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1)   DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1)   DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500)   DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
);

--
-- Dumping data for table `sys_dict_data`
--

INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','0','admin','2025-09-18 13:44:00','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','admin','2025-09-18 13:44:00','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','admin','2025-09-18 13:44:00','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2025-09-18 13:44:00','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2025-09-18 13:44:00','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2025-09-18 13:44:00','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2025-09-18 13:44:00','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2025-09-18 13:44:00','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2025-09-18 13:44:00','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2025-09-18 13:44:00','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2025-09-18 13:44:00','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2025-09-18 13:44:00','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2025-09-18 13:44:00','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2025-09-18 13:44:00','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2025-09-18 13:44:00','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2025-09-18 13:44:00','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2025-09-18 13:44:00','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2025-09-18 13:44:00','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2025-09-18 13:44:00','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2025-09-18 13:44:00','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2025-09-18 13:44:00','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2025-09-18 13:44:00','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2025-09-18 13:44:00','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2025-09-18 13:44:00','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2025-09-18 13:44:00','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2025-09-18 13:44:00','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2025-09-18 13:44:00','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2025-09-18 13:44:00','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2025-09-18 13:44:00','',NULL,'停用状态');

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100)   DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100)   DEFAULT '' COMMENT '字典类型',
  `status` char(1)   DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500)   DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `sys_dict_type_dict_type` (`dict_type`)
);

--
-- Dumping data for table `sys_dict_type`
--

INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2025-09-18 13:44:00','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2025-09-18 13:44:00','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2025-09-18 13:44:00','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2025-09-18 13:44:00','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2025-09-18 13:44:00','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2025-09-18 13:44:00','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2025-09-18 13:44:00','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2025-09-18 13:44:00','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2025-09-18 13:44:00','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2025-09-18 13:44:00','',NULL,'登录状态列表');

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64)   NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64)   NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500)   NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255)   DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20)   DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1)   DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1)   DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500)   DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
);

--
-- Dumping data for table `sys_job`
--

INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2025-09-18 13:44:00','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(''ry'')','0/15 * * * * ?','3','1','1','admin','2025-09-18 13:44:00','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(''ry'', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2025-09-18 13:44:00','',NULL,'');

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64)   NOT NULL COMMENT '任务名称',
  `job_group` varchar(64)   NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500)   NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500)   DEFAULT NULL COMMENT '日志信息',
  `status` char(1)   DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000)   DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
);

--
-- Dumping data for table `sys_job_log`
--


--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `login_name` varchar(50)   DEFAULT '' COMMENT '登录账号',
  `ipaddr` varchar(128)   DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255)   DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50)   DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50)   DEFAULT '' COMMENT '操作系统',
  `status` char(1)   DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255)   DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `sys_logininfor_idx_sys_logininfor_s` (`status`),
  KEY `sys_logininfor_idx_sys_logininfor_lt` (`login_time`)
);

--
-- Dumping data for table `sys_logininfor`
--

INSERT INTO `sys_logininfor` VALUES (100,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','密码输入错误1次','2025-09-18 15:37:00'),(101,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:37:06'),(102,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:37:07'),(103,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:37:07'),(104,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','密码输入错误2次','2025-09-18 15:37:12'),(105,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','密码输入错误3次','2025-09-18 15:38:43'),(106,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:45'),(107,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:46'),(108,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:46'),(109,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:47'),(110,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:47'),(111,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:48'),(112,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:48'),(113,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:49'),(114,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:49'),(115,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:49'),(116,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:38:50'),(117,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','验证码错误','2025-09-18 15:39:02'),(118,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','密码输入错误4次','2025-09-18 15:39:04'),(119,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-09-18 15:44:03'),(120,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','密码输入错误1次','2025-10-20 23:27:46'),(121,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-20 23:27:59'),(122,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','密码输入错误1次','2025-10-20 23:32:33'),(123,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-20 23:32:42'),(124,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','1','密码输入错误1次','2025-10-20 23:35:23'),(125,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-20 23:35:29'),(126,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-20 23:36:34'),(127,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-22 23:01:45'),(128,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-22 23:08:27'),(129,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-22 23:53:08'),(130,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-22 23:55:34'),(131,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-23 23:15:43'),(132,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-23 23:20:26'),(133,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-23 23:23:10'),(134,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-10-29 21:12:10'),(135,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-11-10 23:00:38'),(136,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-11-10 23:45:52'),(137,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-11-11 18:00:42'),(138,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-11-11 18:01:40'),(139,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-11-11 18:12:44'),(140,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-11-11 18:13:59'),(141,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-11-11 18:17:50'),(142,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2025-11-11 18:55:53'),(143,'admin','116.2.2.6','XX XX','Chrome 14','Windows 10','1','密码输入错误1次','2026-06-14 22:00:33'),(144,'admin','116.2.2.6','XX XX','Chrome 14','Windows 10','0','登录成功','2026-06-14 22:00:44'),(145,'admin','112.96.212.162','XX XX','Chrome 14','Mac OS X','0','登录成功','2026-06-14 22:31:03'),(146,'admin','8.139.192.246','XX XX','Chrome 15','Windows 10','1','密码输入错误1次','2026-09-04 09:38:26'),(147,'admin','8.139.192.246','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-04 09:39:57'),(148,'adin','112.96.202.86','XX XX','Chrome 15','Windows 10','1','用户不存在/密码错误','2026-09-06 19:59:44'),(149,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-06 20:00:24'),(150,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-07 08:57:51'),(151,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-07 13:52:12'),(152,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误1次','2026-09-07 14:34:42'),(153,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误2次','2026-09-07 14:34:48'),(154,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误3次','2026-09-07 14:34:52'),(155,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误4次','2026-09-07 14:34:56'),(156,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','验证码错误','2026-09-07 14:35:10'),(157,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次','2026-09-07 14:35:17'),(158,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','验证码错误','2026-09-07 14:35:19'),(159,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:35:25'),(160,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:36:06'),(161,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','验证码错误','2026-09-07 14:36:11'),(162,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:36:20'),(163,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:42:43'),(164,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:52:14'),(165,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:55:55'),(166,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:55:59'),(167,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:56:37'),(168,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:56:47'),(169,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:57:08'),(170,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','验证码错误','2026-09-07 14:57:11'),(171,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:57:13'),(172,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:57:20'),(173,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:57:27'),(174,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:57:34'),(175,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:57:45'),(176,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:58:00'),(177,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:58:08'),(178,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:58:14'),(179,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:58:21'),(180,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:58:27'),(181,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:58:33'),(182,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:58:40'),(183,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:58:50'),(184,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:58:56'),(185,'admin','116.2.24.27','XX XX','Chrome 15','Linux','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:59:01'),(186,'admin','116.2.24.27','XX XX','Chrome Mobile','Android 1.x','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:59:32'),(187,'admin','116.2.24.27','XX XX','Chrome Mobile','Android 1.x','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:59:38'),(188,'admin','116.2.24.27','XX XX','Chrome Mobile','Android 1.x','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:59:40'),(189,'admin','116.2.24.27','XX XX','Chrome Mobile','Android 1.x','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:59:42'),(190,'admin','116.2.24.27','XX XX','Chrome Mobile','Android 1.x','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:59:47'),(191,'admin','116.2.24.27','XX XX','Chrome Mobile','Android 1.x','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:59:52'),(192,'admin','116.2.24.27','XX XX','Chrome Mobile','Android 1.x','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 14:59:58'),(193,'admin','116.2.24.27','XX XX','Chrome Mobile','Android 1.x','1','密码输入错误5次，帐户锁定10分钟','2026-09-07 15:00:04'),(194,'admin','116.2.24.27','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2026-09-07 15:05:03'),(195,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','1','密码输入错误1次','2026-09-07 15:19:34'),(196,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-07 15:19:45'),(197,'admin','8.139.192.246','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 11:33:40'),(198,'admin','8.139.192.246','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 11:51:36'),(199,'admin','8.139.192.246','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 11:55:32'),(200,'admin','8.139.192.246','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 12:01:09'),(201,'lilibestcoder@163.com','8.139.192.246','XX XX','Mozilla','Windows 10','1','验证码错误','2026-09-08 12:07:51'),(202,'admin','8.139.192.246','XX XX','Mozilla','Windows 10','1','验证码错误','2026-09-08 12:08:51'),(203,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 12:36:56'),(204,'admin','39.144.59.158','XX XX','Chrome 15','Linux','1','密码输入错误1次','2026-09-08 12:46:33'),(205,'admin','39.144.59.158','XX XX','Chrome 15','Linux','0','登录成功','2026-09-08 12:46:50'),(206,'admin','39.144.59.158','XX XX','Chrome 15','Linux','0','登录成功','2026-09-08 12:48:10'),(207,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 12:50:46'),(208,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 12:52:52'),(209,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 12:54:56'),(210,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 13:08:07'),(211,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 13:13:28'),(212,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-08 14:24:57'),(213,'admin','112.96.202.86','XX XX','Chrome 15','Windows 10','0','登录成功','2026-09-09 09:33:12');

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50)   NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `url` varchar(200)   DEFAULT '#' COMMENT '请求地址',
  `target` varchar(20)   DEFAULT '' COMMENT '打开方式（menuItem页签 menuBlank新窗口）',
  `menu_type` char(1)   DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1)   DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `is_refresh` char(1)   DEFAULT '1' COMMENT '是否刷新（0刷新 1不刷新）',
  `perms` varchar(100)   DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100)   DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500)   DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
);

--
-- Dumping data for table `sys_menu`
--

INSERT INTO `sys_menu` VALUES (1,'系统管理',0,1,'#','','M','0','1','','fa fa-gear','admin','2025-09-18 13:43:59','',NULL,'系统管理目录'),(2,'系统监控',0,2,'#','','M','0','1','','fa fa-video-camera','admin','2025-09-18 13:43:59','',NULL,'系统监控目录'),(3,'系统工具',0,3,'#','','M','0','1','','fa fa-bars','admin','2025-09-18 13:43:59','',NULL,'系统工具目录'),(4,'若依官网',0,4,'http://ruoyi.vip','menuBlank','C','0','1','','fa fa-location-arrow','admin','2025-09-18 13:43:59','',NULL,'若依官网地址'),(100,'用户管理',1,1,'/system/user','','C','0','1','system:user:view','fa fa-user-o','admin','2025-09-18 13:43:59','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'/system/role','','C','0','1','system:role:view','fa fa-user-secret','admin','2025-09-18 13:43:59','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'/system/menu','','C','0','1','system:menu:view','fa fa-th-list','admin','2025-09-18 13:43:59','',NULL,'菜单管理菜单'),(103,'部门管理',1,4,'/system/dept','','C','0','1','system:dept:view','fa fa-outdent','admin','2025-09-18 13:43:59','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'/system/post','','C','0','1','system:post:view','fa fa-address-card-o','admin','2025-09-18 13:43:59','',NULL,'岗位管理菜单'),(105,'字典管理',1,6,'/system/dict','','C','0','1','system:dict:view','fa fa-bookmark-o','admin','2025-09-18 13:43:59','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'/system/config','','C','0','1','system:config:view','fa fa-sun-o','admin','2025-09-18 13:43:59','',NULL,'参数设置菜单'),(107,'通知公告',1,8,'/system/notice','','C','0','1','system:notice:view','fa fa-bullhorn','admin','2025-09-18 13:43:59','',NULL,'通知公告菜单'),(108,'日志管理',1,9,'#','','M','0','1','','fa fa-pencil-square-o','admin','2025-09-18 13:43:59','',NULL,'日志管理菜单'),(109,'在线用户',2,1,'/monitor/online','','C','0','1','monitor:online:view','fa fa-user-circle','admin','2025-09-18 13:43:59','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'/monitor/job','','C','0','1','monitor:job:view','fa fa-tasks','admin','2025-09-18 13:43:59','',NULL,'定时任务菜单'),(111,'数据监控',2,3,'/monitor/data','','C','0','1','monitor:data:view','fa fa-bug','admin','2025-09-18 13:43:59','',NULL,'数据监控菜单'),(112,'服务监控',2,4,'/monitor/server','','C','0','1','monitor:server:view','fa fa-server','admin','2025-09-18 13:43:59','',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'/monitor/cache','','C','0','1','monitor:cache:view','fa fa-cube','admin','2025-09-18 13:43:59','',NULL,'缓存监控菜单'),(114,'表单构建',3,1,'/tool/build','','C','0','1','tool:build:view','fa fa-wpforms','admin','2025-09-18 13:43:59','',NULL,'表单构建菜单'),(115,'代码生成',3,2,'/tool/gen','','C','0','1','tool:gen:view','fa fa-code','admin','2025-09-18 13:43:59','',NULL,'代码生成菜单'),(116,'系统接口',3,3,'/tool/swagger','','C','0','1','tool:swagger:view','fa fa-gg','admin','2025-09-18 13:43:59','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'/monitor/operlog','','C','0','1','monitor:operlog:view','fa fa-address-book','admin','2025-09-18 13:43:59','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'/monitor/logininfor','','C','0','1','monitor:logininfor:view','fa fa-file-image-o','admin','2025-09-18 13:43:59','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'#','','F','0','1','system:user:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1001,'用户新增',100,2,'#','','F','0','1','system:user:add','#','admin','2025-09-18 13:43:59','',NULL,''),(1002,'用户修改',100,3,'#','','F','0','1','system:user:edit','#','admin','2025-09-18 13:43:59','',NULL,''),(1003,'用户删除',100,4,'#','','F','0','1','system:user:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1004,'用户导出',100,5,'#','','F','0','1','system:user:export','#','admin','2025-09-18 13:43:59','',NULL,''),(1005,'用户导入',100,6,'#','','F','0','1','system:user:import','#','admin','2025-09-18 13:43:59','',NULL,''),(1006,'重置密码',100,7,'#','','F','0','1','system:user:resetPwd','#','admin','2025-09-18 13:43:59','',NULL,''),(1007,'角色查询',101,1,'#','','F','0','1','system:role:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1008,'角色新增',101,2,'#','','F','0','1','system:role:add','#','admin','2025-09-18 13:43:59','',NULL,''),(1009,'角色修改',101,3,'#','','F','0','1','system:role:edit','#','admin','2025-09-18 13:43:59','',NULL,''),(1010,'角色删除',101,4,'#','','F','0','1','system:role:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1011,'角色导出',101,5,'#','','F','0','1','system:role:export','#','admin','2025-09-18 13:43:59','',NULL,''),(1012,'菜单查询',102,1,'#','','F','0','1','system:menu:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1013,'菜单新增',102,2,'#','','F','0','1','system:menu:add','#','admin','2025-09-18 13:43:59','',NULL,''),(1014,'菜单修改',102,3,'#','','F','0','1','system:menu:edit','#','admin','2025-09-18 13:43:59','',NULL,''),(1015,'菜单删除',102,4,'#','','F','0','1','system:menu:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1016,'部门查询',103,1,'#','','F','0','1','system:dept:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1017,'部门新增',103,2,'#','','F','0','1','system:dept:add','#','admin','2025-09-18 13:43:59','',NULL,''),(1018,'部门修改',103,3,'#','','F','0','1','system:dept:edit','#','admin','2025-09-18 13:43:59','',NULL,''),(1019,'部门删除',103,4,'#','','F','0','1','system:dept:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1020,'岗位查询',104,1,'#','','F','0','1','system:post:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1021,'岗位新增',104,2,'#','','F','0','1','system:post:add','#','admin','2025-09-18 13:43:59','',NULL,''),(1022,'岗位修改',104,3,'#','','F','0','1','system:post:edit','#','admin','2025-09-18 13:43:59','',NULL,''),(1023,'岗位删除',104,4,'#','','F','0','1','system:post:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1024,'岗位导出',104,5,'#','','F','0','1','system:post:export','#','admin','2025-09-18 13:43:59','',NULL,''),(1025,'字典查询',105,1,'#','','F','0','1','system:dict:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1026,'字典新增',105,2,'#','','F','0','1','system:dict:add','#','admin','2025-09-18 13:43:59','',NULL,''),(1027,'字典修改',105,3,'#','','F','0','1','system:dict:edit','#','admin','2025-09-18 13:43:59','',NULL,''),(1028,'字典删除',105,4,'#','','F','0','1','system:dict:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1029,'字典导出',105,5,'#','','F','0','1','system:dict:export','#','admin','2025-09-18 13:43:59','',NULL,''),(1030,'参数查询',106,1,'#','','F','0','1','system:config:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1031,'参数新增',106,2,'#','','F','0','1','system:config:add','#','admin','2025-09-18 13:43:59','',NULL,''),(1032,'参数修改',106,3,'#','','F','0','1','system:config:edit','#','admin','2025-09-18 13:43:59','',NULL,''),(1033,'参数删除',106,4,'#','','F','0','1','system:config:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1034,'参数导出',106,5,'#','','F','0','1','system:config:export','#','admin','2025-09-18 13:43:59','',NULL,''),(1035,'公告查询',107,1,'#','','F','0','1','system:notice:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1036,'公告新增',107,2,'#','','F','0','1','system:notice:add','#','admin','2025-09-18 13:43:59','',NULL,''),(1037,'公告修改',107,3,'#','','F','0','1','system:notice:edit','#','admin','2025-09-18 13:43:59','',NULL,''),(1038,'公告删除',107,4,'#','','F','0','1','system:notice:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1039,'操作查询',500,1,'#','','F','0','1','monitor:operlog:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1040,'操作删除',500,2,'#','','F','0','1','monitor:operlog:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1041,'详细信息',500,3,'#','','F','0','1','monitor:operlog:detail','#','admin','2025-09-18 13:43:59','',NULL,''),(1042,'日志导出',500,4,'#','','F','0','1','monitor:operlog:export','#','admin','2025-09-18 13:43:59','',NULL,''),(1043,'登录查询',501,1,'#','','F','0','1','monitor:logininfor:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1044,'登录删除',501,2,'#','','F','0','1','monitor:logininfor:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1045,'日志导出',501,3,'#','','F','0','1','monitor:logininfor:export','#','admin','2025-09-18 13:43:59','',NULL,''),(1046,'账户解锁',501,4,'#','','F','0','1','monitor:logininfor:unlock','#','admin','2025-09-18 13:43:59','',NULL,''),(1047,'在线查询',109,1,'#','','F','0','1','monitor:online:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1048,'批量强退',109,2,'#','','F','0','1','monitor:online:batchForceLogout','#','admin','2025-09-18 13:43:59','',NULL,''),(1049,'单条强退',109,3,'#','','F','0','1','monitor:online:forceLogout','#','admin','2025-09-18 13:43:59','',NULL,''),(1050,'任务查询',110,1,'#','','F','0','1','monitor:job:list','#','admin','2025-09-18 13:43:59','',NULL,''),(1051,'任务新增',110,2,'#','','F','0','1','monitor:job:add','#','admin','2025-09-18 13:43:59','',NULL,''),(1052,'任务修改',110,3,'#','','F','0','1','monitor:job:edit','#','admin','2025-09-18 13:43:59','',NULL,''),(1053,'任务删除',110,4,'#','','F','0','1','monitor:job:remove','#','admin','2025-09-18 13:43:59','',NULL,''),(1054,'状态修改',110,5,'#','','F','0','1','monitor:job:changeStatus','#','admin','2025-09-18 13:44:00','',NULL,''),(1055,'任务详细',110,6,'#','','F','0','1','monitor:job:detail','#','admin','2025-09-18 13:44:00','',NULL,''),(1056,'任务导出',110,7,'#','','F','0','1','monitor:job:export','#','admin','2025-09-18 13:44:00','',NULL,''),(1057,'生成查询',115,1,'#','','F','0','1','tool:gen:list','#','admin','2025-09-18 13:44:00','',NULL,''),(1058,'生成修改',115,2,'#','','F','0','1','tool:gen:edit','#','admin','2025-09-18 13:44:00','',NULL,''),(1059,'生成删除',115,3,'#','','F','0','1','tool:gen:remove','#','admin','2025-09-18 13:44:00','',NULL,''),(1060,'预览代码',115,4,'#','','F','0','1','tool:gen:preview','#','admin','2025-09-18 13:44:00','',NULL,''),(1061,'生成代码',115,5,'#','','F','0','1','tool:gen:code','#','admin','2025-09-18 13:44:00','',NULL,''),(2000,'luxwap',0,1,'#','','M','0','1',NULL,'#','admin','2025-09-18 13:44:00','',NULL,''),(2001,'客户管理',2000,1,'#','','M','0','1',NULL,'fa-users','admin','2025-09-18 13:44:00','',NULL,''),(2002,'用户信息',2001,1,'/system/xray-user','','C','0','1','system:xray-user:view','#','admin','2025-09-18 13:44:00','',NULL,''),(2003,'分销商信息',2001,2,'/system/distributors','','C','0','1','system:distributors:view','#','admin','2025-09-18 13:44:00','',NULL,''),(2004,'线路信息',2000,1,'/system/lines','','C','0','1','system:lines:view','fa-route','admin','2025-09-18 13:44:00','',NULL,''),(2005,'账单',2000,1,'/system/bill','','C','0','1',NULL,'fa-file-invoice-dollar','admin','2025-09-18 13:44:00','',NULL,''),(2006,'活动管理',2000,1,'/system/activity','','C','0','1','system:activity:view','fa-clipboard-check','admin','2025-09-18 13:44:00','',NULL,''),(2007,'管理员配置',2000,1,'/system/adminsettings','','C','0','1','system:adminsettings:view','fa-shield-alt','admin','2025-09-18 13:44:00','',NULL,''),(2008,'支付接口',2007,1,'#','','F','0','1','system:adminsettings:payment','#','admin','2025-09-18 13:44:00','',NULL,''),(2009,'用户管理',2007,1,'#','','F','0','1','system:adminsettings:users','#','admin','2025-09-18 13:44:00','',NULL,''),(2010,'线路管理',2007,1,'#','','F','0','1','system:adminsettings:lines','#','admin','2025-09-18 13:44:00','',NULL,''),(2011,'财务报表看板',2007,1,'#','','F','0','1','system:adminsettings:board','#','admin','2025-09-18 13:44:00','',NULL,''),(2012,'活动设置',2007,1,'#','','F','0','1','system:adminsettings:activity','#','admin','2025-09-18 13:44:00','',NULL,''),(2013,'经销商政策',2007,1,'#','','F','0','1','system:adminsettings:distributors','#','admin','2025-09-18 13:44:00','',NULL,''),(2014,'权限管理',2007,1,'#','','F','0','1','system:adminsettings:perms','#','admin','2025-09-18 13:44:00','',NULL,'');

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50)   NOT NULL COMMENT '公告标题',
  `notice_type` char(1)   NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1)   DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255)   DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
);

--
-- Dumping data for table `sys_notice`
--

INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 若依新版本发布啦','2',0xE696B0E78988E69CACE58685E5AEB9,'0','admin','2025-09-18 13:44:00','',NULL,'管理员'),(2,'维护通知：2018-07-01 若依系统凌晨维护','1',0xE7BBB4E68AA4E58685E5AEB9,'0','admin','2025-09-18 13:44:00','',NULL,'管理员'),(3,'若依开源框架介绍','1',0x3C703E3C7370616E207374796C653D22636F6C6F723A20726762283233302C20302C2030293B223EE9A1B9E79BAEE4BB8BE7BB8D3C2F7370616E3E3C2F703E3C703E3C666F6E7420636F6C6F723D2223333333333333223E52756F5969E5BC80E6BA90E9A1B9E79BAEE698AFE4B8BAE4BC81E4B89AE794A8E688B7E5AE9AE588B6E79A84E5908EE58FB0E8849AE6898BE69EB6E6A186E69EB6EFBC8CE4B8BAE4BC81E4B89AE68993E980A0E79A84E4B880E7AB99E5BC8FE8A7A3E586B3E696B9E6A188EFBC8CE9998DE4BD8EE4BC81E4B89AE5BC80E58F91E68890E69CACEFBC8CE68F90E58D87E5BC80E58F91E69588E78E87E38082E4B8BBE8A681E58C85E68BACE794A8E688B7E7AEA1E79086E38081E8A792E889B2E7AEA1E79086E38081E983A8E997A8E7AEA1E79086E38081E88F9CE58D95E7AEA1E79086E38081E58F82E695B0E7AEA1E79086E38081E5AD97E585B8E7AEA1E79086E380813C2F666F6E743E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE5B297E4BD8DE7AEA1E790863C2F7370616E3E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE38081E5AE9AE697B6E4BBBBE58AA13C2F7370616E3E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE380813C2F7370616E3E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE69C8DE58AA1E79B91E68EA7E38081E799BBE5BD95E697A5E5BF97E38081E6938DE4BD9CE697A5E5BF97E38081E4BBA3E7A081E7949FE68890E7AD89E58A9FE883BDE38082E585B6E4B8ADEFBC8CE8BF98E694AFE68C81E5A49AE695B0E68DAEE6BA90E38081E695B0E68DAEE69D83E99990E38081E59BBDE99985E58C96E380815265646973E7BC93E5AD98E38081446F636B6572E983A8E7BDB2E38081E6BB91E58AA8E9AA8CE8AF81E7A081E38081E7ACACE4B889E696B9E8AEA4E8AF81E799BBE5BD95E38081E58886E5B883E5BC8FE4BA8BE58AA1E380813C2F7370616E3E3C666F6E7420636F6C6F723D2223333333333333223EE58886E5B883E5BC8FE69687E4BBB6E5AD98E582A83C2F666F6E743E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE38081E58886E5BA93E58886E8A1A8E5A484E79086E7AD89E68A80E69CAFE789B9E782B9E380823C2F7370616E3E3C2F703E3C703E3C696D67207372633D2268747470733A2F2F666F727564612E67697465652E636F6D2F696D616765732F313730353033303538333937373430313635312F35656435646236615F313135313030342E706E6722207374796C653D2277696474683A20363470783B223E3C62723E3C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A20726762283233302C20302C2030293B223EE5AE98E7BD91E58F8AE6BC94E7A4BA3C2F7370616E3E3C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE88BA5E4BE9DE5AE98E7BD91E59CB0E59D80EFBC9A266E6273703B3C2F7370616E3E3C6120687265663D22687474703A2F2F72756F79692E76697022207461726765743D225F626C616E6B223E687474703A2F2F72756F79692E7669703C2F613E3C6120687265663D22687474703A2F2F72756F79692E76697022207461726765743D225F626C616E6B223E3C2F613E3C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE88BA5E4BE9DE69687E6A1A3E59CB0E59D80EFBC9A266E6273703B3C2F7370616E3E3C6120687265663D22687474703A2F2F646F632E72756F79692E76697022207461726765743D225F626C616E6B223E687474703A2F2F646F632E72756F79692E7669703C2F613E3C62723E3C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE6BC94E7A4BAE59CB0E59D80E38090E4B88DE58886E7A6BBE78988E38091EFBC9A266E6273703B3C2F7370616E3E3C6120687265663D22687474703A2F2F64656D6F2E72756F79692E76697022207461726765743D225F626C616E6B223E687474703A2F2F64656D6F2E72756F79692E7669703C2F613E3C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE6BC94E7A4BAE59CB0E59D80E38090E58886E7A6BBE78988E69CACE38091EFBC9A266E6273703B3C2F7370616E3E3C6120687265663D22687474703A2F2F7675652E72756F79692E76697022207461726765743D225F626C616E6B223E687474703A2F2F7675652E72756F79692E7669703C2F613E3C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE6BC94E7A4BAE59CB0E59D80E38090E5BEAEE69C8DE58AA1E78988E38091EFBC9A266E6273703B3C2F7370616E3E3C6120687265663D22687474703A2F2F636C6F75642E72756F79692E76697022207461726765743D225F626C616E6B223E687474703A2F2F636C6F75642E72756F79692E7669703C2F613E3C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE6BC94E7A4BAE59CB0E59D80E38090E7A7BBE58AA8E7ABAFE78988E38091EFBC9A266E6273703B3C2F7370616E3E3C6120687265663D22687474703A2F2F68352E72756F79692E76697022207461726765743D225F626C616E6B223E687474703A2F2F68352E72756F79692E7669703C2F613E3C2F703E3C703E3C6272207374796C653D22636F6C6F723A207267622834382C2034392C203531293B20666F6E742D66616D696C793A202671756F743B48656C766574696361204E6575652671756F743B2C2048656C7665746963612C20417269616C2C2073616E732D73657269663B20666F6E742D73697A653A20313270783B223E3C2F703E,'0','admin','2025-09-18 13:44:00','',NULL,'管理员');

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50)   DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200)   DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10)   DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50)   DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50)   DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255)   DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128)   DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255)   DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000)   DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000)   DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000)   DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `sys_oper_log_idx_sys_oper_log_bt` (`business_type`),
  KEY `sys_oper_log_idx_sys_oper_log_s` (`status`),
  KEY `sys_oper_log_idx_sys_oper_log_ot` (`oper_time`)
);

--
-- Dumping data for table `sys_oper_log`
--

INSERT INTO `sys_oper_log` VALUES (100,'重置密码',2,'com.ruoyi.web.controller.system.SysProfileController.resetPwd()','POST',1,'admin','研发部门','/system/user/profile/resetPwd','127.0.0.1','内网IP','{\"userId\":[\"1\"],\"loginName\":[\"admin\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2025-10-20 23:33:17',158),(101,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-20 23:52:45',12),(102,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-20 23:55:19',3),(103,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-20 23:55:22',5),(104,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-21 00:07:22',3),(105,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-21 00:11:15',4),(106,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-21 00:11:16',3),(107,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-21 00:11:44',3),(108,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-21 00:11:49',3),(109,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-22 23:03:44',21),(110,'活动管理',2,'com.ruoyi.system.controller.XrayActivityController.startActivity()','POST',1,'admin','研发部门','/system/activity/startActivity','127.0.0.1','内网IP','','{\"msg\":\"开启活动成功!\",\"code\":0}',0,NULL,'2025-10-22 23:04:00',107),(111,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-22 23:56:37',52),(112,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{}}',0,NULL,'2025-10-23 23:15:45',175),(113,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','127.0.0.1','内网IP','',NULL,1,'\r\n### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column ''createdAt'' in ''field list''\r\n### The error may exist in file [D:\\RuoYi\\ruoyi-system\\target\\classes\\mapper\\system\\VpnLinesMapper.xml]\r\n### The error may involve com.ruoyi.system.mapper.VpnLinesMapper.selectVpnLinesList-Inline\r\n### The error occurred while setting parameters\r\n### SQL: select id, name, region, `type`, bandwidth, remaining_traffic, total_traffic, connection_cnt, max_connection_cnt, ping_delay, ping_offset, `ip`, `port`, `keyword`, `status`, `protocol`, createdAt, updatedAt from vpn_lines                WHERE  name like concat(''%'', ?, ''%'')\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column ''createdAt'' in ''field list''\n; bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: Unknown column ''createdAt'' in ''field list''','2025-10-23 23:16:21',233),(114,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','127.0.0.1','内网IP','',NULL,1,'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named ''created_at'' in ''class com.ruoyi.system.domain.VpnLines''','2025-10-23 23:20:38',84),(115,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','127.0.0.1','内网IP','','{\"msg\":\"success\",\"code\":0}',0,NULL,'2025-10-23 23:23:19',398),(116,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2025-11-10 23:00:41',21),(117,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2025-11-10 23:00:44',6),(118,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','116.2.2.6','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-06-14 22:01:00',100),(119,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','8.139.192.246','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-04 09:42:45',152),(120,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 20:00:35',52),(121,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 20:02:57',16),(122,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 20:03:01',23),(123,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 20:07:14',14),(124,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 20:07:33',24),(125,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 20:08:13',6),(126,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 20:47:31',20),(127,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 20:47:33',10),(128,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 21:47:28',5),(129,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 21:48:23',7),(130,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-06 22:34:30',4),(131,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-07 08:58:01',14),(132,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-07 13:53:51',10),(133,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-07 13:53:56',3),(134,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-07 14:00:09',8),(135,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-07 14:00:30',7),(136,'VPN线路信息',2,'com.ruoyi.system.controller.VpnLinesController.editSave()','POST',1,'admin','研发部门','/system/lines/edit','112.96.202.86','XX XX','{\"id\":[\"1\"],\"pingOffset\":[\"50\"],\"status\":[\"offline\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-07 14:00:51',15),(137,'VPN线路信息',2,'com.ruoyi.system.controller.VpnLinesController.editSave()','POST',1,'admin','研发部门','/system/lines/edit','112.96.202.86','XX XX','{\"id\":[\"1\"],\"pingOffset\":[\"50\"],\"status\":[\"offline\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-07 14:00:52',5),(138,'VPN线路信息',2,'com.ruoyi.system.controller.VpnLinesController.editSave()','POST',1,'admin','研发部门','/system/lines/edit','112.96.202.86','XX XX','{\"id\":[\"2\"],\"pingOffset\":[\"100\"],\"status\":[\"offline\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-07 14:00:53',4),(139,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-07 14:03:39',5),(140,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','116.2.24.27','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":8}}',0,NULL,'2026-09-07 15:05:12',293),(141,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','116.2.24.27','XX XX','','{\"msg\":\"success\",\"code\":0}',0,NULL,'2026-09-07 15:06:08',381),(142,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','116.2.24.27','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":11}}',0,NULL,'2026-09-07 15:08:29',24),(143,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','116.2.24.27','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":11}}',0,NULL,'2026-09-07 15:10:49',31),(144,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','112.96.202.86','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":11}}',0,NULL,'2026-09-07 15:19:47',12),(145,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','116.2.24.27','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":11}}',0,NULL,'2026-09-08 09:58:28',76),(146,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','8.139.192.246','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":11}}',0,NULL,'2026-09-08 11:33:46',383),(147,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','8.139.192.246','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":11}}',0,NULL,'2026-09-08 11:34:01',47),(148,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','8.139.192.246','XX XX','',NULL,1,'第 1 行: 配置字段必须是以 vless:// 开头的链接','2026-09-08 11:45:32',35),(149,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','8.139.192.246','XX XX','',NULL,1,'第 1 行: 配置字段必须是以 vless:// 开头的链接','2026-09-08 11:45:41',3),(150,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','8.139.192.246','XX XX','','{\"msg\":\"success\",\"code\":0}',0,NULL,'2026-09-08 11:46:43',2242),(151,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','8.139.192.246','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":11}}',0,NULL,'2026-09-08 11:51:46',325),(152,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','8.139.192.246','XX XX','{\"ids\":[\"1\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 11:55:43',237),(153,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','8.139.192.246','XX XX','{\"ids\":[\"2\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 11:55:47',52),(154,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','8.139.192.246','XX XX','{\"ids\":[\"3\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 11:55:51',20),(155,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','8.139.192.246','XX XX','{\"ids\":[\"4\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 11:55:55',24),(156,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','8.139.192.246','XX XX','{\"ids\":[\"5\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 11:55:58',28),(157,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','8.139.192.246','XX XX','{\"ids\":[\"6\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 11:56:01',24),(158,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','8.139.192.246','XX XX','{\"ids\":[\"7\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 11:56:05',26),(159,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','8.139.192.246','XX XX','{\"ids\":[\"8\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 11:56:09',23),(160,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','8.139.192.246','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"offline\":3}}',0,NULL,'2026-09-08 12:00:10',56),(161,'VPN线路信息',2,'com.ruoyi.system.controller.VpnLinesController.editSave()','POST',1,'admin','研发部门','/system/lines/edit','8.139.192.246','XX XX','{\"id\":[\"9\"],\"pingOffset\":[\"5\"],\"status\":[\"online\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 12:01:19',213),(162,'VPN线路信息',2,'com.ruoyi.system.controller.VpnLinesController.editSave()','POST',1,'admin','研发部门','/system/lines/edit','8.139.192.246','XX XX','{\"id\":[\"10\"],\"pingOffset\":[\"45\"],\"status\":[\"online\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 12:01:31',20),(163,'VPN线路信息',2,'com.ruoyi.system.controller.VpnLinesController.editSave()','POST',1,'admin','研发部门','/system/lines/edit','8.139.192.246','XX XX','{\"id\":[\"11\"],\"pingOffset\":[\"120\"],\"status\":[\"maintenance\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 12:01:35',26),(164,'VPN线路信息',2,'com.ruoyi.system.controller.VpnLinesController.editSave()','POST',1,'admin','研发部门','/system/lines/edit','112.96.202.86','XX XX','{\"id\":[\"11\"],\"pingOffset\":[\"120\"],\"status\":[\"maintenance\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 12:41:00',13),(165,'VPN线路信息',2,'com.ruoyi.system.controller.VpnLinesController.editSave()','POST',1,'admin','研发部门','/system/lines/edit','112.96.202.86','XX XX','{\"id\":[\"11\"],\"pingOffset\":[\"120\"],\"status\":[\"maintenance\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 12:41:02',15),(166,'VPN线路信息',13,'com.ruoyi.system.controller.VpnLinesController.countLinesByStatus()','GET',1,'admin','研发部门','/system/lines/stats','39.144.59.158','XX XX','','{\"msg\":\"操作成功\",\"code\":0,\"data\":{\"online\":2,\"maintenance\":1}}',0,NULL,'2026-09-08 12:47:09',33),(167,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','39.144.59.158','XX XX','{\"ids\":[\"9\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 12:48:21',18),(168,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','39.144.59.158','XX XX','',NULL,1,'第 1 行: 配置字段必须是以 vless:// 开头的链接','2026-09-08 12:48:40',45),(169,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','39.144.59.158','XX XX','{\"ids\":[\"10\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 12:50:08',25),(170,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','39.144.59.158','XX XX','{\"ids\":[\"11\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 12:50:10',12),(171,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','39.144.59.158','XX XX','',NULL,1,'第 1 行: 配置字段必须是以 vless:// 开头的链接','2026-09-08 12:58:45',4),(172,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','39.144.59.158','XX XX','',NULL,1,'第 1 行: 配置字段必须是以 vless:// 开头的链接','2026-09-08 12:58:48',3),(173,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','39.144.59.158','XX XX','',NULL,1,'第 1 行: 配置字段必须是以 vless:// 开头的链接','2026-09-08 13:00:14',4),(174,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','8.139.192.246','XX XX','','{\"msg\":\"success\",\"code\":0}',0,NULL,'2026-09-08 13:02:43',2855),(175,'VPN线路信息',3,'com.ruoyi.system.controller.VpnLinesController.remove()','POST',1,'admin','研发部门','/system/lines/remove','112.96.202.86','XX XX','{\"ids\":[\"12\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 13:08:24',14),(176,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','112.96.202.86','XX XX','','{\"msg\":\"success\",\"code\":0}',0,NULL,'2026-09-08 13:10:29',2020),(177,'VPN线路信息',6,'com.ruoyi.system.controller.VpnLinesController.importVpnLines()','POST',1,'admin','研发部门','/system/lines/import','112.96.202.86','XX XX','','{\"msg\":\"success\",\"code\":0}',0,NULL,'2026-09-08 13:10:30',2538),(178,'VPN线路信息',2,'com.ruoyi.system.controller.VpnLinesController.editSave()','POST',1,'admin','研发部门','/system/lines/edit','112.96.202.86','XX XX','{\"id\":[\"13\"],\"pingOffset\":[\"120\"],\"status\":[\"online\"]}','{\"msg\":\"操作成功\",\"code\":0}',0,NULL,'2026-09-08 14:25:05',46);

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64)   NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50)   NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1)   NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500)   DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
);

--
-- Dumping data for table `sys_post`
--

INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,'0','admin','2025-09-18 13:43:59','',NULL,''),(2,'se','项目经理',2,'0','admin','2025-09-18 13:43:59','',NULL,''),(3,'hr','人力资源',3,'0','admin','2025-09-18 13:43:59','',NULL,''),(4,'user','普通员工',4,'0','admin','2025-09-18 13:43:59','',NULL,'');

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30)   NOT NULL COMMENT '角色名称',
  `role_key` varchar(100)   NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1)   DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `status` char(1)   NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1)   DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500)   DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
);

--
-- Dumping data for table `sys_role`
--

INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1','0','0','admin','2025-09-18 13:43:59','',NULL,'超级管理员'),(2,'普通角色','common',2,'2','0','0','admin','2025-09-18 13:43:59','',NULL,'普通角色'),(100,'管理员','admin-slave',3,'3','0','0','admin','2025-09-18 13:43:59','',NULL,'管理员'),(101,'客服','support',4,'4','0','0','admin','2025-09-18 13:43:59','',NULL,'客服'),(102,'经销商','reseller',5,'5','0','0','admin','2025-09-18 13:43:59','',NULL,'经销商');

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
);

--
-- Dumping data for table `sys_role_dept`
--

INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
);

--
-- Dumping data for table `sys_role_menu`
--

INSERT INTO `sys_role_menu` VALUES (2,1),(2,2),(2,3),(2,4),(2,100),(2,101),(2,102),(2,103),(2,104),(2,105),(2,106),(2,107),(2,108),(2,109),(2,110),(2,111),(2,112),(2,113),(2,114),(2,115),(2,116),(2,500),(2,501),(2,1000),(2,1001),(2,1002),(2,1003),(2,1004),(2,1005),(2,1006),(2,1007),(2,1008),(2,1009),(2,1010),(2,1011),(2,1012),(2,1013),(2,1014),(2,1015),(2,1016),(2,1017),(2,1018),(2,1019),(2,1020),(2,1021),(2,1022),(2,1023),(2,1024),(2,1025),(2,1026),(2,1027),(2,1028),(2,1029),(2,1030),(2,1031),(2,1032),(2,1033),(2,1034),(2,1035),(2,1036),(2,1037),(2,1038),(2,1039),(2,1040),(2,1041),(2,1042),(2,1043),(2,1044),(2,1045),(2,1046),(2,1047),(2,1048),(2,1049),(2,1050),(2,1051),(2,1052),(2,1053),(2,1054),(2,1055),(2,1056),(2,1057),(2,1058),(2,1059),(2,1060),(2,1061),(2,2000),(2,2004),(2,2006),(100,2000),(100,2001),(100,2002),(100,2003),(100,2004),(100,2005),(100,2006),(100,2007),(100,2008),(100,2009),(100,2010),(100,2011),(100,2012),(100,2013),(100,2014),(101,2000),(101,2001),(101,2002),(101,2003),(101,2005),(101,2006),(102,2000),(102,2001),(102,2002),(102,2003),(102,2005);

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `login_name` varchar(30)   NOT NULL COMMENT '登录账号',
  `user_name` varchar(30)   DEFAULT '' COMMENT '用户昵称',
  `user_type` varchar(2)   DEFAULT '00' COMMENT '用户类型（00系统用户 01注册用户）',
  `email` varchar(50)   DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11)   DEFAULT '' COMMENT '手机号码',
  `sex` char(1)   DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100)   DEFAULT '' COMMENT '头像路径',
  `password` varchar(50)   DEFAULT '' COMMENT '密码',
  `salt` varchar(20)   DEFAULT '' COMMENT '盐加密',
  `status` char(1)   DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1)   DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128)   DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64)   DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64)   DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500)   DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
);

--
-- Dumping data for table `sys_user`
--

INSERT INTO `sys_user` VALUES 
(1,103,'admin','超级管理员','00','admin@luxwap.com','15888888888','1','','5c2c8ebc2b8a5b53fbde82902416455d','75b7a9','0','0','127.0.0.1','2026-09-09 09:33:12','2025-10-20 23:33:17','admin','2025-09-18 13:43:59','','2026-09-09 09:33:12','超级管理员'),
(2,105,'common','普通员工','00','common@luxwap.com','15666666666','1','','f38983fe3acea6954250682b5005359a','111111','0','0','127.0.0.1','2026-09-09 09:33:12','2025-10-20 23:33:17','admin','2025-09-18 13:43:59','','2026-09-09 09:33:12','普通角色'),
(100,103,'manager','副管理员','00','manager@luxwap.com','15888888880','1','','276a73e2308d54ea641a90ab61817e59','111111','0','0','127.0.0.1','2026-09-09 09:33:12','2025-10-20 23:33:17','admin','2025-09-18 13:43:59','','2026-09-09 09:33:12','管理员'),
(101,104,'support','客服专员','00','support@luxwap.com','15888888881','0','','a38ec113556a809e8ef24f07918e76ca','111111','0','0','127.0.0.1','2026-09-09 09:33:12','2025-10-20 23:33:17','admin','2025-09-18 13:43:59','','2026-09-09 09:33:12','客服'),
(102,104,'reseller','合作经销商','00','reseller@luxwap.com','15888888882','1','','5e8b026a2b4e04b364adb0eab354b638','111111','0','0','127.0.0.1','2026-09-09 09:33:12','2025-10-20 23:33:17','admin','2025-09-18 13:43:59','','2026-09-09 09:33:12','经销商'),
(103,104,'kefu','客服别名','00','kefu@luxwap.com','15888888883','0','','7ae56702d4272e545396928050971ae9','e3ef79','0','0','127.0.0.1','2026-09-09 09:33:12','2025-10-20 23:33:17','admin','2025-09-18 13:43:59','','2026-09-09 09:33:12','客服别名'),
(104,103,'abcv','管理员别名','00','abcv@luxwap.com','15888888884','1','','39236a6df8cc1238c566378cc7911198','5bfa96','0','0','127.0.0.1','2026-09-09 09:33:12','2025-10-20 23:33:17','admin','2025-09-18 13:43:59','','2026-09-09 09:33:12','管理员别名');

--
-- Table structure for table `sys_user_online`
--

DROP TABLE IF EXISTS `sys_user_online`;
CREATE TABLE `sys_user_online` (
  `sessionId` varchar(50)   NOT NULL DEFAULT '' COMMENT '用户会话id',
  `login_name` varchar(50)   DEFAULT '' COMMENT '登录账号',
  `dept_name` varchar(50)   DEFAULT '' COMMENT '部门名称',
  `ipaddr` varchar(128)   DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255)   DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50)   DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50)   DEFAULT '' COMMENT '操作系统',
  `status` varchar(10)   DEFAULT '' COMMENT '在线状态on_line在线off_line离线',
  `start_timestamp` datetime DEFAULT NULL COMMENT 'session创建时间',
  `last_access_time` datetime DEFAULT NULL COMMENT 'session最后访问时间',
  `expire_time` int DEFAULT '0' COMMENT '超时时间，单位为分钟',
  PRIMARY KEY (`sessionId`)
);

--
-- Dumping data for table `sys_user_online`
--


--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
);

--
-- Dumping data for table `sys_user_post`
--

INSERT INTO `sys_user_post` VALUES (1,1),(2,2);

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
);

--
-- Dumping data for table `sys_user_role`
--

INSERT INTO `sys_user_role` VALUES (1,1),(2,2),(100,100),(101,101),(102,102),(103,101),(104,100);

--
-- Table structure for table `vpn_lines`
--

DROP TABLE IF EXISTS `vpn_lines`;
CREATE TABLE `vpn_lines` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(128) DEFAULT NULL COMMENT '线路名称',
  `region` varchar(32) DEFAULT NULL COMMENT '地区',
  `type` varchar(32) DEFAULT NULL COMMENT '类型',
  `bandwidth` int DEFAULT NULL COMMENT '带宽',
  `remaining_traffic` bigint DEFAULT NULL COMMENT '剩余流量（字节）',
  `total_traffic` bigint DEFAULT NULL COMMENT '总流量（字节）',
  `connection_cnt` int DEFAULT NULL COMMENT '连接数',
  `max_connection_cnt` int DEFAULT NULL COMMENT '最大连接数',
  `ping_delay` int DEFAULT NULL COMMENT 'ping延迟（毫秒）',
  `ping_offset` int DEFAULT NULL COMMENT 'ping偏移',
  `ip` varchar(45) DEFAULT NULL COMMENT '线路服务器IP',
  `port` int DEFAULT NULL COMMENT '线路服务器端口',
  `keyword` varchar(255) DEFAULT NULL COMMENT '搜索关键词',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `protocol` varchar(50) DEFAULT NULL COMMENT '协议',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
);

--
-- Dumping data for table `vpn_lines`
--

INSERT INTO `vpn_lines` VALUES (13,'洛杉矶BGP01','north_america','wireless',1000,2000000,2000000,0,1000,0,120,'203.0.0.1',80,'us-la','online','vless','2026-09-08 05:10:28','2026-09-08 06:25:05');

--
-- Table structure for table `vpn_lines_config`
--

DROP TABLE IF EXISTS `vpn_lines_config`;
CREATE TABLE `vpn_lines_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `vpn_line_id` bigint DEFAULT NULL COMMENT '关联的VPN线路ID',
  `config_json` longtext COMMENT 'Xray配置文件内容',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `vpn_lines_config_idx_vpn_line_id` (`vpn_line_id`)
);

--
-- Dumping data for table `vpn_lines_config`
--

INSERT INTO `vpn_lines_config` VALUES (1,1,'{\"log\":{\"loglevel\":\"info\"},\"inbounds\":[{\"listen\":\"0.0.0.0\",\"port\":443,\"protocol\":\"vless\",\"settings\":{\"clients\":[{\"id\":\"60fbe84f-f3c2-4307-83d4-44ee2bf798d3\",\"flow\":\"xtls-rprx-vision\"}],\"decryption\":\"none\"},\"streamSettings\":{\"network\":\"tcp\",\"security\":\"reality\",\"realitySettings\":{\"show\":false,\"dest\":\"www.tesla.com:443\",\"xver\":0,\"serverNames\":[\"www.tesla.com\"],\"privateKey\":\"SQVSn1RxmKEfL6rr-uDC8QgXUHPkLIJuYCDigca8LhA\",\"shortIds\":[\"8A48A469\"],\"settings\":{\"fingerprint\":\"chrome\"}}}}],\"outbounds\":[{\"protocol\":\"freedom\"}]}','2025-10-23 15:23:19','2025-10-23 15:26:14'),(2,2,'{\"log\":{\"loglevel\":\"info\"},\"inbounds\":[{\"listen\":\"0.0.0.0\",\"port\":443,\"protocol\":\"vless\",\"settings\":{\"clients\":[{\"id\":\"60fbe84f-f3c2-4307-83d4-44ee2bf798d3\",\"flow\":\"xtls-rprx-vision\"}],\"decryption\":\"none\"},\"streamSettings\":{\"network\":\"tcp\",\"security\":\"reality\",\"realitySettings\":{\"show\":false,\"dest\":\"www.tesla.com:443\",\"xver\":0,\"serverNames\":[\"www.tesla.com\"],\"privateKey\":\"SQVSn1RxmKEfL6rr-uDC8QgXUHPkLIJuYCDigca8LhA\",\"shortIds\":[\"8A48A469\"],\"settings\":{\"fingerprint\":\"chrome\"}}}}],\"outbounds\":[{\"protocol\":\"freedom\"}]}','2025-10-23 15:23:19','2025-10-23 15:26:14'),(3,3,'{\"log\":{\"loglevel\":\"info\"},\"inbounds\":[{\"listen\":\"0.0.0.0\",\"port\":443,\"protocol\":\"vless\",\"settings\":{\"clients\":[{\"id\":\"60fbe84f-f3c2-4307-83d4-44ee2bf798d3\",\"flow\":\"xtls-rprx-vision\"}],\"decryption\":\"none\"},\"streamSettings\":{\"network\":\"tcp\",\"security\":\"reality\",\"realitySettings\":{\"show\":false,\"dest\":\"www.tesla.com:443\",\"xver\":0,\"serverNames\":[\"www.tesla.com\"],\"privateKey\":\"SQVSn1RxmKEfL6rr-uDC8QgXUHPkLIJuYCDigca8LhA\",\"shortIds\":[\"8A48A469\"],\"settings\":{\"fingerprint\":\"chrome\"}}}}],\"outbounds\":[{\"protocol\":\"freedom\"}]}','2025-10-23 15:23:19','2025-10-23 15:26:14'),(4,4,'{\"log\":{\"loglevel\":\"info\"},\"inbounds\":[{\"listen\":\"0.0.0.0\",\"port\":443,\"protocol\":\"vless\",\"settings\":{\"clients\":[{\"id\":\"60fbe84f-f3c2-4307-83d4-44ee2bf798d3\",\"flow\":\"xtls-rprx-vision\"}],\"decryption\":\"none\"},\"streamSettings\":{\"network\":\"tcp\",\"security\":\"reality\",\"realitySettings\":{\"show\":false,\"dest\":\"www.tesla.com:443\",\"xver\":0,\"serverNames\":[\"www.tesla.com\"],\"privateKey\":\"SQVSn1RxmKEfL6rr-uDC8QgXUHPkLIJuYCDigca8LhA\",\"shortIds\":[\"8A48A469\"],\"settings\":{\"fingerprint\":\"chrome\"}}}}],\"outbounds\":[{\"protocol\":\"freedom\"}]}','2025-10-23 15:23:19','2025-10-23 15:26:14'),(5,5,'{\"log\":{\"loglevel\":\"info\"},\"inbounds\":[{\"listen\":\"0.0.0.0\",\"port\":443,\"protocol\":\"vless\",\"settings\":{\"clients\":[{\"id\":\"60fbe84f-f3c2-4307-83d4-44ee2bf798d3\",\"flow\":\"xtls-rprx-vision\"}],\"decryption\":\"none\"},\"streamSettings\":{\"network\":\"tcp\",\"security\":\"reality\",\"realitySettings\":{\"show\":false,\"dest\":\"www.tesla.com:443\",\"xver\":0,\"serverNames\":[\"www.tesla.com\"],\"privateKey\":\"SQVSn1RxmKEfL6rr-uDC8QgXUHPkLIJuYCDigca8LhA\",\"shortIds\":[\"8A48A469\"],\"settings\":{\"fingerprint\":\"chrome\"}}}}],\"outbounds\":[{\"protocol\":\"freedom\"}]}','2025-10-23 15:23:19','2025-10-23 15:26:14'),(6,6,'{\"log\":{\"loglevel\":\"info\"},\"inbounds\":[{\"listen\":\"0.0.0.0\",\"port\":443,\"protocol\":\"vless\",\"settings\":{\"clients\":[{\"id\":\"60fbe84f-f3c2-4307-83d4-44ee2bf798d3\",\"flow\":\"xtls-rprx-vision\"}],\"decryption\":\"none\"},\"streamSettings\":{\"network\":\"tcp\",\"security\":\"reality\",\"realitySettings\":{\"show\":false,\"dest\":\"www.tesla.com:443\",\"xver\":0,\"serverNames\":[\"www.tesla.com\"],\"privateKey\":\"SQVSn1RxmKEfL6rr-uDC8QgXUHPkLIJuYCDigca8LhA\",\"shortIds\":[\"8A48A469\"],\"settings\":{\"fingerprint\":\"chrome\"}}}}],\"outbounds\":[{\"protocol\":\"freedom\"}]}','2025-10-23 15:23:19','2025-10-23 15:26:14'),(7,7,'{\"log\":{\"loglevel\":\"info\"},\"inbounds\":[{\"listen\":\"0.0.0.0\",\"port\":443,\"protocol\":\"vless\",\"settings\":{\"clients\":[{\"id\":\"60fbe84f-f3c2-4307-83d4-44ee2bf798d3\",\"flow\":\"xtls-rprx-vision\"}],\"decryption\":\"none\"},\"streamSettings\":{\"network\":\"tcp\",\"security\":\"reality\",\"realitySettings\":{\"show\":false,\"dest\":\"www.tesla.com:443\",\"xver\":0,\"serverNames\":[\"www.tesla.com\"],\"privateKey\":\"SQVSn1RxmKEfL6rr-uDC8QgXUHPkLIJuYCDigca8LhA\",\"shortIds\":[\"8A48A469\"],\"settings\":{\"fingerprint\":\"chrome\"}}}}],\"outbounds\":[{\"protocol\":\"freedom\"}]}','2025-10-23 15:23:19','2025-10-23 15:26:14'),(8,8,'{\"log\":{\"loglevel\":\"info\"},\"inbounds\":[{\"listen\":\"0.0.0.0\",\"port\":443,\"protocol\":\"vless\",\"settings\":{\"clients\":[{\"id\":\"60fbe84f-f3c2-4307-83d4-44ee2bf798d3\",\"flow\":\"xtls-rprx-vision\"}],\"decryption\":\"none\"},\"streamSettings\":{\"network\":\"tcp\",\"security\":\"reality\",\"realitySettings\":{\"show\":false,\"dest\":\"www.tesla.com:443\",\"xver\":0,\"serverNames\":[\"www.tesla.com\"],\"privateKey\":\"SQVSn1RxmKEfL6rr-uDC8QgXUHPkLIJuYCDigca8LhA\",\"shortIds\":[\"8A48A469\"],\"settings\":{\"fingerprint\":\"chrome\"}}}}],\"outbounds\":[{\"protocol\":\"freedom\"}]}','2025-10-23 15:23:19','2025-10-23 15:26:14'),(9,9,'{\"inbounds\":[{\"port\":443,\"protocol\":\"vless\",\"settings\":{\"clients\":[{\"id\":\"b831381d-6324-4d53-ad4f-8cda48b30811\",\"flow\":\"xtls-rprx-vision\"}],\"decryption\":\"none\"},\"streamSettings\":{\"network\":\"tcp\",\"security\":\"reality\",\"realitySettings\":{\"show\":false,\"dest\":\"www.apple.com:443\",\"xver\":0,\"serverNames\":[\"www.apple.com\"],\"privateKey\":\"YAh39X1Z_m6vK0S7y3PZ_mK9_X2y0W1Z_mK9_X2y0W1Z\",\"shortIds\":[\"6ba7b810\"],\"settings\":{\"fingerprint\":\"chrome\"}}}}]}','2026-09-07 07:06:08','2026-09-07 07:06:08'),(10,10,'{\"inbounds\":[{\"port\":8080,\"protocol\":\"vless\",\"settings\":{\"clients\":[{\"id\":\"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d\",\"flow\":\"\"}],\"decryption\":\"none\"},\"streamSettings\":{\"network\":\"tcp\",\"security\":\"none\"}}]}','2026-09-07 07:06:08','2026-09-07 07:06:08'),(11,11,'vless://dbc4a0d2-29da-4d00-8734-de14425c3309@103.94.185.18:443?encryption=none&fp=chrome&pbk=in1Xx9fLt8JnupY-qFXonhAvNxy_5o7CZiervtDSUig&security=reality&sid=c2fa336e84e275&sni=www.tesla.com&spx=%2FlMcoLHce6KjlApj&type=tcp#my_xray','2026-09-07 07:06:08','2026-09-08 03:46:43'),(12,12,'vless://dbc4a0d2-29da-4d00-8734-de14425c3309@103.94.185.18:443?encryption=none&fp=chrome&pbk=in1Xx9fLt8JnupY-qFXonhAvNxy_5o7CZiervtDSUig&security=reality&sid=c2fa336e84e275&sni=www.tesla.com&spx=%2FlMcoLHce6KjlApj&type=tcp#my_xray','2026-09-08 05:02:43','2026-09-08 05:02:43'),(13,13,'vless://dbc4a0d2-29da-4d00-8734-de14425c3309@103.94.185.18:443?encryption=none&fp=chrome&pbk=in1Xx9fLt8JnupY-qFXonhAvNxy_5o7CZiervtDSUig&security=reality&sid=c2fa336e84e275&sni=www.tesla.com&spx=%2FlMcoLHce6KjlApj&type=tcp#my_xray','2026-09-08 05:10:28','2026-09-08 05:10:28');

--
-- Table structure for table `xray_activity`
--

DROP TABLE IF EXISTS `xray_activity`;
CREATE TABLE `xray_activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `activity_config_id` bigint DEFAULT NULL COMMENT '关联活动配置表ID',
  `participants_cnt` bigint DEFAULT NULL COMMENT '参与人数',
  `moderators_cnt` bigint DEFAULT NULL COMMENT '调节人数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '活动创建时间，默认当前时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '活动修改时间，默认当前时间，更新时自动更改',
  PRIMARY KEY (`id`),
  KEY `xray_activity_idx_activity_config_id` (`activity_config_id`)
);

--
-- Dumping data for table `xray_activity`
--

INSERT INTO `xray_activity` VALUES (1,1,998,1,'2025-10-22 15:04:00','2025-10-29 13:11:55');

--
-- Table structure for table `xray_activity_config`
--

DROP TABLE IF EXISTS `xray_activity_config`;
CREATE TABLE `xray_activity_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `participants_cnt` bigint DEFAULT NULL COMMENT '参与人数',
  `moderators_cnt` bigint DEFAULT NULL COMMENT '调节人数',
  `status` bigint DEFAULT NULL COMMENT '活动状态：0-终止，1-开始',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间，默认当前时间，更新时自动更改',
  PRIMARY KEY (`id`)
);

--
-- Dumping data for table `xray_activity_config`
--

INSERT INTO `xray_activity_config` VALUES (1,1000,1,1,'2025-10-22 15:03:14','2025-10-22 15:04:00');

--
-- Table structure for table `xray_activity_participants`
--

DROP TABLE IF EXISTS `xray_activity_participants`;
CREATE TABLE `xray_activity_participants` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `registration_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间，默认当前时间',
  `user_email` varchar(128) DEFAULT NULL COMMENT '用户Email',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
  `member` tinyint(1) NOT NULL DEFAULT '0' COMMENT '会员：0-非会员，1-会员',
  `expiration` date DEFAULT NULL COMMENT '有效期，会员有效期',
  `rank` bigint DEFAULT NULL COMMENT '名次，活动中的名次',
  `type` varchar(64) DEFAULT NULL COMMENT '用户类型，如团队、部门等',
  `activity_id` bigint DEFAULT NULL COMMENT '关联活动ID',
  `audit_link` varchar(256) DEFAULT NULL COMMENT '审核链接',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间，默认当前时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间，自动更新',
  PRIMARY KEY (`id`),
  KEY `xray_activity_participants_idx_user_id` (`user_id`),
  KEY `xray_activity_participants_idx_activity_id` (`activity_id`)
);

--
-- Dumping data for table `xray_activity_participants`
--

INSERT INTO `xray_activity_participants` VALUES (1,1,'2025-10-22 15:52:23','test7@example.com','test1',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(2,1,'2025-10-22 15:52:23','test7@example.com','test2',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(3,1,'2025-10-22 15:52:23','test7@example.com','test3',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(4,1,'2025-10-22 15:52:23','test7@example.com','test4',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(5,1,'2025-10-22 15:52:23','test7@example.com','test5',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(6,1,'2025-10-22 15:52:23','test7@example.com','test6',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(7,1,'2025-10-22 15:52:23','test7@example.com','test7',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(8,1,'2025-10-22 15:52:23','test7@example.com','test8',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(9,1,'2025-10-22 15:52:23','test7@example.com','test9',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(10,1,'2025-10-22 15:52:23','test7@example.com','test10',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(11,1,'2025-10-22 15:52:23','test7@example.com','test11',1,'2025-10-24',1,'outer',1,'https://www.baidu.com','2025-10-22 15:52:23','2025-10-22 16:29:35'),(12,3,'2025-10-29 13:11:55','1871814749@qq.com','1871814749@qq.com',1,'2025-11-01',1,'inner',1,'https://www.baidu.com','2025-10-29 13:11:55','2025-10-29 13:12:25');

--
-- Table structure for table `xray_distributors`
--

DROP TABLE IF EXISTS `xray_distributors`;
CREATE TABLE `xray_distributors` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分销商ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `email` varchar(128) DEFAULT NULL COMMENT '分销商Email',
  `contact_person` varchar(64) DEFAULT NULL COMMENT '联系人',
  `level` varchar(32) DEFAULT NULL COMMENT '等级（钻石/黄金/白银/普通）',
  `monthly_sales` decimal(15,2) DEFAULT NULL COMMENT '月销售额',
  `commission_rate` bigint DEFAULT NULL COMMENT '佣金比例（%）',
  `first_charge_bonus` bigint DEFAULT NULL COMMENT '首充返佣（%）',
  `paypal_account` varchar(128) DEFAULT NULL COMMENT '收款Paypal',
  `binding_code` varchar(64) DEFAULT NULL COMMENT '绑定码',
  `exclusive_suffix` varchar(64) DEFAULT NULL COMMENT '专属后缀',
  `region` varchar(32) DEFAULT NULL COMMENT '地区（中国/台湾/越南/俄罗斯/韩国/美国/日本）',
  `status` varchar(32) DEFAULT NULL COMMENT '状态（pending:初始化，approved:审批通过，rejected:审批不通过）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `xray_distributors_idx_user_id` (`user_id`),
  KEY `xray_distributors_idx_email` (`email`)
);

--
-- Dumping data for table `xray_distributors`
--


--
-- Table structure for table `xray_distributors_config`
--

DROP TABLE IF EXISTS `xray_distributors_config`;
CREATE TABLE `xray_distributors_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `commission_rate` bigint DEFAULT NULL COMMENT '佣金比例（%）',
  `first_charge_bonus` bigint DEFAULT NULL COMMENT '首充返佣（%）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
);

--
-- Dumping data for table `xray_distributors_config`
--


--
-- Table structure for table `xray_email_verification`
--

DROP TABLE IF EXISTS `xray_email_verification`;
CREATE TABLE `xray_email_verification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `email` varchar(255) NOT NULL COMMENT '邮箱地址',
  `code` varchar(64) NOT NULL COMMENT '验证码',
  `expire_at` timestamp NOT NULL COMMENT '过期时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
);

--
-- Dumping data for table `xray_email_verification`
--

INSERT INTO `xray_email_verification` VALUES (1,'lilibestcoder@163.com','788355','2025-10-27 16:39:58','2025-10-27 16:30:00','2025-10-27 16:30:00'),(2,'lilibestcoder@163.com','278107','2025-10-27 16:43:25','2025-10-27 16:33:27','2025-10-27 16:33:27'),(3,'lilibestcoder@163.com','623595','2025-10-27 16:45:30','2025-10-27 16:35:31','2025-10-27 16:35:31'),(4,'lilibestcoder@163.com','381990','2025-10-27 16:53:43','2025-10-27 16:43:43','2025-10-27 16:43:43'),(5,'1871814749@qq.com','399630','2025-10-28 10:58:13','2025-10-28 10:48:13','2025-10-28 10:48:13'),(6,'1871814749@qq.com','246262','2025-10-28 10:58:30','2025-10-28 10:48:30','2025-10-28 10:48:30'),(7,'1871814749@qq.com','607689','2025-10-28 14:32:31','2025-10-28 14:22:30','2025-10-28 14:22:30'),(8,'1871814749@qq.com','564974','2025-10-28 14:32:36','2025-10-28 14:22:36','2025-10-28 14:22:36'),(9,'1871814749@qq.com','674521','2025-10-28 14:35:36','2025-10-28 14:25:36','2025-10-28 14:25:36'),(10,'','112602','2025-10-28 15:51:54','2025-10-28 15:41:54','2025-10-28 15:41:54'),(11,'','564321','2025-10-28 15:52:27','2025-10-28 15:42:26','2025-10-28 15:42:26'),(12,'1871814749@qq.com','926163','2025-10-28 16:08:32','2025-10-28 15:58:32','2025-10-28 15:58:32'),(13,'1871814749@qq.com','489452','2025-10-28 16:21:52','2025-10-28 16:11:51','2025-10-28 16:11:51'),(14,'1871814749@qq.com','727137','2025-10-28 16:36:45','2025-10-28 16:26:45','2025-10-28 16:26:45'),(15,'1871814749@qq.com','426234','2025-10-28 16:38:33','2025-10-28 16:28:32','2025-10-28 16:28:32'),(16,'lilibestcoder@163.com','339450','2025-10-28 17:31:38','2025-10-28 17:21:37','2025-10-28 17:21:37'),(17,'1871814749@qq.com','775452','2025-10-28 18:20:18','2025-10-28 18:10:17','2025-10-28 18:10:17'),(18,'lilibestcoder@163.com','380053','2025-10-29 07:03:18','2025-10-29 06:53:17','2025-10-29 06:53:17'),(19,'1871814749@qq.com','931585','2025-10-29 08:22:33','2025-10-29 08:12:32','2025-10-29 08:12:32'),(20,'lilibestcoder@163.com','611151','2025-10-29 12:46:42','2025-10-29 12:36:42','2025-10-29 12:36:42'),(21,'1871814749@qq.com','808530','2025-10-29 12:53:28','2025-10-29 12:43:27','2025-10-29 12:43:27'),(22,'lilibestcoder@163.com','449616','2025-10-29 13:55:48','2025-10-29 13:45:47','2025-10-29 13:45:47'),(23,'1871814749','978412','2025-10-29 15:39:02','2025-10-29 15:29:01','2025-10-29 15:29:01'),(24,'1871814749','868835','2025-10-29 15:39:05','2025-10-29 15:29:05','2025-10-29 15:29:05'),(25,'1871814749@qq.com','220329','2025-10-29 16:20:41','2025-10-29 16:10:40','2025-10-29 16:10:40'),(26,'1871814749@qq.com','724222','2025-10-29 16:26:58','2025-10-29 16:16:58','2025-10-29 16:16:58'),(27,'1871814749@qq.com','149796','2025-10-29 16:28:50','2025-10-29 16:18:50','2025-10-29 16:18:50'),(28,'1871814749@qq.com','108847','2025-10-29 16:30:25','2025-10-29 16:20:25','2025-10-29 16:20:25'),(29,'1871814749@qq.com','337581','2025-10-29 16:33:27','2025-10-29 16:23:27','2025-10-29 16:23:27'),(30,'1871814749@qq.com','555408','2025-10-29 16:34:04','2025-10-29 16:24:04','2025-10-29 16:24:04'),(31,'1871814749@qq.com','523785','2025-10-29 16:34:24','2025-10-29 16:24:23','2025-10-29 16:24:23'),(32,'lilibestcoder@163.com','514365','2025-10-29 16:51:04','2025-10-29 16:41:03','2025-10-29 16:41:03'),(33,'lilibestcoder@163.com','813440','2025-10-29 16:54:12','2025-10-29 16:44:12','2025-10-29 16:44:12'),(34,'1871814749@qq.com','483443','2025-10-30 03:02:29','2025-10-30 02:52:28','2025-10-30 02:52:28');

--
-- Table structure for table `xray_inner_users`
--

DROP TABLE IF EXISTS `xray_inner_users`;
CREATE TABLE `xray_inner_users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(128) DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) DEFAULT NULL COMMENT '密码',
  `type` varchar(32) DEFAULT NULL COMMENT '类型：admin/slave/support',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标记 0=未删除, 1=已删除',
  `status` tinyint DEFAULT '1' COMMENT '状态 0=禁用, 1=启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
);

--
-- Dumping data for table `xray_inner_users`
--

INSERT INTO `xray_inner_users` VALUES (1,100,'abcv','123456','admin-slave',0,1,'2025-11-10 15:49:56','2025-11-10 15:49:56'),(2,101,'kefu','123456','support',0,1,'2026-09-06 12:03:50','2026-09-06 12:03:50');

--
-- Table structure for table `xray_packet`
--

DROP TABLE IF EXISTS `xray_packet`;
CREATE TABLE `xray_packet` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
  `name` varchar(50) NOT NULL COMMENT '套餐名称，如 1个月、12个月',
  `duration_months` int NOT NULL COMMENT '套餐时长(月)',
  `bonus_months` int DEFAULT '0' COMMENT '赠送时长(月)',
  `price` decimal(18,2) NOT NULL,
  `price_per_month` decimal(18,2) NOT NULL,
  `description` varchar(255) DEFAULT NULL COMMENT '套餐描述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint DEFAULT '1',
  `sort_order` int DEFAULT '0',
  PRIMARY KEY (`id`)
);

--
-- Dumping data for table `xray_packet`
--

INSERT INTO `xray_packet` VALUES (1,'1个月',1,0,0.01,0.01,'按月付费，灵活自由<br>即时开通，立即享受全会员权益','2026-06-11 15:01:31','2026-06-11 18:29:06',1,10),(2,'12个月',12,3,59.85,3.99,'一次性快速长期续订，赠送 3 个月时长<br>即时开通，立即享受全会员权益','2026-06-11 15:01:31','2026-06-11 15:01:31',1,20),(3,'24个月',24,3,61.83,2.29,'一次性最长周期优惠，赠送 3 个月时长<br>即时开通，立即享受全会员权益','2026-06-11 15:01:31','2026-06-11 15:01:31',1,30);

--
-- Table structure for table `xray_payment_merchant`
--

DROP TABLE IF EXISTS `xray_payment_merchant`;
CREATE TABLE `xray_payment_merchant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商户ID',
  `name` varchar(50) NOT NULL COMMENT '商户名称，如 Alipay、WeChat、Stripe',
  `type` varchar(20) NOT NULL COMMENT '支付类型：alipay/wechat/stripe/paypal/usdc 等',
  `config` text NOT NULL COMMENT '商户配置，JSON 格式存储密钥、appid、secret 等',
  `status` tinyint DEFAULT '1' COMMENT '状态：1启用，0禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `xray_payment_merchant_uniq_name_type` (`name`,`type`)
);

--
-- Dumping data for table `xray_payment_merchant`
--

INSERT INTO `xray_payment_merchant` VALUES (1,'Alipay Sandbox Payment','alipay','{\"appId\":\"9021000164666496\",\"privateKey\":\"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQD8KAL6yzkaD7UTZs/UjtI/8xRkehN1dVjxb2ArPwU2DzCiQm8PrB84aTFbqaXhUhK9wItWDDnSqv9RmzQEFqFPl7idBStD/0bArkaEUe8ADjmcUIqq91+MvuvdKa9Bmm4OrGIGfJ1Dviq5AYFh4JAu2nG71P4Z458mjP69NoOTPho23wWguOCq8Q/c1aS8bKsEBj1r4Zp+Isy428Rjbw3ydPoEs0/TcmfeEGHUkMJ9YaTbNcXPhRxKY4bhEMbcnglb7ERWvWU5GLNPbKyPYQllGkrFWdRb/Adtr0MYJBFDHKY3Pzqknl1Q5WTvhqIo2TgolKl2VgzGGbQnfKbkJdZJAgMBAAECggEAEwDE8LeeDwu9/r9RzFgTGkSsdcIJc2GrmU5mdfvi3acZaDEPg6PaGns7AAhx+uAfj3NSTOLQ/MJsSW8j5Wb7My9g91xrXU2AvIalw9UU6PEpgFFFkgrghY/h4KICljQYNTziilCb3X4YZCbr5D9zPkSyK5CqsbLIzLP1grb4BYGTVopzCxZkseXdKrJrrsmol/KccBm/VsI1f4oryj3oqrIwpyMtWoMs41Teehv3nCjsXIaCfMcprcQkm8MyYlb7BMWncWM969yRj8IjlWKGAiJGT4OIHG4WgtJnyERD3bdTknk8EoArF9C+8pWnFZzvaH2BgAs7VYSqJqotK8NPAQKBgQD/DbAZ3K/RJTMCJqTJdKu80kP3yXW7i/fh9LjM7u2NXzoHdHUWoqIlQpGJwfSTrIVW6qb1r2leLU25AFBOSwxkvxyqpwF+8yvbk3KB4QG2shwwdBmP5G6pYSyYlyP2TlLlnzv4WUa6xfu+eM6wCiEjE4hdVrQ2V4vAHVKH58ZcKQKBgQD9F5JA2L7mWjngsS865nJVNKagGMv/Q1xMy059G/3nliPqdevI1Y+oBF1sDiElvRW1Kp3lQhOiE3QeozynWG5GV6G3sfqnCEVm+2I6dn3U+L6IhiDTHJN8lTJYrXhZd40X8RIbRzwvUfUQzn3Omd8psH9q4ixDIyN0dpSyZnLtIQKBgA1JU0Elo+sLOwsDpFXyQ/gEJBJGZmGsuu8EZpQWZj2VFlrJEEhrlHLIcvhwKCOBOzj55FSsHJsRVRksEQLjGfQtmHqzVHqehExd8/scE5DG2n0Trzk1tyYPr9kLFo6AKgVYNDGNxQd5xNF5TZsSDAPdwp7KI30pNlh9rozx3/1xAoGALi3826QwwOs5oEv0KDUgK3fd51xe7Ian8sMt4RoYbAczKna1vQFqoueY/Dxfw/kKWvIZuVkjJ/GQwH5cdNRn2qHmuhPZfly9/RplQ/GW6CMy8Mo4dwzBxHucoh2U1g5ASlwMR7npu2Ci7JwgzG+7DmpL9X6rQNzKfVedriePVMECgYEA9RFavqzbn/e9PVa5TVmT9uRQ4XoC4/PyLGbMNcOLCxH9TWXSo7Oj4IfFlb9DLA7u/pqf3PRsCWAAD7o/PYgUWxHhLKuov9pTTK/VMXDfaFtFo2d9cl1IgIilb65UPBpo4XgjC6QQze3W2+xysRfsE9PHMrifgbbuW2LO4SonJsw=\",\"alipayPublicKey\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzL0xNWhCJKTMaGmS4yT9V4oOGA7Df79EbIxTrcdw+5UstyeeEB+L70u7mRN8XbuAaAlIUNpk54ULfy/B9z31QHaRAp9/HqYyU1Of/vrWnl5bDr+HKPEPLHLC5ctDFWUmGr2FHGhVQVEJff7MnUk5RBK2/ZVrgc2GWm5HzR5kGxPzSzRw1ijDxlSfgcP0eNP+pFmEJkxqhMOCywJS+FpchvER8irmTwofKiVR6gp5CcSBWBAUXzvoERc1XYN5rZon1dnS0Q2r4aXoDCAY+88W4z3pkNDHFPaNDYckstOlaLYeafb9V1mXoNp2UNrVDn4HiAtIyongZdIHUykDoOXRhQIDAQAB\",\"notifyUrl\":\"http://101.201.215.20:8000/api/client/payment/notify/alipay\",\"returnUrl\":\"http://101.201.215.20:8000/pay\",\"serviceUrl\":\"https://openapi-sandbox.dl.alipaydev.com/gateway.do\",\"format\":\"json\",\"charset\":\"UTF-8\",\"signType\":\"RSA2\",\"env\":\"sandbox\"}',1,'Alipay International','2026-06-11 15:01:31','2026-06-11 17:42:24'),(2,'WeChat International Payment','wechat','{appId:demo,mchId:demo,apiKey:demo,notifyUrl:http://127.0.0.1:8081/api/client/payment/callback/wechat}',1,'WeChat International','2026-06-11 15:01:31','2026-06-11 15:01:31'),(3,'Stripe Credit Card Payment','stripe','{secretKey:demo,publishableKey:demo}',1,'Stripe','2026-06-11 15:01:31','2026-06-11 15:01:31'),(4,'PayPal Payment','paypal','{clientId:demo,clientSecret:demo}',1,'PayPal','2026-06-11 15:01:31','2026-06-11 15:01:31'),(5,'Circle USDC Payment','circle','{apiKey:demo,accountId:demo}',1,'Circle USDC','2026-06-11 15:01:31','2026-06-11 15:01:31');

--
-- Table structure for table `xray_payment_notify_log`
--

DROP TABLE IF EXISTS `xray_payment_notify_log`;
CREATE TABLE `xray_payment_notify_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) DEFAULT NULL,
  `trade_no` varchar(128) DEFAULT NULL,
  `notify_id` varchar(128) DEFAULT NULL,
  `channel` varchar(32) NOT NULL,
  `trade_status` varchar(64) DEFAULT NULL,
  `amount` decimal(18,2) DEFAULT NULL,
  `currency` varchar(16) DEFAULT NULL,
  `raw_payload` text,
  `sign_verified` tinyint DEFAULT '0',
  `handled` tinyint DEFAULT '0',
  `fail_reason` varchar(500) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `xray_payment_notify_log_idx_order_no` (`order_no`),
  KEY `xray_payment_notify_log_idx_trade_no` (`trade_no`),
  KEY `xray_payment_notify_log_idx_notify_id` (`notify_id`),
  KEY `xray_payment_notify_log_idx_channel_created_at` (`channel`,`created_at`)
);

--
-- Dumping data for table `xray_payment_notify_log`
--

INSERT INTO `xray_payment_notify_log` VALUES (1,'ORDER_1781176345033_1198','2026061122001435060508672070','2026061101222191237135060508694023','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-11 19:12:29\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"oMiHIGNMl2QXAlCi3pzwBVWpVqVZQWWsu6S07dZM90YSboJ+FuyAME2xAjB/vWj/tO/sivWEV0i4a0dTTCn1U3tDwqpFpisACEnvstudpTUZpFB7uUbNONapnM+/tcyQRiQvtdzJFO5drysJV1e/h3eEdXcfJFoPSFmdmgoo4r84edBzfM5uI7OPB/IS0T8W4FYWXWu5EYvkdi6Qxe4wEkiDfEh/XOwpOKYAyVt55CtLK/dZ8tUzgnzoMaB4AmaQlds+Kvi4cySB3mjdXDSXb7OkMV5u+RWfrsYbL109uD+Cr9T0J8eK1HRR+tIkkTYtuhPGQNmm6az7vLGEDc/dXQ==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061101222191237135060508694023\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-11 19:12:36\",\"notify_time\":\"2026-06-11 19:12:37\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781176345033_1198\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061122001435060508672070\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-11 19:12:38','2026-06-11 19:12:38'),(2,'ORDER_1781236017221_9027','2026061222001435060508673865','2026061201222114727135060508691043','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-12 11:47:17\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"lnsgOOoD1O4eKkt5KnJify7s/qzLTFeq7Y1K05v6L5YBd56gYSxC2Wyafbl8tKoWhvm7aSzq/JXuzkkRGXwLsoaoILfpWIqPq8TdK6gIx2BgTmx3qHJ2CenNWWfhdyOXnBTUvos+Tp7EDh1lDk1X8D+u0UJzr8ceWgaHBVF24nFMZlY1iEBB03Bq2/a7dRWYnH34TxFf11EU2mXV05pBXhdF798SFra9v7EzQK0XodLIN5j3nTttdA4XPY16dXAxYlvTlP4FrlufAYtAkIyU501ej29WJlHStM9UEd7dnxSQyey55wbs8MKZ/EeMuwGFtRYXWPTYWHuolXTvPpaXCg==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061201222114727135060508691043\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-12 11:47:26\",\"notify_time\":\"2026-06-12 11:47:28\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781236017221_9027\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061222001435060508673865\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-12 11:47:28','2026-06-12 11:47:28'),(3,NULL,NULL,NULL,'alipay',NULL,NULL,NULL,'{}',0,0,'missing out_trade_no','2026-06-12 11:59:55','2026-06-12 11:59:55'),(4,'ORDER_1781242638754_4728','2026061222001435060508673868','2026061201222133740135060508692797','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-12 13:37:32\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"AJiV78z3mxkojz7g7PfDBk+QqOIg1SNifqChzZ6a/M7ju8MPpplppnZLDnLiGqnfMZHqC9cRJI9OUE6rxY3ozzKxbAPoOHk0yg/md0lIXO4moHymOp4jiKOn9evj2yBbWvGIiOcnE3T62Yhln38WNXqS5JgEhiiM3O6ZXpI5QHm6O1eu6UEO72ZVVCQKL6gZwYoMMoactzpbwX+Vpnk2m89O03bwv64bz4e0o8wPIfA9q4ffpCZimkPUGraPhPiA+ZipL4iwonNe9IebMzK8Z5JvX35XZUcY6LuZOr98U1BxX7cCNFwy5tc75O51t87u1jFKFLbQ5s5Qli9gDth5EQ==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061201222133740135060508692797\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-12 13:37:39\",\"notify_time\":\"2026-06-12 13:37:41\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781242638754_4728\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061222001435060508673868\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-12 13:37:40','2026-06-12 13:37:40'),(5,'ORDER_1781244401820_9963','2026061222001435060508668638','2026061201222140654135060508694038','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-12 14:06:47\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"K7odkGWE+6Ve7cWcNqatlIzf7jSJrPfmO3Cgdej5JJs1T/EVUNvMl9TxJtqDUtu5HOMorgC9cHqP/KmOF/owlCPQFRbWguJUWp6OaFANdZMsZSe+LpRdCUYLBaxcStBwA9FpQiAneMuVsinKV5XMn1nBZEc2ZbRHFFtJvwDV+eYpRoU/l2I/5g+csaaJBe9YSLa+K3VUEBIM70+O32Gr3mwoaNj6KSkYxPYe8NSUqswt3MOr2Dm1UQ4RWeeFnzDr6Pk/UZbOuceFRkuVcfg5gjNyNmiXt0pHqlXYZCXofdeETT41EbAO1vfs/EWxyiOzNzGvH/8WZ6yjmavBr+Ansg==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061201222140654135060508694038\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-12 14:06:54\",\"notify_time\":\"2026-06-12 14:06:55\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781244401820_9963\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061222001435060508668638\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-12 14:06:54','2026-06-12 14:06:54'),(6,'ORDER_1781244544617_5344','2026061222001435060508670218','2026061201222140917135060508695488','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-12 14:09:09\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"BZ1tJiNy7qGbrekvvHgE4+odouU0z1X+81VXJXeURhTMvKNOG9kDh4MWJR7wb7JYgL64JdS+tcMvlAhmBwBGgY1oCE9yDsT4hzHVjHER+Kb21TG0TSHFlahgncaeY4weL/jsCuXafTAd+Ad8fHbfPeQYsqhg8ct4jHJunFGRtiXFu3Z9kGu6oQEy9BBTRt2lOaJGO2tUn3Q3kgXip90Kk9nB1NvzUSH8vgmgRK1rx4BfXefdKD3KDNkSvEiWfP8Hz5IWoIUJGHuQwOhQEAiQ2zl1jQHqHXni+/THWapdLF2cFdNMU3FL0HZZpGXlI5cya3q6ZfB+GZutHJIRDvWv6Q==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061201222140917135060508695488\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-12 14:09:17\",\"notify_time\":\"2026-06-12 14:09:18\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781244544617_5344\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061222001435060508670218\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-12 14:09:17','2026-06-12 14:09:17'),(7,'ORDER_1781248023932_1257','2026061222001435060508668639','2026061201222150722135060508695489','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-12 15:07:10\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"SLgPXg6Bu5IOhabUE+21c31D1j1XURXRf8mJMZJbF76ugV1oiSn1XociJTffJJklFx/8/rG/CXKLh8q5gKVy86YKnlDFmwz7lCia6Y9Wu9iS7ZwoO1817tQMblq8EJgssTsKK6+895/zvOo8tXlzJdN0r2wJJbRyVPdViN4FDsOa0BXhlqY4oCRv46eGN27lkZzwBPjbijoY1zi6AmsCBygdMnxs4De1h4JBGoC5GvuWOiBhVbHQIzZKPk6VSET+VoAKdJBojhh4wXzPq8yA/AVQFeHw2EfuxQEizxVm2p0EjKJSRZhXSFaz1B1UWvrMB3gl/J955XPT3J12Ya+bTA==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061201222150722135060508695489\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-12 15:07:21\",\"notify_time\":\"2026-06-12 15:07:22\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781248023932_1257\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061222001435060508668639\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-12 15:07:22','2026-06-12 15:07:22'),(8,'ORDER_1781253890563_5854','2026061222001435060508673878','2026061201222164502135060508698664','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-12 16:44:54\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"I7Ha6XgeI8gNP36xPxIT37qtsPzJ6vp6kRfDdPerFPUjTS2CtQSq+0YJaq0ZzclrQ+Eb54tNv/5flrNoP6gqS7NmsdD35fwBMCkwCFLxRpHiQm7y12PFwwPKPNBGYkIsK2i39iSS8lRKuOdr3/VaFEyobmQX+WnkAbOxJ0R8mJAfdTcb8MAknm7Y70PX+iK2em5e8k71YoLhAd5xqQUbL0jgH/ZXr3VttqhZhLfxb/dpQ2Clr2b/216r9qUal7q0qFqQAMV8W1xjWBwEIL0biDOVh0Z0KjszWz0HN9YvjuNL8ct2pZ3tunv2GQ2k0QM/yXlV01idU/v55Ql0kmGgDQ==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061201222164502135060508698664\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-12 16:45:01\",\"notify_time\":\"2026-06-12 16:45:03\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781253890563_5854\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061222001435060508673878\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-12 16:45:02','2026-06-12 16:45:03'),(9,'ORDER_1781253961536_7111','2026061222001435060508677510','2026061201222164614135060508695490','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-12 16:46:06\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"L50jFJm6njZl7Lx1xETWskc/PcBjzi8UMXP1mgkyDlWJ4pKGC9M7qDQV/qUniIqKkzVJfm/yfEwRb97ipFf1Xd9LYP0a/1yAGM7wL4hhhDbb46/ltVjt/Zus25klEFk+1a3aMwgeRLxg3cq/AnaM6LcXlpIAVhXEvvPTQ1E++ZU6E/VXGHE+VsFi4fPcUFD0+mX8wlnXu/VbspigqiO9p8HoxDhTg/qT6I/rp1e5hp+XL3cDNBULg0pVGOiVB1M1nJL/qYWwQG9oaeO4hMboK2In+gnDgHVuMo7r0HNU1OV7VGdYdVMP/IrcWqNJuRW04SBrU+DBE9wwwNiWhp4Kqw==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061201222164614135060508695490\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-12 16:46:14\",\"notify_time\":\"2026-06-12 16:46:15\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781253961536_7111\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061222001435060508677510\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-12 16:46:15','2026-06-12 16:46:15'),(10,'ORDER_1781254124049_9952','2026061222001435060508678947','2026061201222164855135060508698665','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-12 16:48:48\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"EkOILRDNdHvak+IA+jFyQ3FT/rgH4zUvfUP7z3CHBCLc+D4JzjtlJWlFhEayfhus2zf/yjwV7aypADo8hy2PiygxTv8QJlrN41M7q4W5MiBAMDzwyIiHyeeRkEMYVqgP5pgAfDrXdHfCjc4pKeCEdG06snNRUspqOBtRobE6qmA9H5fCokaPwpNqjSQxcnCkezN5ldj4gidYAoW5UNs7d3gycHuY+gpYM9Q8PaQBHA0BGOgch8ojsots4VUI3kjh39DEMtwfHEqKIvhn2xfejuzl8KVakjEEpJKfbY/51ILJ98qRjM+iI3WdkaF+WLORgyr6VGoM4BZIeWQHQd0Atw==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061201222164855135060508698665\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-12 16:48:55\",\"notify_time\":\"2026-06-12 16:48:56\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781254124049_9952\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061222001435060508678947\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-12 16:48:56','2026-06-12 16:48:56'),(11,'ORDER_1781254324599_8288','2026061222001435060508673879','2026061201222165215135060508697402','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-12 16:52:09\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"lzpE76FkaIQLtVV7UzFV1zqPWb+sKbT9iAw2y9nSHh8TtqmeSLL6py+OYHa5x3fTjNO/GLXqU+wtiad9kGjO+hu8mi/CyepiIDcHFQdq+XDaN+mw1rgqShgprh7iVkW03DCiak9SscdwGDnTqAQtQ7HLP8cKyVsXyc2CKU+txY0k7lXhhPRhzh8fOoMQisMAdCly3gvOTVR67jNFrc/VpR5o/jPLUueNYckegiJfFdGvsS2h9mzyiWq6S4cs1ET0Fg5Rp7MMtw9zu9/eBde2X4kPcgk/zISMbYTU47Tg/NdIQU9OTxFRmItlAQtRSFyTfDMZnqpsCHHSlH6lkcqXEA==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061201222165215135060508697402\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-12 16:52:15\",\"notify_time\":\"2026-06-12 16:52:16\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781254324599_8288\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061222001435060508673879\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-12 16:52:16','2026-06-12 16:52:16'),(12,'ORDER_1781254557857_8274','2026061222001435060508678948','2026061201222165610135060508700884','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-12 16:56:02\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"BmjYP6qFj5qpfgYjEG410Ora1g+Sjhx5gGfDDpbF0W5JRl6ZV2xGapfRIVf707EU9irDh01DfW9x0T6GZJxwATKjcq2ixQJLidgkRJzT/a7DOYIduVF3j7X3HWUHMtC8br/c+N/ZWhoKFGXIzK4cguxI+nAg+NdCs9mkfIAZwhJ6577Ch8D+fUcCkvENwRhah+TzM8wUbmtDsLE0Bb3RpuBeyiGrxOYhq6Lgn3K4V/RGMpEPfX9gnUI9IR3zFY1QyCYzb1VA62NTGyEgID6WQAuN+zEd9zAMEgjDrm30YL7A9nbegAXBsv4c1I8u9vXAZKM/C2w9O0SCVdX4Gyzr6g==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061201222165610135060508700884\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-12 16:56:09\",\"notify_time\":\"2026-06-12 16:56:10\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781254557857_8274\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061222001435060508678948\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-12 16:56:10','2026-06-12 16:56:10'),(13,'ORDER_1781507107966_4485','2026061522001435060508688811','2026061501222150532135060508711028','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-15 15:05:24\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"p1h+zMR6oHy/rczzBO+bpnOp4tT2twpBAjUPxX6jrUwK2R/wYYcN1g2gcjA5MpS9E81uM8eG9xqaG7OpVpdH/Bsi/qQYQScdRoRLJ9cfYD6DgOpKlb6Fxar1w/1gnxT+CKuNDBYoLYwMrg7y/gRL+uyy7qLCVII9irNlrE+fen1c6rmP7+05fosbyXD/vBHew6k37Gc8JuDofkmo4Si87Ip+Xc/e+QjFISZ8HcL7PbCa4AOkTOfkYFK9+A+ejSQN5eMeXNnrW+mKeQAR2Ve24C8S0xGUXo05ncUe9Xv63m2PVli9COfoiIhc9vC7x/OYb5CV4oHFqeTQuTw4ibDTFg==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061501222150532135060508711028\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-15 15:05:32\",\"notify_time\":\"2026-06-15 15:05:33\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781507107966_4485\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061522001435060508688811\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-15 15:05:33','2026-06-15 15:05:33'),(14,'ORDER_1781507229624_7917','2026061522001435060508690091','2026061501222150726135060508709708','alipay','TRADE_SUCCESS',0.01,'USD','{\"gmt_create\":\"2026-06-15 15:07:13\",\"charset\":\"UTF-8\",\"seller_email\":\"jnlsin4627@sandbox.com\",\"subject\":\"Luxwap 1个月\",\"sign\":\"qHP2Zj5EHXkY/FYbF679XcNmYg9j63sNYSed7rrU1/6IFx49lDMMolkmBmb5FtorFohze1Uk7L7XVv9mE6iKJ15rndkicB2lWw0wRIOoOcLrBPoesr/YQNlrdcgjfGBORVa4aGDVb4hM62q9HDFJL3STDrFN3b9aSnNshdOdok55Kv0/h1JKgUJir68Q0Q7rfRnY2lleVuiYblKCkdYOvpl8xGxE4bNtxGSpHx4fPUDaee9uBM9EhGTn52sbwxxx3XldcG/twIPIeWTKTiNnC/PCrGjqhuCWfwA+GmFeyrcEV24/ifUIXXn4jRewn1KLhnH3DUiRKeiwiPerT1bP8w==\",\"buyer_id\":\"2088722102235064\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2026061501222150726135060508709708\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"9021000164666496\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088721102235054\",\"gmt_payment\":\"2026-06-15 15:07:26\",\"notify_time\":\"2026-06-15 15:07:27\",\"version\":\"1.0\",\"out_trade_no\":\"ORDER_1781507229624_7917\",\"total_amount\":\"0.01\",\"trade_no\":\"2026061522001435060508690091\",\"auth_app_id\":\"9021000164666496\",\"buyer_logon_id\":\"dauvky1389@sandbox.com\",\"point_amount\":\"0.00\"}',1,1,NULL,'2026-06-15 15:07:27','2026-06-15 15:07:27');

--
-- Table structure for table `xray_payment_order`
--

DROP TABLE IF EXISTS `xray_payment_order`;
CREATE TABLE `xray_payment_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `packet_id` bigint NOT NULL,
  `order_no` varchar(64) NOT NULL,
  `submit_token` varchar(64) DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `amount` decimal(18,2) NOT NULL,
  `currency` varchar(16) NOT NULL DEFAULT 'USD',
  `status` varchar(32) NOT NULL,
  `notify_url` varchar(255) DEFAULT NULL,
  `return_url` varchar(255) DEFAULT NULL,
  `payment_url` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `expired_at` datetime DEFAULT NULL,
  `trade_no` varchar(128) DEFAULT NULL,
  `paid_amount` decimal(18,2) DEFAULT NULL,
  `paid_currency` varchar(16) DEFAULT NULL,
  `paid_at` datetime DEFAULT NULL,
  `success_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `xray_payment_order_uk_order_no` (`order_no`),
  UNIQUE KEY `xray_payment_order_uk_payment_order_submit_token` (`submit_token`),
  KEY `xray_payment_order_idx_user_id` (`user_id`),
  KEY `xray_payment_order_idx_packet_id` (`packet_id`),
  KEY `xray_payment_order_idx_status` (`status`),
  KEY `xray_payment_order_idx_trade_no` (`trade_no`)
);

--
-- Dumping data for table `xray_payment_order`
--

INSERT INTO `xray_payment_order` VALUES (1,1,1,'ORDER_1781161522213_7430',NULL,'alipay',8.99,'USD','CLOSED',NULL,NULL,NULL,'2026-06-11 15:05:22','2026-06-11 18:20:18','2026-06-11 16:05:22',NULL,NULL,NULL,NULL,NULL),(2,1,1,'ORDER_1781161664352_8590',NULL,'alipay',8.99,'USD','CLOSED',NULL,NULL,NULL,'2026-06-11 15:07:44','2026-06-11 18:20:18','2026-06-11 16:07:44',NULL,NULL,NULL,NULL,NULL),(3,1,1,'ORDER_1781168559414_4534',NULL,'alipay',8.99,'USD','FAILED','http://127.0.0.1:8081/api/client/payment/notify/alipay','http://127.0.0.1:8081/pay',NULL,'2026-06-11 17:02:39','2026-06-11 17:02:40','2026-06-11 18:02:39',NULL,NULL,NULL,NULL,NULL),(4,1,1,'ORDER_1781168572133_443',NULL,'alipay',8.99,'USD','FAILED','http://127.0.0.1:8081/api/client/payment/notify/alipay','http://127.0.0.1:8081/pay',NULL,'2026-06-11 17:02:52','2026-06-11 17:02:52','2026-06-11 18:02:52',NULL,NULL,NULL,NULL,NULL),(5,1,1,'ORDER_1781168663212_6528',NULL,'alipay',8.99,'USD','FAILED','http://127.0.0.1:8081/api/client/payment/notify/alipay','http://127.0.0.1:8081/pay',NULL,'2026-06-11 17:04:23','2026-06-11 17:04:24','2026-06-11 18:04:23',NULL,NULL,NULL,NULL,NULL),(6,1,1,'ORDER_1781168682084_5630',NULL,'alipay',8.99,'USD','FAILED','http://127.0.0.1:8081/api/client/payment/notify/alipay','http://127.0.0.1:8081/pay',NULL,'2026-06-11 17:04:42','2026-06-11 17:04:42','2026-06-11 18:04:42',NULL,NULL,NULL,NULL,NULL),(7,1,1,'ORDER_1781168851211_4615',NULL,'alipay',8.99,'USD','FAILED','http://127.0.0.1:8081/api/client/payment/notify/alipay','http://127.0.0.1:8081/pay',NULL,'2026-06-11 17:07:31','2026-06-11 17:07:32','2026-06-11 18:07:31',NULL,NULL,NULL,NULL,NULL),(8,1,1,'ORDER_1781168927048_426',NULL,'alipay',8.99,'USD','CLOSED','http://127.0.0.1:8081/api/client/payment/notify/alipay','http://127.0.0.1:8081/pay','<form name=\"punchout_form\" method=\"post\" action=\"https://openapi.alipay.com/gateway.do?charset=UTF-8&method=alipay.trade.page.pay&sign=pIrEZpZh9qDsiC%2F7lscCUl%2BbS6CjXZmU%2FwHJkYneCSkercKI7dMOFZReIM%2BsJnSQuymhGksu6IRg5WzNHgO3RIe8bfGqBgBpCLq%2BxK1npKaNqDYn4yZYgwCEshEXLC%2B3HEXqEo1RalZkzPTo6EghLgsSTU9sSbw7pHsDWpdHt1%2Bz6x0udNt7mkkoopPUUlIWgUdd7XRf97UxhOiZujxzzAztvtbMJ%2BUFI7Mposcn9SValHiRRzTy8ES8NjdZyqvVkyEFJNCGLi8JHwS34JN311tDduM3LhptUKnu%2FCv8a%2FjD1RpXXqBQySVoAaUwVIEF1RsrJiaFEehR5ypFwmAKlw%3D%3D&return_url=http%3A%2F%2F127.0.0.1%3A8081%2Fpay&notify_url=http%3A%2F%2F127.0.0.1%3A8081%2Fapi%2Fclient%2Fpayment%2Fnotify%2Falipay&version=1.0&app_id=2021006158692340&sign_type=RSA2&timestamp=2026-06-11+17%3A08%3A47&alipay_sdk=alipay-sdk-java-4.40.512.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;ORDER_1781168927048_426&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;,&quot;subject&quot;:&quot;Luxwap 1个月&quot;,&quot;total_amount&quot;:&quot;8.99&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>','2026-06-11 17:08:47','2026-06-11 17:43:57','2026-06-11 18:08:47',NULL,NULL,NULL,NULL,NULL),(9,1,1,'ORDER_1781168944731_3658',NULL,'alipay',8.99,'USD','CLOSED','http://127.0.0.1:8081/api/client/payment/notify/alipay','http://127.0.0.1:8081/pay','<form name=\"punchout_form\" method=\"post\" action=\"https://openapi.alipay.com/gateway.do?charset=UTF-8&method=alipay.trade.page.pay&sign=i4ZtgnnXYJEj1sx1AQi1vq5npUgG%2F5ktaAIr8gggsxk4UGO%2Bn43JGREXj16%2BjrOJ4erPkg5%2FkJuQMPAD2G%2Bsam%2FSDSNbcY0ttMnevdv%2F%2Fq%2B1YPhcl0757Z1UwAjBzWrahBN%2Bcd90kMq6oKdlnXjJTLFGEvyLezG7yrBS3UqUJWGvHPUwhH2JHb%2BWHFkTYcFYIGF4brXTRU4zGce7k1x5jXv6dxsormrpssud04Ckmx1yjOgRgJhBBTZc5q9uwd%2Bl0Y5nachvMFJewA%2FPOQWnAY5BTp5AZHSLG67%2Bb0hfXs2OzQe5n5KesBoXrRt3q6pT8rAxCZd%2BI1Qk2cpHxS4p9w%3D%3D&return_url=http%3A%2F%2F127.0.0.1%3A8081%2Fpay&notify_url=http%3A%2F%2F127.0.0.1%3A8081%2Fapi%2Fclient%2Fpayment%2Fnotify%2Falipay&version=1.0&app_id=2021006158692340&sign_type=RSA2&timestamp=2026-06-11+17%3A09%3A04&alipay_sdk=alipay-sdk-java-4.40.512.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;ORDER_1781168944731_3658&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;,&quot;subject&quot;:&quot;Luxwap 1个月&quot;,&quot;total_amount&quot;:&quot;8.99&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>','2026-06-11 17:09:05','2026-06-11 17:43:57','2026-06-11 18:09:05',NULL,NULL,NULL,NULL,NULL),(10,1,2,'ORDER_1781170686962_8328',NULL,'alipay',59.85,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','<form name=\"punchout_form\" method=\"post\" action=\"https://openapi.alipay.com/gateway.do?charset=UTF-8&method=alipay.trade.page.pay&sign=DOhBADEKOXa%2FYW8EX7dFxTnZf2fOgS4jank%2BFnA1bvrLkJ0%2FL%2BS31lTNrnYrQR%2FpMZ8EcboVYSMhoSFhs5jCd7C5hgKgk8310hG0QDuJI6Ap%2ByVO8YD4bUw8gW2qRyFcOuQcxXih%2F3AYiiTCyvz96vc4QRIJvBe6z3VjL0s2ySi0m%2BDutg7Qwg%2FF6EC%2BGw5V3cq0RLEO%2FPa3EC3Ydo8KDVZ562V2FzEwPlnly3coxTGD6pN7T0jX6dkDZLvFrQoIhp5TYrwOA4vuxEfttVgn7tmHs0L5EHC8C%2BaHdZGXVGWf%2Fws7KYWRa0fQBLyT8f9U2tbVeTP2jTmzzjaLpUxb2A%3D%3D&return_url=http%3A%2F%2F101.201.215.20%3A8000%2Fpay&notify_url=http%3A%2F%2F101.201.215.20%3A8000%2Fapi%2Fclient%2Fpayment%2Fnotify%2Falipay&version=1.0&app_id=2021006158692340&sign_type=RSA2&timestamp=2026-06-11+17%3A38%3A07&alipay_sdk=alipay-sdk-java-4.40.512.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;ORDER_1781170686962_8328&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;,&quot;subject&quot;:&quot;Luxwap 12个月&quot;,&quot;total_amount&quot;:&quot;59.85&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>','2026-06-11 17:38:07','2026-06-11 17:43:57','2026-06-11 18:38:07',NULL,NULL,NULL,NULL,NULL),(11,1,3,'ORDER_1781170956376_1640',NULL,'alipay',61.83,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','<form name=\"punchout_form\" method=\"post\" action=\"https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=UTF-8&method=alipay.trade.page.pay&sign=cOFlCGPAGx9SJ%2FxtBedgZDTB5HjSDM72BWoz6kcLGf%2BD5mdvr6laML4HF%2BzW%2F3WrSgOlYyT90GZcAfaInSvQNeBfNFZEH10pAtnvLqMq%2FrUhDvRAUMhDM27kjsXK2RydeDBMFCzxW6u1KBZXcyZ2zRKZjyhlu4AK7sywM5d4O%2BCFyuu5ElpTMZLAQP9RCOGL9Rs9MaC7lLkOb0nfuH0k9yPwx505e8J%2FV6vWpyMMqC%2FULvRis%2BbrNITSZnr7Y4OST%2B%2BCWx8eyMeXLdla8RR%2FXZG0dN4JfFbP%2BpuBcLU0B7WzRkLB%2Fz%2B4ytT6Sv57bPSLzxkrfaFhirPUpMBhfPTBVQ%3D%3D&return_url=http%3A%2F%2F101.201.215.20%3A8000%2Fpay&notify_url=http%3A%2F%2F101.201.215.20%3A8000%2Fapi%2Fclient%2Fpayment%2Fnotify%2Falipay&version=1.0&app_id=9021000164666496&sign_type=RSA2&timestamp=2026-06-11+17%3A42%3A36&alipay_sdk=alipay-sdk-java-4.40.512.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;ORDER_1781170956376_1640&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;,&quot;subject&quot;:&quot;Luxwap 24个月&quot;,&quot;total_amount&quot;:&quot;61.83&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>','2026-06-11 17:42:36','2026-06-11 18:20:18','2026-06-11 18:42:36',NULL,NULL,NULL,NULL,NULL),(12,1,1,'ORDER_1781171110302_4384',NULL,'alipay',8.99,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','<form name=\"punchout_form\" method=\"post\" action=\"https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=UTF-8&method=alipay.trade.page.pay&sign=f1ihHMqILPvS%2F3tTFcdrGm6ejfUiZbLAqDt%2BeD70CwEfr5d%2B%2BERx6HIqdaM7SB%2BkQ%2F7tsICdXrvBlH53507o%2FdZoXH7NYw0khjkNTpA5JKYPxLUxXtU0PL%2BA4tN9y%2FfhmnKeqHajyHPFmbapudq3hQSgn6EMzzM17t%2Fn68jeLv77FS%2B943w9zE78fyuONEnvTBLNFfUI8JSOQ6wsExFurNCMSOEd3v8yRIfvmXRI2aRs0lmSCVyHlfUY5sHl2ArvQ%2Fky8U0ERQLPUpeIEeBOBEOgTg7s6U0eaIaEFv85J0q8C0CGWEHIzmaguM7UtOXaAf6h7ZBUi4sJ1e9HE%2FhhfA%3D%3D&return_url=http%3A%2F%2F101.201.215.20%3A8000%2Fpay&notify_url=http%3A%2F%2F101.201.215.20%3A8000%2Fapi%2Fclient%2Fpayment%2Fnotify%2Falipay&version=1.0&app_id=9021000164666496&sign_type=RSA2&timestamp=2026-06-11+17%3A45%3A10&alipay_sdk=alipay-sdk-java-4.40.512.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;ORDER_1781171110302_4384&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;,&quot;subject&quot;:&quot;Luxwap 1个月&quot;,&quot;total_amount&quot;:&quot;8.99&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>','2026-06-11 17:45:10','2026-06-11 18:20:18','2026-06-11 18:45:10',NULL,NULL,NULL,NULL,NULL),(13,1,1,'ORDER_1781173219728_3047',NULL,'alipay',8.99,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','<form name=\"punchout_form\" method=\"post\" action=\"https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=UTF-8&method=alipay.trade.page.pay&sign=9iWBTZHnHu9ukKTpM1z72c4F%2FH6BUqE9WryVO%2FUG0C5ULwZ7CRpmLb6YzAkpPZgIu4NNQ7hTjo3WzDoTTzamUSX5U4h%2FyDSRuOniMbeyRrseHZ9wWKuK%2Fhk2jlVdtKbPIer1fDMQ%2FwKApelW6ngAVMFRni8ecTfkxZXhIRfAhXszzcwo44r9%2BQOQysr%2F%2Bqg6TjVtxFVQf20nd2VkaG7ujASl83tl%2Bjh6BJdfxunULYc6DUo6%2F%2FRaQOaOYoSWX8bnCtP5OGe1%2Fo5Jlje3ta09Mtlun%2Fr0LURL%2FJICWHIjU3fBlSH3yv2V3Dmd0AVzWZYGLcXjgnZ64P56k4rFQjNkvQ%3D%3D&return_url=http%3A%2F%2F101.201.215.20%3A8000%2Fpay&notify_url=http%3A%2F%2F101.201.215.20%3A8000%2Fapi%2Fclient%2Fpayment%2Fnotify%2Falipay&version=1.0&app_id=9021000164666496&sign_type=RSA2&timestamp=2026-06-11+18%3A20%3A19&alipay_sdk=alipay-sdk-java-4.40.512.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;ORDER_1781173219728_3047&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;,&quot;subject&quot;:&quot;Luxwap 1个月&quot;,&quot;total_amount&quot;:&quot;8.99&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>','2026-06-11 18:20:20','2026-06-11 18:23:34','2026-06-11 19:20:20',NULL,NULL,NULL,NULL,NULL),(14,1,1,'ORDER_1781173785622_5072',NULL,'alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax00388i61vzv9aiprp007e','2026-06-11 18:29:46','2026-06-11 18:38:10','2026-06-11 19:29:46',NULL,NULL,NULL,NULL,NULL),(15,1,1,'ORDER_1781174345675_1328',NULL,'alipay',0.01,'USD','FAILED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay',NULL,'2026-06-11 18:39:06','2026-06-11 18:39:21','2026-06-11 18:49:06',NULL,NULL,NULL,NULL,NULL),(16,1,1,'ORDER_1781174375277_6423',NULL,'alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax07216nd5g7yldh6zp008e','2026-06-11 18:39:35','2026-06-11 18:51:29','2026-06-11 18:49:35',NULL,NULL,NULL,NULL,NULL),(17,1,1,'ORDER_1781175076047_7459','07b27281e8954028a491093fe6cc4385','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax04723jrkiduhxl9yx00d5','2026-06-11 18:51:16','2026-06-11 19:01:29','2026-06-11 19:01:16',NULL,NULL,NULL,NULL,NULL),(18,1,1,'ORDER_1781175081301_8104','8c73f04372ee4231aabbde825833e8e1','alipay',0.01,'USD','FAILED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay',NULL,'2026-06-11 18:51:21','2026-06-11 18:51:36','2026-06-11 19:01:21',NULL,NULL,NULL,NULL,NULL),(19,1,1,'ORDER_1781175624125_4123','2522b812d8bc4194939ccb3c75e85368','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax08465cusykl6w2jbx0075','2026-06-11 19:00:24','2026-06-11 19:02:47','2026-06-11 19:10:24',NULL,NULL,NULL,NULL,NULL),(20,1,1,'ORDER_1781176036049_7530','ba1cd4bcb7054340a6ef29708ed13e93','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax05527kfuqbk5uttjv005b','2026-06-11 19:07:16','2026-06-11 19:19:46','2026-06-11 19:17:16',NULL,NULL,NULL,NULL,NULL),(21,1,1,'ORDER_1781176345033_1198','d5cfb7c1d32a4b89aa0ed914707dafad','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax07435ihsq8f1mtfr800b2','2026-06-11 19:12:25','2026-06-11 19:12:38','2026-06-11 19:22:25','2026061122001435060508672070',0.01,'USD','2026-06-11 19:12:36','2026-06-11 19:12:38'),(22,1,1,'ORDER_1781235853716_8850','b87ed9667a934e6ab09531b615cd8085','alipay',0.01,'USD','FAILED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay',NULL,'2026-06-12 11:44:14','2026-06-12 11:44:20','2026-06-12 11:54:14',NULL,NULL,NULL,NULL,NULL),(23,1,1,'ORDER_1781235877928_4216','e5b7a24f6e654eff9790636c87ac4a89','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax03193zlvxjn04suru00f2','2026-06-12 11:44:38','2026-06-12 11:54:39','2026-06-12 11:54:38',NULL,NULL,NULL,NULL,NULL),(24,1,1,'ORDER_1781236017221_9027','f0664b05c4274d4c8ffee3a20811f4da','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax08149ehiausebbmjs00ff','2026-06-12 11:46:57','2026-06-12 11:47:28','2026-06-12 11:56:57','2026061222001435060508673865',0.01,'USD','2026-06-12 11:47:26','2026-06-12 11:47:28'),(25,1,1,'ORDER_1781236508178_6350','433a45e7cc2a44929dd47b1dd3d529b1','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax06886bj7jxdnpepov00a0','2026-06-12 11:55:08','2026-06-12 12:05:39','2026-06-12 12:05:08',NULL,NULL,NULL,NULL,NULL),(26,1,1,'ORDER_1781236596372_726','22515d62050043aab7a464c86e3766fb','alipay',0.01,'USD','FAILED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay',NULL,'2026-06-12 11:56:36','2026-06-12 11:56:42','2026-06-12 12:06:36',NULL,NULL,NULL,NULL,NULL),(27,1,1,'ORDER_1781236618012_2173','b187ab5b3fde47dba960a51598ec4d00','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax000571j1epujynfwm009a','2026-06-12 11:56:58','2026-06-12 12:07:39','2026-06-12 12:06:58',NULL,NULL,NULL,NULL,NULL),(28,1,1,'ORDER_1781236638687_2585','bf4b238b41654264aadb7edca06e8dfd','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax05232vzfpnazvudal00af','2026-06-12 11:57:19','2026-06-12 12:07:39','2026-06-12 12:07:19',NULL,NULL,NULL,NULL,NULL),(29,1,1,'ORDER_1781236877168_7427','f14423dc38f54d2a9d93a1ebfb713303','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax08591nulnitvnftay0051','2026-06-12 12:01:17','2026-06-12 12:11:39','2026-06-12 12:11:17',NULL,NULL,NULL,NULL,NULL),(30,1,1,'ORDER_1781242638754_4728','ce79e3eb18014f3590fd786b398ff3b6','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax09835cb0kz2uexi9s00a5','2026-06-12 13:37:19','2026-06-12 13:37:40','2026-06-12 13:47:19','2026061222001435060508673868',0.01,'USD','2026-06-12 13:37:39','2026-06-12 13:37:40'),(31,1,1,'ORDER_1781244263283_5200','4b4375dd8df84853a6a68e600fd3be53','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax01557ypiut5dyqjml00b3','2026-06-12 14:04:23','2026-06-12 14:14:43','2026-06-12 14:14:23',NULL,NULL,NULL,NULL,NULL),(32,1,1,'ORDER_1781244281071_9165','98b54ca908674c4b858f5f36d0086808','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax06919s85cnwcusw3v00fe','2026-06-12 14:04:41','2026-06-12 14:14:43','2026-06-12 14:14:41',NULL,NULL,NULL,NULL,NULL),(33,1,1,'ORDER_1781244401820_9963','5232d99cc1cd43878b1dfb96d6537d51','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax09749k6eokp2cmgbv0013','2026-06-12 14:06:42','2026-06-12 14:06:54','2026-06-12 14:16:42','2026061222001435060508668638',0.01,'USD','2026-06-12 14:06:54','2026-06-12 14:06:54'),(34,1,1,'ORDER_1781244544617_5344','99fa40eace9143e986b13129d19e0b0c','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax07259zcs5ewlp1taw00be','2026-06-12 14:09:05','2026-06-12 14:09:17','2026-06-12 14:19:05','2026061222001435060508670218',0.01,'USD','2026-06-12 14:09:17','2026-06-12 14:09:17'),(35,1,1,'ORDER_1781247870745_5288','55a1b8976aeb47b1ae13490f050021e3','alipay',0.01,'USD','FAILED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay',NULL,'2026-06-12 15:04:31','2026-06-12 15:04:47','2026-06-12 15:14:31',NULL,NULL,NULL,NULL,NULL),(36,1,1,'ORDER_1781247900004_5240','135691e0e29949ffbbfd56465a1eac13','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax06799osi8oiblupdl0056','2026-06-12 15:05:00','2026-06-12 15:15:51','2026-06-12 15:15:00',NULL,NULL,NULL,NULL,NULL),(37,1,1,'ORDER_1781248010822_251','6f45cabdcbed4399a9c31e2afcc5b6d2','alipay',0.01,'USD','FAILED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay',NULL,'2026-06-12 15:06:51','2026-06-12 15:06:57','2026-06-12 15:16:51',NULL,NULL,NULL,NULL,NULL),(38,1,1,'ORDER_1781248023932_1257','df2009f4e5ba4bb8ad07bdbdbfda04bd','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax09522frcyaui5bkej00ee','2026-06-12 15:07:04','2026-06-12 15:07:22','2026-06-12 15:17:04','2026061222001435060508668639',0.01,'USD','2026-06-12 15:07:21','2026-06-12 15:07:22'),(39,1,1,'ORDER_1781252765889_4532','02c9c0da3a820acc5a63aff5c63e8da9','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax08070o2buby0xffax0025','2026-06-12 16:26:06','2026-06-12 16:43:55','2026-06-12 16:36:06',NULL,NULL,NULL,NULL,NULL),(40,1,1,'ORDER_1781252905440_9050','657bfc08789090370618a08e7c5e7e6e','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax03018a1q6lqnzktmg00d8','2026-06-12 16:28:25','2026-06-12 16:43:55','2026-06-12 16:38:25',NULL,NULL,NULL,NULL,NULL),(41,1,1,'ORDER_1781252944838_3004','c2ab5ecb256fd4a772b36a2639449854','alipay',0.01,'USD','FAILED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay',NULL,'2026-06-12 16:29:05','2026-06-12 16:29:10','2026-06-12 16:39:05',NULL,NULL,NULL,NULL,NULL),(42,1,1,'ORDER_1781252994747_9394','33f4adc84f2d04c94ae4984bde003220','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax09573qyjkbnk0akkh00d7','2026-06-12 16:29:55','2026-06-12 16:43:55','2026-06-12 16:39:55',NULL,NULL,NULL,NULL,NULL),(43,1,1,'ORDER_1781253870342_7557','528dc5c3f36b810613b659b42936077c','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax01887epontytucxzt00eb','2026-06-12 16:44:30','2026-06-12 16:54:37','2026-06-12 16:54:30',NULL,NULL,NULL,NULL,NULL),(44,1,1,'ORDER_1781253890563_5854','edd05d4c59dabcdf168a10c81eb6402f','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax01475mpbib6oxbivx002d','2026-06-12 16:44:51','2026-06-12 16:45:03','2026-06-12 16:54:51','2026061222001435060508673878',0.01,'USD','2026-06-12 16:45:01','2026-06-12 16:45:03'),(45,1,1,'ORDER_1781253948033_8573','96e2d7d4e13fca03aa00fa16cd5c839b','alipay',0.01,'USD','FAILED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay',NULL,'2026-06-12 16:45:48','2026-06-12 16:45:54','2026-06-12 16:55:48',NULL,NULL,NULL,NULL,NULL),(46,1,1,'ORDER_1781253961536_7111','0a855ab7a566150f0d988fec1219773d','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax05262dybz2dkwvwo20028','2026-06-12 16:46:02','2026-06-12 16:46:15','2026-06-12 16:56:02','2026061222001435060508677510',0.01,'USD','2026-06-12 16:46:14','2026-06-12 16:46:15'),(47,1,1,'ORDER_1781254102985_7809','faf109ab2da26402cd24117b3ce3d1c9','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax01374abs1rzz2sg6700d1','2026-06-12 16:48:23','2026-06-12 16:59:18','2026-06-12 16:58:23',NULL,NULL,NULL,NULL,NULL),(48,1,1,'ORDER_1781254124049_9952','928e8e2a4811adb19a3fc92a080a2638','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax02565ukxwc6zycm9l000e','2026-06-12 16:48:44','2026-06-12 16:48:56','2026-06-12 16:58:44','2026061222001435060508678947',0.01,'USD','2026-06-12 16:48:55','2026-06-12 16:48:56'),(49,1,1,'ORDER_1781254299978_1418','12a55aaa392ec16ecec9f4b08a399041','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax06323cxgo01qyhpna00e0','2026-06-12 16:51:40','2026-06-12 17:02:18','2026-06-12 17:01:40',NULL,NULL,NULL,NULL,NULL),(50,1,1,'ORDER_1781254324599_8288','0d7d49bc05462806337a1c8e8a449067','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax02366j9thzyl5csw40068','2026-06-12 16:52:05','2026-06-12 16:52:16','2026-06-12 17:02:05','2026061222001435060508673879',0.01,'USD','2026-06-12 16:52:15','2026-06-12 16:52:16'),(51,1,1,'ORDER_1781254531221_6842','a9c84e53d8f5a77c98095c2fb2ba48af','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax0945988dknaxnatul0066','2026-06-12 16:55:31','2026-06-12 17:06:18','2026-06-12 17:05:31',NULL,NULL,NULL,NULL,NULL),(52,1,1,'ORDER_1781254547983_7878','6b0ce433e081fca315a30ef82dbe75dc','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax02378yngowlmbmvt400e8','2026-06-12 16:55:48','2026-06-12 17:06:18','2026-06-12 17:05:48',NULL,NULL,NULL,NULL,NULL),(53,1,1,'ORDER_1781254557857_8274','0890ea8e33410ba6bcf941945fb61f2d','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax05072pjeumvb4e00c0074','2026-06-12 16:55:58','2026-06-12 16:56:10','2026-06-12 17:05:58','2026061222001435060508678948',0.01,'USD','2026-06-12 16:56:09','2026-06-12 16:56:10'),(54,1,1,'ORDER_1781447792196_6544','aa319330f851a13abbb5081baf172f9b','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax09248kjs6tyz732nj003f','2026-06-14 22:36:32','2026-06-14 22:47:13','2026-06-14 22:46:32',NULL,NULL,NULL,NULL,NULL),(55,1,1,'ORDER_1781507107966_4485','6b00cfe3a79deec6d81ded9971d2b2d1','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax024836nc2brwtio5r00b3','2026-06-15 15:05:08','2026-06-15 15:05:33','2026-06-15 15:15:08','2026061522001435060508688811',0.01,'USD','2026-06-15 15:05:32','2026-06-15 15:05:33'),(56,1,1,'ORDER_1781507155336_3572','c18c1860ccbf576f9935f0cd00145219','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax079938hsimwiz1e0r0033','2026-06-15 15:05:55','2026-06-15 15:16:17','2026-06-15 15:15:55',NULL,NULL,NULL,NULL,NULL),(57,1,1,'ORDER_1781507171191_923','b7234b47ae0e4a3eef137f82734ee9e2','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax01397s1tqumnbtqdn0028','2026-06-15 15:06:11','2026-06-15 15:16:17','2026-06-15 15:16:11',NULL,NULL,NULL,NULL,NULL),(58,1,1,'ORDER_1781507185592_8276','a2d8a98ead791b11c634a44e12ac4740','alipay',0.01,'USD','FAILED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay',NULL,'2026-06-15 15:06:26','2026-06-15 15:06:31','2026-06-15 15:16:26',NULL,NULL,NULL,NULL,NULL),(59,1,1,'ORDER_1781507213247_662','c9eeed0950892451c1d1f6457a89808a','alipay',0.01,'USD','CLOSED','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax03493sms6d9tziywf000c','2026-06-15 15:06:53','2026-06-15 15:17:17','2026-06-15 15:16:53',NULL,NULL,NULL,NULL,NULL),(60,1,1,'ORDER_1781507229624_7917','e5cc5e5712c86cdc07bbe2889e65315d','alipay',0.01,'USD','SUCCESS','http://101.201.215.20:8000/api/client/payment/notify/alipay','http://101.201.215.20:8000/pay','https://qr.alipay.com/bax00933qvzw3gxc4cjp0085','2026-06-15 15:07:10','2026-06-15 15:07:27','2026-06-15 15:17:10','2026061522001435060508690091',0.01,'USD','2026-06-15 15:07:26','2026-06-15 15:07:27');

--
-- Table structure for table `xray_payment_submit_token`
--

DROP TABLE IF EXISTS `xray_payment_submit_token`;
CREATE TABLE `xray_payment_submit_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` varchar(64) NOT NULL,
  `user_id` bigint NOT NULL,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0 unused, 1 used, 2 expired',
  `order_no` varchar(64) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `expired_at` datetime NOT NULL,
  `used_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `xray_payment_submit_token_uk_submit_token_token` (`token`),
  KEY `xray_payment_submit_token_idx_submit_token_user_status` (`user_id`,`status`),
  KEY `xray_payment_submit_token_idx_submit_token_expired_at` (`expired_at`),
  KEY `xray_payment_submit_token_idx_submit_token_order_no` (`order_no`)
);

--
-- Dumping data for table `xray_payment_submit_token`
--

INSERT INTO `xray_payment_submit_token` VALUES (1,'d8560e14767c482396f6734e85c05c1d',1,2,NULL,'2026-06-11 18:51:03','2026-06-11 18:56:03',NULL),(2,'07b27281e8954028a491093fe6cc4385',1,1,'ORDER_1781175076047_7459','2026-06-11 18:51:05','2026-06-11 18:56:05','2026-06-11 18:51:16'),(3,'8c73f04372ee4231aabbde825833e8e1',1,1,'ORDER_1781175081301_8104','2026-06-11 18:51:21','2026-06-11 18:56:21','2026-06-11 18:51:21'),(4,'2522b812d8bc4194939ccb3c75e85368',1,1,'ORDER_1781175624125_4123','2026-06-11 19:00:22','2026-06-11 19:05:22','2026-06-11 19:00:24'),(5,'ba1cd4bcb7054340a6ef29708ed13e93',1,1,'ORDER_1781176036049_7530','2026-06-11 19:07:14','2026-06-11 19:12:14','2026-06-11 19:07:16'),(6,'d5cfb7c1d32a4b89aa0ed914707dafad',1,1,'ORDER_1781176345033_1198','2026-06-11 19:12:23','2026-06-11 19:17:23','2026-06-11 19:12:25'),(7,'7323be0a277547f982aaf1d3f67d92f5',1,2,NULL,'2026-06-11 19:18:49','2026-06-11 19:23:49',NULL),(8,'dab13afce4274a209efdc544bde4263c',1,2,NULL,'2026-06-11 19:19:08','2026-06-11 19:24:08',NULL),(9,'d6f83fe1d48243229ef3ac55a603bfdd',1,2,NULL,'2026-06-11 19:19:31','2026-06-11 19:24:31',NULL),(10,'b87ed9667a934e6ab09531b615cd8085',1,1,'ORDER_1781235853716_8850','2026-06-12 11:44:09','2026-06-12 11:49:09','2026-06-12 11:44:13'),(11,'e5b7a24f6e654eff9790636c87ac4a89',1,1,'ORDER_1781235877928_4216','2026-06-12 11:44:36','2026-06-12 11:49:36','2026-06-12 11:44:37'),(12,'f0664b05c4274d4c8ffee3a20811f4da',1,1,'ORDER_1781236017221_9027','2026-06-12 11:46:55','2026-06-12 11:51:55','2026-06-12 11:46:57'),(13,'55138500e2cc4518811580a1a693f060',1,2,NULL,'2026-06-12 11:47:35','2026-06-12 11:52:35',NULL),(14,'433a45e7cc2a44929dd47b1dd3d529b1',1,1,'ORDER_1781236508178_6350','2026-06-12 11:55:06','2026-06-12 12:00:06','2026-06-12 11:55:08'),(15,'22515d62050043aab7a464c86e3766fb',1,1,'ORDER_1781236596372_726','2026-06-12 11:56:34','2026-06-12 12:01:34','2026-06-12 11:56:36'),(16,'b187ab5b3fde47dba960a51598ec4d00',1,1,'ORDER_1781236618012_2173','2026-06-12 11:56:56','2026-06-12 12:01:56','2026-06-12 11:56:58'),(17,'bf4b238b41654264aadb7edca06e8dfd',1,1,'ORDER_1781236638687_2585','2026-06-12 11:57:17','2026-06-12 12:02:17','2026-06-12 11:57:18'),(18,'f14423dc38f54d2a9d93a1ebfb713303',1,1,'ORDER_1781236877168_7427','2026-06-12 12:01:15','2026-06-12 12:06:15','2026-06-12 12:01:17'),(19,'ce79e3eb18014f3590fd786b398ff3b6',1,1,'ORDER_1781242638754_4728','2026-06-12 13:37:17','2026-06-12 13:42:17','2026-06-12 13:37:18'),(20,'2ce850841616476b86294b1295c520b9',1,2,NULL,'2026-06-12 13:37:47','2026-06-12 13:42:47',NULL),(21,'ba5e1dbe24314422ab53b9c8cb31f902',1,2,NULL,'2026-06-12 13:40:12','2026-06-12 13:45:12',NULL),(22,'4b4375dd8df84853a6a68e600fd3be53',1,1,'ORDER_1781244263283_5200','2026-06-12 14:04:21','2026-06-12 14:09:21','2026-06-12 14:04:23'),(23,'98b54ca908674c4b858f5f36d0086808',1,1,'ORDER_1781244281071_9165','2026-06-12 14:04:39','2026-06-12 14:09:39','2026-06-12 14:04:41'),(24,'5232d99cc1cd43878b1dfb96d6537d51',1,1,'ORDER_1781244401820_9963','2026-06-12 14:06:40','2026-06-12 14:11:40','2026-06-12 14:06:41'),(25,'3617668297294d0db0618e62c3371766',1,2,NULL,'2026-06-12 14:07:00','2026-06-12 14:12:00',NULL),(26,'99fa40eace9143e986b13129d19e0b0c',1,1,'ORDER_1781244544617_5344','2026-06-12 14:09:03','2026-06-12 14:14:03','2026-06-12 14:09:04'),(27,'fd6c6e8e454849c5af0809afb218b132',1,2,NULL,'2026-06-12 14:09:27','2026-06-12 14:14:27',NULL),(28,'55a1b8976aeb47b1ae13490f050021e3',1,1,'ORDER_1781247870745_5288','2026-06-12 15:04:28','2026-06-12 15:09:28','2026-06-12 15:04:30'),(29,'135691e0e29949ffbbfd56465a1eac13',1,1,'ORDER_1781247900004_5240','2026-06-12 15:04:58','2026-06-12 15:09:58','2026-06-12 15:05:00'),(30,'6f45cabdcbed4399a9c31e2afcc5b6d2',1,1,'ORDER_1781248010822_251','2026-06-12 15:06:49','2026-06-12 15:11:49','2026-06-12 15:06:50'),(31,'df2009f4e5ba4bb8ad07bdbdbfda04bd',1,1,'ORDER_1781248023932_1257','2026-06-12 15:07:02','2026-06-12 15:12:02','2026-06-12 15:07:03'),(32,'e7f264b133fa4a5f8200b4333b9f9217',1,2,NULL,'2026-06-12 15:07:29','2026-06-12 15:12:29',NULL),(33,'02c9c0da3a820acc5a63aff5c63e8da9',1,1,'ORDER_1781252765889_4532','2026-06-12 16:26:01','2026-06-12 16:31:01','2026-06-12 16:26:05'),(34,'657bfc08789090370618a08e7c5e7e6e',1,1,'ORDER_1781252905440_9050','2026-06-12 16:28:23','2026-06-12 16:33:23','2026-06-12 16:28:25'),(35,'c2ab5ecb256fd4a772b36a2639449854',1,1,'ORDER_1781252944838_3004','2026-06-12 16:29:02','2026-06-12 16:34:02','2026-06-12 16:29:04'),(37,'33f4adc84f2d04c94ae4984bde003220',1,1,'ORDER_1781252994747_9394','2026-06-12 16:29:53','2026-06-12 16:34:53','2026-06-12 16:29:54'),(38,'528dc5c3f36b810613b659b42936077c',1,1,'ORDER_1781253870342_7557','2026-06-12 16:44:26','2026-06-12 16:49:26','2026-06-12 16:44:30'),(39,'edd05d4c59dabcdf168a10c81eb6402f',1,1,'ORDER_1781253890563_5854','2026-06-12 16:44:48','2026-06-12 16:49:48','2026-06-12 16:44:50'),(40,'96e2d7d4e13fca03aa00fa16cd5c839b',1,1,'ORDER_1781253948033_8573','2026-06-12 16:45:46','2026-06-12 16:50:46','2026-06-12 16:45:48'),(41,'0a855ab7a566150f0d988fec1219773d',1,1,'ORDER_1781253961536_7111','2026-06-12 16:46:00','2026-06-12 16:51:00','2026-06-12 16:46:01'),(42,'faf109ab2da26402cd24117b3ce3d1c9',1,1,'ORDER_1781254102985_7809','2026-06-12 16:48:21','2026-06-12 16:53:21','2026-06-12 16:48:22'),(43,'928e8e2a4811adb19a3fc92a080a2638',1,1,'ORDER_1781254124049_9952','2026-06-12 16:48:42','2026-06-12 16:53:42','2026-06-12 16:48:44'),(44,'12a55aaa392ec16ecec9f4b08a399041',1,1,'ORDER_1781254299978_1418','2026-06-12 16:51:37','2026-06-12 16:56:37','2026-06-12 16:51:40'),(45,'0d7d49bc05462806337a1c8e8a449067',1,1,'ORDER_1781254324599_8288','2026-06-12 16:52:02','2026-06-12 16:57:02','2026-06-12 16:52:04'),(46,'a9c84e53d8f5a77c98095c2fb2ba48af',1,1,'ORDER_1781254531221_6842','2026-06-12 16:55:29','2026-06-12 17:00:29','2026-06-12 16:55:31'),(47,'6b0ce433e081fca315a30ef82dbe75dc',1,1,'ORDER_1781254547983_7878','2026-06-12 16:55:46','2026-06-12 17:00:46','2026-06-12 16:55:47'),(48,'0890ea8e33410ba6bcf941945fb61f2d',1,1,'ORDER_1781254557857_8274','2026-06-12 16:55:56','2026-06-12 17:00:56','2026-06-12 16:55:57'),(49,'aa319330f851a13abbb5081baf172f9b',1,1,'ORDER_1781447792196_6544','2026-06-14 22:36:08','2026-06-14 22:41:08','2026-06-14 22:36:32'),(50,'6b00cfe3a79deec6d81ded9971d2b2d1',1,1,'ORDER_1781507107966_4485','2026-06-15 15:05:01','2026-06-15 15:10:01','2026-06-15 15:05:07'),(51,'c18c1860ccbf576f9935f0cd00145219',1,1,'ORDER_1781507155336_3572','2026-06-15 15:05:52','2026-06-15 15:10:52','2026-06-15 15:05:55'),(52,'b7234b47ae0e4a3eef137f82734ee9e2',1,1,'ORDER_1781507171191_923','2026-06-15 15:06:08','2026-06-15 15:11:08','2026-06-15 15:06:11'),(53,'a2d8a98ead791b11c634a44e12ac4740',1,1,'ORDER_1781507185592_8276','2026-06-15 15:06:23','2026-06-15 15:11:23','2026-06-15 15:06:25'),(54,'362f098054f311ce19814a26ffc867f9',1,2,NULL,'2026-06-15 15:06:42','2026-06-15 15:11:42',NULL),(55,'c9eeed0950892451c1d1f6457a89808a',1,1,'ORDER_1781507213247_662','2026-06-15 15:06:50','2026-06-15 15:11:50','2026-06-15 15:06:53'),(56,'e5cc5e5712c86cdc07bbe2889e65315d',1,1,'ORDER_1781507229624_7917','2026-06-15 15:07:07','2026-06-15 15:12:07','2026-06-15 15:07:09'),(57,'76d8796c6b73745a0f4ab5bdff77c038',1,2,NULL,'2026-09-13 17:23:44','2026-09-13 17:28:44',NULL);

--
-- Table structure for table `xray_traffic_collect`
--

DROP TABLE IF EXISTS `xray_traffic_collect`;
CREATE TABLE `xray_traffic_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `client_ip` varchar(45) DEFAULT NULL COMMENT '客户端IP',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `level` varchar(32) DEFAULT NULL COMMENT '级别 user/inbounds',
  `type` varchar(16) DEFAULT NULL COMMENT '类型 up上传流量/down下载流量',
  `val` bigint DEFAULT NULL COMMENT '值',
  `time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上报时间',
  `status` tinyint DEFAULT '0' COMMENT '状态 0未被统计 1已被统计',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
);

--
-- Dumping data for table `xray_traffic_collect`
--


--
-- Table structure for table `xray_user`
--

DROP TABLE IF EXISTS `xray_user`;
CREATE TABLE `xray_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` varchar(128) NOT NULL COMMENT '用户唯一标识符，UUID',
  `username` varchar(128) NOT NULL COMMENT '用户名，唯一',
  `nick` varchar(45) DEFAULT NULL COMMENT '昵称',
  `country` varchar(45) DEFAULT NULL COMMENT '国家',
  `device_id` varchar(45) DEFAULT NULL COMMENT 'mac地址',
  `password` varchar(256) DEFAULT NULL COMMENT '加密后的密码',
  `email` varchar(128) DEFAULT NULL COMMENT '真实邮箱，可选',
  `unique_email` varchar(128) DEFAULT NULL COMMENT '内部使用的唯一邮箱，不对外暴露',
  `provider` varchar(32) DEFAULT NULL COMMENT '第三方平台名称或本地登录',
  `provider_user_id` varchar(128) DEFAULT NULL COMMENT '第三方平台用户唯一标识符',
  `provider_username` varchar(128) DEFAULT NULL COMMENT '第三方平台上的用户名（昵称）',
  `provider_avatar_url` varchar(256) DEFAULT NULL COMMENT '第三方平台用户头像 URL',
  `status` tinyint DEFAULT '1' COMMENT '账户状态：1 启用，0 禁用',
  `used_traffic` bigint DEFAULT '0' COMMENT '总流量，单位字节',
  `expiration` timestamp NULL DEFAULT NULL COMMENT '有效期',
  `cumulative_months` int DEFAULT '0' COMMENT '累冲月数',
  `type` varchar(32) DEFAULT NULL COMMENT '用户类型',
  `invite_code` varchar(32) DEFAULT NULL COMMENT '邀请码',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `xray_user_uq_username` (`username`),
  UNIQUE KEY `xray_user_uq_uuid` (`uuid`)
);

--
-- Dumping data for table `xray_user`
--

INSERT INTO `xray_user` VALUES (1,'dbc4a0d2-29da-4d00-8734-de14425c3309','lilibestcoder@163.com','u1234567','CN',NULL,'$2a$10$F3knmnDNmWfOGuj43CdDOO9MkJtvEtIUyCZAP3AEk95hI2TF74/hm','test7@example.com','0df10af4-bce4-43a9-a999-4788b14db584@tt.com',NULL,NULL,NULL,NULL,0,0,'2027-07-11 11:12:39',293,'outer','123456','2025-10-20 16:15:44','2026-09-11 19:44:07'),(3,'a1dbb70d-261f-4fa2-b58e-cb040adef22e','1871814749@qq.com','U9L2VZUVV','CN','40:1C:83:48:59:D2','$2a$10$7y1GjhrEhIJiC6f6ppH4mOqXXsfrKLC/0geiQpDtmSr4xnReQCNRm','1871814749@qq.com','a1dbb70d-261f-4fa2-b58e-cb040adef22e@luxwap.com',NULL,NULL,NULL,NULL,0,0,'2025-11-01 08:16:29',0,'inner',NULL,'2025-10-29 08:16:29','2025-11-09 17:22:00'),(4,'37722d41-5880-4335-a2a0-b09d4f4546d8','quxiaojuncuiyue@gmail.com','杨summer','CN','40:1C:83:48:59:D1',NULL,'quxiaojuncuiyue@gmail.com','37722d41-5880-4335-a2a0-b09d4f4546d8@luxwap.com','google','100513416082925115268','杨summer','https://lh3.googleusercontent.com/a/ACg8ocJc-luSjxch9saycf0jz3jiRmBOb8o4BiJYLn8DMMycKSpVAfo=s96-c',0,0,'2025-11-12 17:24:24',0,'outer',NULL,'2025-11-09 17:24:25','2025-11-09 17:24:25');

--
-- Table structure for table `xray_user_session`
--

DROP TABLE IF EXISTS `xray_user_session`;
CREATE TABLE `xray_user_session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `device_id` varchar(128) DEFAULT NULL COMMENT '设备id',
  `os` varchar(64) DEFAULT NULL COMMENT '操作系统',
  `token` varchar(256) DEFAULT NULL COMMENT 'token',
  `status` tinyint DEFAULT '1' COMMENT '状态',
  `token_expiration` timestamp NULL DEFAULT NULL COMMENT 'token过期时间',
  `device_name` varchar(128) DEFAULT NULL COMMENT '设备名称',
  `device_type` varchar(64) DEFAULT NULL COMMENT '设备类型',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `xray_user_session_uq_token` (`token`),
  KEY `xray_user_session_idx_user_id` (`user_id`)
);

--
-- Dumping data for table `xray_user_session`
--

INSERT INTO `xray_user_session` VALUES (1,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','yGHDKJtYWpMUf5nfwbWA0USU29iG7DLm8cfHwyNEmmaIv90omBDF91GI0ZHsxzHlwEZgxpOtHI4nbmTSSPAAzFxYxHoVrpuptJxRnWt3L8NgCeI16RNQKpMM1KtAW7GSupyCPA==',0,'2025-10-21 16:39:05','PC-202509141148','PC','2025-10-20 16:39:04','2025-10-20 16:39:04'),(2,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','NvhTxtehxOIDbGadbJBSyIuK05oW0vS28kvJWTkY1TmRr3jhbIrsPiLJQ5bFfWwDmEsHQjvDKhQj2GvkB7IwdtIsOkk3XGmadrS1/QbydGlyhF2w7937m7Z5gi21fXQkad2Inw==',0,'2025-10-22 16:43:40','PC-202509141148','PC','2025-10-21 16:43:40','2025-10-21 16:43:40'),(3,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','lA8MXVIlGXGaZ2gt1VJPkWALzgzkuLmEtMoE2bq/68Idpb0VwIcfLLOyCTfgE3bdCFuDSZOmIe/x0DaI4ZVWMI2MnVEhGeeqtTS03454Cdf6h05/p5CGBMRk0QNpUcVvr4Yz3w==',0,'2025-10-24 14:35:17','PC-202509141148','PC','2025-10-23 14:35:16','2025-10-23 14:35:16'),(4,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','DidJOhL6viJUVSEH5TVKlEWmEuJOLmvRhAMEEA7uJAIz2zqFnXH8uPwwyDad5jIGnQVKeGxBFcIvAOgyZ45sgqel5FqvnDDIAKIDuGQvsj32I/lIhO7/fd60BQdy5Re5PyrMew==',0,'2025-10-24 14:35:53','PC-202509141148','PC','2025-10-23 14:35:52','2025-10-23 14:35:52'),(5,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','AVsAxNuDIUzrjfmg3J8tUtlmFCLYP5OwWa3mMwu+CvqZjfZmfU/NqV+SvNZt4rkZGpCx5fDf4hywg7cQOfQJMpJauZqYVnv1tDVSCYF/ucC80wDgytaDbViNTl4SrsVjrL8pMQ==',0,'2025-10-25 14:39:00','PC-202509141148','PC','2025-10-24 14:38:59','2025-10-24 14:38:59'),(6,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','MX9yQjsVOITfmoB/jxtNlbff8swqhVoBMMYb184pE9341BYoLd7cpZ2dTkGOtQ2QTr/BJeCrop9stiXUo7KR6vqf+jezWLcUoTTOSdqyQdleTJdw6xzMcqP7etZrTSyW1gclTw==',0,'2025-10-26 14:40:27','PC-202509141148','PC','2025-10-25 14:40:26','2025-10-25 14:40:26'),(7,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','jZp9mzoFVLhOe/Dv7LFxZbLasMaiuYcxA9Vv3Nr7tdsyVS7fuMGLjcYPfKL9GD62lQvHY+JGdx1kxU+unGJhimmYcC3K5Qr3I6dbhRtZhe+4KUQWybHIvFmhheDkkcYLHt/1VA==',0,'2025-10-27 14:52:20','PC-202509141148','PC','2025-10-26 14:52:19','2025-10-26 14:52:19'),(8,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','ksyqGBk9RzL5zieXf6zzyxDdZrfFGTnxMEqgC6O9uMIm7J4kkWTyRjuHlm5wqB8t3RhgmuRHcaqKUpmohiwR21NL9icUWmH9tEDf4yY8foeYJQll0JmsUemmisZMyplWl6xoiw==',0,'2025-10-28 02:14:00','PC-202509141148','PC','2025-10-27 02:14:00','2025-10-27 02:14:00'),(9,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','cER1tm9oeXsRHuGPKV9Ibpxy47Icum6v2RmyS21LiBs9ObRp08pAc0NP4FMhcNUIoKQ108ixfX1V8GA9xkhbySxQ+miD5+gG5NLa+mRVyIWzr4UrexlvvF1R4DyWbEiD1O4CMw==',0,'2025-10-28 02:14:06','PC-202509141148','PC','2025-10-27 02:14:05','2025-10-27 02:14:05'),(10,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','1DNY+TpKDnEtZza+U84QIsqncmUENTaMZoEuO5hVw+osTQ7sY1UMSp6nqH54xsZ60OT31ACU+rGhgEZ+D0KnNKpu1XwWG2ouNFTTIHj7rulfU9BSeCJT9cj5ZSsy9hIIdkz0nA==',0,'2025-10-28 17:07:04','PC-202509141148','PC','2025-10-27 17:07:04','2025-10-27 17:07:04'),(11,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','2qU+rwuj1rtUf5aljQ+QKaJ7tNBKQvBGJIO1IWLhWYbhGCF+UwJZsbiGIXehkpS1UadoHK5DZCl99LsjTqR1Ks+6Iu3HI2FZs4gNgDPy6tZujAJf/4k49Mq7z4H7RwnkRUb6Xw==',0,'2025-10-28 17:15:53','PC-202509141148','PC','2025-10-27 17:15:53','2025-10-27 17:15:53'),(12,2,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','/C7qKG7Kzm6cCr5HAP75QEyBB2XQJ8buwyiQn3j0LJl398lb2TNf/1FhqRgEXvdvCSWZ9Whk3tUtTqVIap7Y+DkFG4SikNQcfrun6MryAVXAz8uNkHahkF0e1BKps8Y+77FVuA==',0,'2025-10-29 16:28:57','PC-202509141148','PC','2025-10-28 16:28:56','2025-10-28 16:28:56'),(13,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','wDfM3IAemKV1YeD8YBPo1mHk74eNluYYlFZ82dBZm+0dO//MkAIOT3TSzFY6HmBDYzURFn9AILonQQZsEQF3G712mSmcZKkS5iKfKvsPQTrVFW5addVE/EXjr8REjyiBCUoaYg==',0,'2025-10-29 17:22:22','PC-202509141148','PC','2025-10-28 17:22:22','2025-10-28 17:22:22'),(14,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','gF9Hdy5GGzP+htBZ04a+VAXFx6prmjvbEu7jGJ2iVsPSBqJuRKUwvlJUSedTFg2H/M750/X1QghJLi34DF9qWHfl/wWowydf1WpZ1qb0OkVVfvxNNnVu3Ot9n0QTObGXd/35Vg==',0,'2025-10-30 06:53:41','PC-202509141148','PC','2025-10-29 06:53:40','2025-10-29 06:53:40'),(15,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','qMGVZXFKDtWHZQyfXRQ5gYk4/ssDb4cKukhvH7p96i/8wWN5CO9Nx5UVxkVMcv5jsYBVcHULs3zVxgLv8XcoPR+lyuU9QcaMs/EXG+DLV66Qg+no+iYugYpAjwXWE9+2lYF/eA==',0,'2025-10-30 06:53:45','PC-202509141148','PC','2025-10-29 06:53:45','2025-10-29 06:53:45'),(16,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','YDCPZcwPbac4Sw1kTv0Ee3fnLlbGRAZE3aD0fNcp9bqAWuO+zrXmjL0nMF+WS4KrNkpS0SC2hYg0GOZsjGX3L7o+uglL9hTIq5AfrsMUtVGa4y4NvQCsitHHErZkjCUKdIxk5w==',0,'2025-10-30 07:03:53','PC-202509141148','PC','2025-10-29 07:03:52','2025-10-29 07:03:52'),(17,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','byn7+DLnPmu6ldtudL4Oa76VQnWGcKlTszs/saGg+3p1ZxQLsLALG/FPawWQZiSLqo0/8+bd+QPPpBPsrscfxR4TsH83rsJNhm8Yjas+wH6iJ7ALkCTLmsDj3iC+Ykq11sNcVA==',0,'2025-10-30 08:08:50','PC-202509141148','PC','2025-10-29 08:08:54','2025-10-29 08:08:54'),(18,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','OPXcImf6CRMUr7tY7irOf8CiY8UI4Ih2jC+zmg+g+C2y0dGHJsNI0MB4ucTg/h+sF205eptDx3ubYFxytUWK5GqadaDhpRiGwlle1jDV4VgZvQDNZbhSWqz9itQd+M1v3nnUdA==',0,'2025-10-30 08:20:16','PC-202509141148','PC','2025-10-29 08:20:16','2025-10-29 08:20:16'),(19,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','shHB9mBvDRNOEVpk7T3K0wEQRY1dqQwpc5dSFnpl8qVO4H/dUV4HUJ3AVe2jWrs3GsIXO/SeFiiJoQ5+Dq5y4RF6522jWkyiI5uZ4P53QIJDHlhaYWrkc14sny7Rh5whOPrR7Q==',0,'2025-10-30 12:37:17','PC-202509141148','PC','2025-10-29 12:37:16','2025-10-29 12:37:16'),(20,3,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','gAoy3lnqP7i0p+DRHBZKpjcP/mGDn0lZNXlEn2pkSNhNsiJEZz8ofjhlK8c6uYi4bXiffeD0mG53Ummcl36qdzBCMCTGobMdxHtceutyHsmZqkac1YimJ+8/Y7iKNybCGG1oEQ==',0,'2025-10-30 13:10:58','PC-202509141148','PC','2025-10-29 13:10:58','2025-10-29 13:10:58'),(21,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','1HhsmAOqbOaLnesCVLmWP4tc1GInw50UkVZ8zL4B9ORC+UC0MWhbC8H6Qp35ntmllCiOA9MVI/rmPMg3zdD/JhpMpLAGy4O/bEMfrixCiZ939s0yGBlb4EXfYoSfwAi792D3vA==',0,'2025-10-30 13:34:11','PC-202509141148','PC','2025-10-29 13:34:10','2025-10-29 13:34:10'),(22,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','NO0yEpDarxokrNzmKpELGQf4/KyIR15cnsMZMZsjsVUNz+g7u3ZRUDIuN1VCUoO1MT025QoTrpVU9/YsFQ2C35VCSvRt13nGcnhl0TrkaC5QvTZgds7irM8LAiNfGQZCpytIog==',0,'2025-10-30 13:39:28','PC-202509141148','PC','2025-10-29 13:39:27','2025-10-29 13:39:27'),(23,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','NwgxWjN4Czp5x+47ZWWHpZwW5vd+U8cTp6UZzD+ghygaGGHp3J0ukXGv1lDYuolM5L7+oI4ObVlDxFi9HUAKJepN3UunKYMgDoLPlCmxpWEW9u1edB5NRab+PwU3vY5Md1Ds1w==',0,'2025-10-30 13:46:11','PC-202509141148','PC','2025-10-29 13:46:10','2025-10-29 13:46:10'),(24,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','jr0JJfNrxAtq+WO1pSbdOOEiOVbabzywxORr4Hw+Apj0I6Xpb9N89ikGXyYQEVvberL6/Z9CcLILcL+JJ5JlAI4FkUyVfOyr7jfnjbLfnV0jEAfNXNL2yaxywo4o5bbRKxx64Q==',0,'2025-10-30 13:52:03','PC-202509141148','PC','2025-10-29 13:52:02','2025-10-29 13:52:02'),(25,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','rtfCdZ5W/lw67udu9o1fg+A4lkykgrL4CUJtz0jcpkAaWXVt8MAa55ugXA0MEs9SORHoGe5juYd4a6YqYTjLxCPrMYxPBuuy0GHvYd5NaWAH1mOMlMgAlOpBaUUZsSQOrBHQKQ==',0,'2025-10-30 16:44:57','PC-202509141148','PC','2025-10-29 16:44:57','2025-10-29 16:44:57'),(26,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','Toak7u9//EORjfXOq6makuPik9bPC/oc2zInOSBrJqe7dI/uyR/y48x6TIDF5rJuphsfC05CVAR9PMvf+jk+q8fWGdb8Wzg6rfATFzjN+J7Yhn/IIhy0gVGEiSkVRiQnz2AhLw==',0,'2025-10-30 19:11:43','PC-202509141148','PC','2025-10-29 19:11:43','2025-10-29 19:11:43'),(27,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','Qfdzf9wCQoQwdjVxJQIZPIJdUMqm5sYibjFgqDhFOzJGwrSsXEV4kF02jZbL8PfbLegfTeyXPLTBO0QXHqcbElpZfoj0GoE8gUtzASv4m0XwAaD/2ZD4kQFEHcoTauxFcg4Euw==',0,'2025-10-31 14:01:20','PC-202509141148','PC','2025-10-31 02:19:19','2025-10-31 14:00:54'),(28,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','LJaaWE5is90gggPo57/cbo1iYq25attOtG/0k6QEASAtHZ0l+ToDOBH7FsEhXwgBJG+Ap0Rq5DWApy1lV9g50jek77RmBbCZZ9wceSCHoje/kIuRhsO+DIG81R1ICEucwVA6Aw==',0,'2025-10-31 14:04:05','PC-202509141148','PC','2025-10-31 14:03:05','2025-10-31 14:03:39'),(29,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','NfMe1m2Bx/cgWVPRmg7GD/7LAqiKZCJTQRLfbyIBr6E9e2huFJAFMFppe/c8zv3Jy/RdFGtvOhXq5iiJk/5x2HY0tZj1gzrmzs2/g+sVHCe/YcIga86/6ujLxpQIOhf1A+WbsA==',0,'2025-10-31 14:06:01','PC-202509141148','PC','2025-10-31 14:05:01','2025-10-31 14:06:40'),(30,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','hrP6kwqWIzA5FD5DLZIwT2pfDJDJ6ZyaeiM1pedBmXV25zKDG0FA5JnGlWXYA2AA3CSde31JIoiiwQBLO79FbzEzsfbzEUdkIYt93udZle54oNadBs05QGMC4z5Ty0wxH1+JFg==',0,'2025-10-31 14:16:10','PC-202509141148','PC','2025-10-31 14:16:10','2025-10-31 14:17:25'),(31,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','8Fu9aUz1ZiXPFybZd6R5z1LQBt5pdkGMFpx8xzVmcGZ4yysKyj91bhtQXZa0AInuKyjqKc5Hfun/HQPVJtzwUDsSKTCgC38TQ/v6M6q91a2zDULUudigaHIWwSCsx5+ZbGcQvw==',0,'2025-11-01 14:18:54','PC-202509141148','PC','2025-10-31 14:18:54','2025-10-31 14:18:54'),(32,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','CSS40RoQN/0fcRTkQjmfqMYIRAEAKKzjRarg8F0MbEGIprjjp1WFpr4qf19ghNZUrLUfTiyeNHmGIKeKSlBhrquM5UWuLp60VvHncdg+tkg7THWjhvLBPnlx/F0IByo/k7Wxcw==',0,'2025-11-01 17:28:26','PC-202509141148','PC','2025-10-31 17:28:26','2025-10-31 17:28:26'),(33,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','C7LJL4C81HYcYLf1YBL9xyhdWVvLlthCRLwmmhnbVAbi4VGcpzrgy3HFQmrot9xVNUJo5MNlRvez+A/Cd0VtFPiuGrFzMhCbh36QJnlRNFY5uOXBj/41f7jr+QUCS3ysikSGtw==',0,'2025-11-01 17:42:05','PC-202509141148','PC','2025-10-31 17:42:05','2025-10-31 17:42:05'),(34,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','jZqq4t4CS0nXrH+OjREDxBlnffzzUoVRkMoSe21RNa5ESSI/vHsOqn/jQKTaAhnYb/9F9NEs3HU7fa1EVlWGQWuxkkAIxMUYAOJdxLCCbT9JZk5Wu5DU18WxjlWss8A8Gtdgvw==',0,'2025-11-04 02:28:48','PC-202509141148','PC','2025-11-03 02:28:48','2025-11-03 02:28:48'),(35,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','cpJyEcj08TashwEU5uk9IVsh6G78HJs8O71YZRzPDC6a1IykJ9f4GMOllVcwJBTNswcrdYxdqdRU9Tb2Eb62Q0/R+bP45KYkglAHCxB5DjKXcYSpVwpQ3QGo9raEKs1SvzbAlQ==',0,'2025-11-05 02:11:31','PC-202509141148','PC','2025-11-04 02:11:31','2025-11-04 02:11:31'),(36,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','naRCzd+iXAK/+t4rw5pjmsVZiyhAjSU9Qn5ynWdGllxsHWuO2o+evdfbTvEEeutdGi+0ZsWyTqsDGEb9wg5TNw1Am1v9hsrPq+lLMSVjiQrlOq8rm5ufAepVwI+PQ9YvSwpT/Q==',0,'2025-11-09 15:16:06','PC-202509141148','PC','2025-11-08 15:16:06','2025-11-08 15:16:06'),(37,4,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','xC4zjtLc7jNOqqZez8cgMs1PstpaqNtJQeXw2LgLEesRFNloH56fz1BUUWCp4wiSDKEYYAu4GF+EqAy07sbAUFVyKz2B0T9PIx8o3vazD0PCChUyvN+EEytc+bgDjEWi4ic48Q==',0,'2025-11-10 17:31:34','PC-202509141148','PC','2025-11-09 17:31:43','2025-11-09 17:31:43'),(38,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','SGmPLy0lq/r3gi64PryKJzvE1rkxm9HOebWDCf5XNXcOeiQfi6+XKlOjxzqnGTRkvmq+5BdpsvR0ZrpMvFIRGvYR/vzaQmnGqQf6RkQosH4F0/JhZb7wKkQcZCcAL/IU/mPbtw==',0,'2025-11-10 17:57:26','PC-202509141148','PC','2025-11-09 17:57:26','2025-11-09 17:57:26'),(39,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','lNT7tjsD1F8ZOwMkbi9/Q1PEvUN1iZ8CRSu5jc5nTipNNJi3cVdQpduCnumxjtoP/Oit3sSLTqVb3ZKk73bZ29yebkfO3+89ZvSlgLU6/F5iGt0GfyVsimXlQFTILYzyW7EdnQ==',0,'2025-11-10 17:57:55','PC-202509141148','PC','2025-11-09 17:57:54','2025-11-09 17:57:54'),(40,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','uADpxy08svIq8vU9fhtBF0yB/MhqYyJYBHS473p96bDlzPDrA5+EqP2iIujCd8KW9rEuTZNEFZLmTOFv9KMDJSit+IsiiDCUrn7TTaoA7y4PxLb1Ye0dvEPeO4UEBPyLqBqHkA==',0,'2025-11-11 14:19:35','PC-202509141148','PC','2025-11-10 14:19:34','2025-11-10 14:19:34'),(41,1,'40:1C:83:48:59:D1','Microsoft Windows NT 10.0.22631.0','Ti3ec91b6DsZv05kWN814suOh94yx45EJaFFFVr1NVIQ2rt3wxmLjA3Az3OKI3UkLmMaMg+/jM3E5DG52ZMkfkDLSL2D5ch5cdsCqlIfRdW2ZkDBYTR41Xpn5IjEYpC6bnNKVQ==',0,'2025-11-11 14:26:33','PC-202509141148','PC','2025-11-10 14:26:33','2025-11-10 14:26:33'),(42,1,'flutter-windows','windows','spFhQSK+CAnU1sQQDPH+P7MrwqGJqG6GhIz9rT6CRSznugxDTG+jXnkl/dC/Ai9fbTPjH0l8D/O3zoJadPZZsxQeDVaVXWoUycIlzWgblcSuHBciZC9MNPrrfM3tR9b7nsIKVA==',0,'2026-06-09 03:41:28','Windows PC','desktop','2026-06-08 03:41:27','2026-06-08 03:41:27'),(43,4,'PC-202509141148','\"Windows 10 Pro\" 10.0 (Build 22631)','UHSp0XmWRyAblOi6gfXLYPtdrdK5fz1VqTOVmGz/oBHGfjHOr4J3zBoldmLXG7tBD+gbWXA2CotbI4zGR8ONHiHxt2qhUC2fSQ5wtRbZZnEMoEK2/Sm47PT3OD7cZ1gA+T26ZA==',0,'2026-06-10 06:42:51','PC-202509141148','PC','2026-06-09 06:42:51','2026-06-09 06:42:51'),(44,1,'flutter-windows','windows','uPYnjFPIA+3/pqMuS5CAqiqMADdQfiIW6iJ8PdH7XLifQIvwTVEDQKhK/KDTCFkhjzhy6MXyn8b7i2XDHkNmeKjIjf1l0inWnUIv5ZWmh1NNZ5+OK9f4m74QKXwsECr4c0CEAQ==',0,'2026-06-12 06:11:35','Windows PC','desktop','2026-06-11 06:11:34','2026-06-11 06:11:34'),(45,1,'flutter-windows','windows','f8nW5Jexzm2pkmlkgyyYMRFTvMUwcdWma4DXBqGikXCHFqrdkiATRV/tb1xaTcvmwKk1Fd6r/IU71+ej1KK3V4/yNt0rlI8EnDp+sSXKERSEyODy9KfLwzbzDeanb+s5gcjRRA==',0,'2026-06-13 07:04:00','Windows PC','desktop','2026-06-12 07:03:59','2026-06-12 07:03:59'),(46,1,'flutter-windows','windows','PBSUymDNe2bhHeBCfEIxRACuiz74sgcXeL4NqFpJaAvwTtX7ULqT/xHQqCdKbfZqwu9eBN4waPyMWEZeZ8uskf1Clr9JCuyd/gPL3nartOuY+Dg6QuIRWsfEuJgpqDSZfwVBPQ==',0,'2026-06-13 08:21:16','Windows PC','desktop','2026-06-12 08:21:16','2026-06-12 08:21:16'),(47,1,'flutter-windows','windows','nfhiagLgNFeQodpKeuvo+RyBKDj1wD/4pvGwko61HyJkbM8sw0DWdwk+MpOWr7zm6cl7CZF7YR12Q6t9BEgSb+OpP2w9Xe+ixnraRlkl7O9tHC0b6q8pcEcsbzGqtwuzWjeOUw==',0,'2026-06-13 10:25:31','Windows PC','desktop','2026-06-12 10:25:31','2026-06-12 10:25:31'),(48,1,'flutter-windows','windows','UyC1OzdmunSXEv9uDY1OTaKPY0W9zYeLSht3B4dH6UiSU3p2cRxdB5WnZnjk1Np8s5DfuOILMcCYRlTUC6Lxh1DnUb7MbFHkec47zXE5IbzqtjOQDGNBrPizhPz/grbZ3OsqVw==',0,'2026-06-15 14:18:17','Windows PC','desktop','2026-06-14 14:18:16','2026-06-14 14:18:16'),(49,1,'flutter-windows','windows','hD1ATD3DDsdh0KM7fxjGVoAEhlAVl6DBKSEWdVdNNCO11A0DD9A4FrdsHRIThUf6m76FC84E2WBujqRK/wHAssEJcEzpUA/TzaqU8M5ULkRa+75H7WdGNUIdouczo6q+fbTgNA==',0,'2026-06-15 14:35:05','Windows PC','desktop','2026-06-14 14:35:04','2026-06-14 14:35:04'),(50,1,'flutter-windows','windows','EtmADL8RTpgHKQC1gU0tCn1rG9sBoKGa7J/RtFLCljeik4Xhzh+bXmZRbP6W+AxkHGM5vnyYe4fqXKnyFLb/2GD5aGqpVdb3lTtmrZT6KuFA7Mcabr9EAZDzlFk4xl00hxt3Dg==',0,'2026-06-15 14:53:26','Windows PC','desktop','2026-06-14 14:53:25','2026-06-14 14:53:25'),(51,1,'AmapChandeMac-mini.local','macos','V0Q0Dn8Zj43DaQh+W0NMhk2eM25G4OQrujGP2gL+H0bAKWckQSbFosQ1P0Fx0RhD9xx0mfARbzKb4Kurgm5SN2K5btqD0lpVT3SIYImE4i6AyFSXJzq8cURpAEn4Zh5CjWQ9Bg==',0,'2026-06-16 07:03:10','macos Mac','desktop','2026-06-15 07:03:09','2026-06-15 07:03:09'),(52,1,'PC-202509141148','windows','d2akoYwAeJ3ShrDBzvUBuFR7TPn/7PuOlSLoOooXgJ8I8leFpWP4yRe5xC02SL3xeG0EDIOpYgh8b3Hc37Y/ZNn+6O8vsKRJBYgPN86497OllIrSFQEtVQK+NfP1JaiwF7XCAw==',0,'2026-06-16 07:50:24','windows PC','desktop','2026-06-15 07:50:23','2026-06-15 07:50:23'),(53,1,'flutter-windows','windows','L5rPfS3yML1L/Z/JoJyddMqWwkr48GLs2QVSSz0/CuiLSTsafHFZcZrbxjnAVqbE5v6mWWD2KaC6OrDI1hxp3FJ1QoHV+wpvJZNlTAFmWFVxZe/APXRgjdRAeKulsdmmXnPe8w==',0,'2026-09-05 01:49:44','Windows PC','desktop','2026-09-04 01:49:43','2026-09-04 01:49:43'),(54,1,'test-device-001','Windows','9Mt5oRW07huWBzsTH0AmuBiYbIpRA31qCXdx5Y4jg6ohj8cJ3fL0FQHDzh5gC1BfFSOw6AaZEQciJ6B2LiQNtYa3CqxzetvloABBF7PDtgK8WkLPxEdx2zNN7ZuMhFcQw+uCxA==',0,'2026-09-09 04:15:08','TestPC','pc','2026-09-08 04:15:08','2026-09-08 04:15:08'),(55,1,'flutter-windows','windows','+3uSCYhk77DeHr6zEKOVloup+93Fupz+VcQtqzuvtATwIN5jh0sFhVI3KK5oj/Gs9DIJM+td+qdXIY9TVekVIAIl6sFe3cqQOwuJWsUHa3+m3JyzYAaHsOKRwmq1e30qlGVOcQ==',0,'2026-09-09 05:14:45','Windows PC','desktop','2026-09-08 05:14:44','2026-09-08 05:14:44'),(56,1,'test_device','windows','BDQldrObEhSC6BR9Nqdv/iRtx6gM3dc+axXi0Ix/FPpdywm+JPS/G9RAb6C/gevUMG/BY9XEs5+wMjV2oIEjwzhxa7C3W3oKUZLrip8E0VJexV3Xow0Lqn9engjYgf36f+AWog==',0,'2026-09-09 05:47:01','test_pc','desktop','2026-09-08 05:47:01','2026-09-08 05:47:01'),(57,1,'py_test_device','windows','LxxpKVlGSsprfBXn/TeezKSGFkSft3ladz7P3rIqGsjiqICvIqD9lDXlLoKVZYOgj47IFSDDfwZNISV+ZhDf+TSHkUv/uI565H2isMpzQ23Ko7QUYN5j6kTQ8nFRx4AdRrVvwg==',0,'2026-09-09 05:54:43','py_test_pc','desktop','2026-09-08 05:54:43','2026-09-08 05:54:43'),(58,1,'test-device-pc','windows','JssvbelY2bZVXc1BY8/I0lhnIiV91S4IlXatsZkC5k5LpucNyEMKb0FyC40qII+8ztNlP+AmTsRyymWyG6rA0m9bcc7cmbQJGLeWmqqQ/JrFAkdrWvVn2hl2g1y8z3qfLKZldA==',0,'2026-09-12 08:01:07','test-pc','PC','2026-09-11 08:01:07','2026-09-11 08:01:07'),(59,1,'test-runner-pc','windows','u1bpt7MUrrQwmSor5QgYulDOpYFFRhS9rSITQkpxmm1SFeCp/7OmP7QCzSQCb7edK4Nlex6yqNP2IJkFn6za43DZlNDL6NlRRCSDhIw8j9noddtj6usruQ6RgefK+wkXXVNi3w==',0,'2026-09-12 08:04:22','test-runner','PC','2026-09-11 08:04:22','2026-09-11 08:04:22'),(60,1,'test','windows','e87xxdUU4YvZt+axdTAWGgdBPFrT84FkaxeNR2AeEEHBfMTi0bgfugzD1/TQUvK+zDbRdL5Dkr94sIDueBRA00eUu+5C5D1QEOKdp7j0YEflj77YrnUhh9ZNIucgyIUd5ThqWQ==',0,'2026-09-12 08:29:02','test','PC','2026-09-11 08:29:01','2026-09-11 08:29:01'),(61,1,'raej6iocw6qvgxg','windows','aO5C80Iml3J4LS/KDf04pfMMJSNtpIFTcr7sqY4W82g9Qq0EoPZXYOSya+CGy7HcLTvX0C3mElVU93yv75bCPtqlT7Y7b+L2i3JJx0B3YawRdFYbTrGazIoF+iZuVyiSiKHXlg==',0,'2026-09-12 17:42:57','windows PC','desktop','2026-09-11 17:42:56','2026-09-11 17:42:56'),(62,1,'test-runner-pc','windows','1xuoqdd1PWuP9hqZ2v1wFyu4KWCakMXl4l600r2nnFu7qFuuUYdeeuSEy6t2NI/sE3kqaB3gSC82nC5yrzgIXE4m9QE2AvnSYIYHoIXkCH0CkkvfriYnKCxKQkxV5MWEUBElWA==',0,'2026-09-13 15:45:56','test-runner','PC','2026-09-12 15:45:56','2026-09-12 15:45:56'),(63,1,'raej6iocw6qvgxg','windows','v1pCsfTr6ji669+vyNA/mVUXRYQ24p/XYjKcjyBY5FTvU3sLISe6GOZvXGgTzP284hBqpEEZZwuHxlRTYDt5YG7TTWkZeJZznijnSgZCtWRPe2/AmvsL2ZDDYG1U6o5gkJhT5w==',0,'2026-09-13 18:00:47','windows PC','desktop','2026-09-12 18:00:47','2026-09-12 18:00:47'),(64,1,'AmapPC','windows','R/jp79S1CgF8pByZeCFiMFe3ymfD5/Jae8utt5IGulQvbOvcKOtIaBXunId1zw6XkkNvIoDR1hRfUt7yIJL7xL51+Hx9oHC0nsSoX1dbx/kCXfohkeR2vnR0XtWrMXbWCmdkkg==',0,'2026-09-14 07:11:22','windows PC','desktop','2026-09-13 07:11:22','2026-09-13 07:11:22'),(999,1,'local-device-id','Windows','valid_test_token_2030',0,'2030-01-01 00:00:00','LocalPC','desktop',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);


DROP TABLE IF EXISTS `xray_chain_proxy_node`;
CREATE TABLE `xray_chain_proxy_node` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '所属用户ID',
  `source_type` tinyint NOT NULL DEFAULT '1' COMMENT '端点来源：1-用户自建导入，2-平台统一分发',
  `protocol` varchar(20) NOT NULL DEFAULT 'socks5' COMMENT '代理协议：socks5, http, https',
  `host` varchar(255) NOT NULL COMMENT '代理主机',
  `port` int NOT NULL COMMENT '代理端口',
  `username` varchar(128) DEFAULT '' COMMENT '认证账号',
  `password_cipher` varchar(512) DEFAULT '' COMMENT '认证密码',
  `remark` varchar(128) DEFAULT '' COMMENT '用户自定义备注',
  `exit_ip` varchar(64) DEFAULT '' COMMENT '实际出网探测IP',
  `country_code` varchar(10) DEFAULT '' COMMENT '出口国家代码',
  `alive_status` tinyint NOT NULL DEFAULT '0' COMMENT '存活状态：0-未检测，1-可用有效，2-不可达',
  `latency_ms` int DEFAULT '0' COMMENT '探测延迟',
  `refresh_url` varchar(512) DEFAULT '' COMMENT '动态住宅代理刷新提取URL',
  `last_check_time` timestamp NULL DEFAULT NULL COMMENT '最近一次查活时间',
  `sort_order` int DEFAULT '0' COMMENT '排序权重',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
);

INSERT INTO `xray_chain_proxy_node` (`id`, `user_id`, `source_type`, `protocol`, `host`, `port`, `username`, `password_cipher`, `remark`, `exit_ip`, `country_code`, `alive_status`, `latency_ms`, `refresh_url`, `last_check_time`, `sort_order`, `del_flag`, `create_by`, `create_time`, `update_time`) VALUES
(1, 1, 1, 'socks5', '45.74.31.23', 7782, '', '', 'GitHub开源S5跳板1', '45.74.31.23', 'US', 1, 120, '', CURRENT_TIMESTAMP, 0, '0', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 1, 'socks5', '8.213.134.213', 443, '', '', 'GitHub开源S5跳板2', '8.213.134.213', 'SG', 1, 85, '', CURRENT_TIMESTAMP, 0, '0', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

DROP TABLE IF EXISTS `xray_chain_proxy_config`;
CREATE TABLE `xray_chain_proxy_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `is_enabled` tinyint NOT NULL DEFAULT '0' COMMENT '是否启用链式代理',
  `active_node_id` bigint DEFAULT NULL COMMENT '当前激活的上游跳板端点ID',
  `exit_line_id` bigint DEFAULT NULL COMMENT '绑定的落地出口线路ID',
  `mode` varchar(32) NOT NULL DEFAULT 'fixed_exit' COMMENT '激活模式',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_chain_config_user_id` (`user_id`)
);

INSERT INTO `xray_chain_proxy_config` (`id`, `user_id`, `is_enabled`, `active_node_id`, `mode`, `create_time`, `update_time`) VALUES
(1, 1, 1, 1, 'fixed_exit', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Dump completed on 2026-09-13 20:54:17

SET FOREIGN_KEY_CHECKS = 1;