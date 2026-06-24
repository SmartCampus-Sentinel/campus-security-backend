-- =============================================
-- 校园智能安防平台 - 数据库初始化脚本
-- 数据库: campus_security
-- 字符集: utf8mb4
-- =============================================

CREATE DATABASE IF NOT EXISTS campus_security DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_security;

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    role_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    permissions TEXT         COMMENT '权限标识列表(JSON或逗号分隔)',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限表';

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    role_id     BIGINT       COMMENT '角色ID',
    phone       VARCHAR(20)  COMMENT '手机号',
    status      TINYINT(1)   DEFAULT 1 COMMENT '状态(1:启用 0:禁用)',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT(1)   DEFAULT 0 COMMENT '逻辑删除(0:未删 1:已删)',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 设备信息表
-- ----------------------------
DROP TABLE IF EXISTS device_info;
CREATE TABLE device_info (
    device_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '设备ID',
    device_code    VARCHAR(50)  NOT NULL COMMENT '设备编号(唯一标识)',
    device_type    VARCHAR(20)  COMMENT '类型(camera:摄像头, sensor:传感器)',
    location       VARCHAR(200) COMMENT '安装位置',
    ip_address     VARCHAR(50)  COMMENT 'IP地址',
    status         TINYINT(1)   DEFAULT 0 COMMENT '状态(1:在线 0:离线)',
    heartbeat_time DATETIME     COMMENT '最后心跳时间',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '安装时间',
    PRIMARY KEY (device_id),
    UNIQUE KEY uk_device_code (device_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';

-- ----------------------------
-- 报警事件记录表
-- ----------------------------
DROP TABLE IF EXISTS alarm_event;
CREATE TABLE alarm_event (
    alarm_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '报警ID',
    device_id      BIGINT       COMMENT '关联设备ID',
    alarm_type     VARCHAR(50)  COMMENT '报警类型(如:消防通道占用)',
    risk_level     TINYINT      DEFAULT 3 COMMENT '风险等级(1:紧急 2:重要 3:一般)',
    location       VARCHAR(200) COMMENT '报警位置',
    alarm_time     DATETIME     COMMENT '报警时间',
    status         TINYINT      DEFAULT 0 COMMENT '处置状态(0:未处置 1:处置中 2:已完成)',
    screenshot_url VARCHAR(500) COMMENT '现场截图URL',
    video_url      VARCHAR(500) COMMENT '关联视频片段URL',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    PRIMARY KEY (alarm_id),
    KEY idx_device_id (device_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警事件记录表';

-- ----------------------------
-- 报警处置记录表
-- ----------------------------
DROP TABLE IF EXISTS alarm_disposal;
CREATE TABLE alarm_disposal (
    disposal_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '处置ID',
    alarm_id         BIGINT       NOT NULL COMMENT '关联报警ID',
    disposer_id      BIGINT       COMMENT '处置人ID(User ID)',
    disposal_time    DATETIME     COMMENT '处置时间',
    disposal_content TEXT         COMMENT '处置说明',
    result_photo_url VARCHAR(500) COMMENT '处置结果照片URL',
    PRIMARY KEY (disposal_id),
    KEY idx_alarm_id (alarm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警处置记录表';

-- ----------------------------
-- 传感器实时数据表
-- ----------------------------
DROP TABLE IF EXISTS sensor_data;
CREATE TABLE sensor_data (
    data_id             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '数据ID',
    device_id           BIGINT        NOT NULL COMMENT '设备ID',
    smoke_concentration DECIMAL(10,2) COMMENT '烟雾浓度',
    temperature         DECIMAL(10,2) COMMENT '温度',
    is_abnormal         TINYINT(1)    DEFAULT 0 COMMENT '是否异常(1:是 0:否)',
    collect_time        DATETIME      COMMENT '采集时间',
    PRIMARY KEY (data_id),
    KEY idx_device_id (device_id),
    KEY idx_abnormal (is_abnormal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传感器实时数据表';

-- ----------------------------
-- 学生隐患上报表
-- ----------------------------
DROP TABLE IF EXISTS student_report;
CREATE TABLE student_report (
    report_id    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '上报ID',
    student_id   VARCHAR(50)  NOT NULL COMMENT '学生ID(或OpenID)',
    report_type  VARCHAR(50)  COMMENT '隐患类型',
    location     VARCHAR(200) COMMENT '位置描述',
    description  TEXT         COMMENT '详细描述',
    media_url    VARCHAR(500) COMMENT '多媒体文件URL(逗号分隔)',
    report_time  DATETIME     COMMENT '上报时间',
    audit_status TINYINT      DEFAULT 0 COMMENT '审核状态(0:待审核 1:已处理 2:驳回)',
    PRIMARY KEY (report_id),
    KEY idx_student_id (student_id),
    KEY idx_audit_status (audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生隐患上报表';

-- ----------------------------
-- 初始化数据
-- ----------------------------
INSERT INTO sys_role (role_name, permissions) VALUES
('超级管理员', '*'),
('安保人员', 'alarm:view,alarm:dispose,device:view'),
('普通用户', 'report:submit,report:view');

-- 默认管理员: admin / 123456 (BCrypt加密，如需重新生成可运行 BcryptHashTool）
INSERT INTO sys_user (username, password, role_id, phone, status) VALUES
('admin', '$2a$10$EqKcp1WFKVQIShMPC7B3kuznX9gAZMsVnSNjN0ABNuHVBCpzqABGe', 1, '13800000000', 1);
