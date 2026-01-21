-- 1. 创建数据库（若不存在）
CREATE DATABASE IF NOT EXISTS campus_security_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_security_platform;

-- 2. 创建角色表（t_role）- 匹配 Role.java Entity
CREATE TABLE IF NOT EXISTS t_role (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 3. 创建用户表（t_user）- 匹配 User.java Entity
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '电话',
    role_id BIGINT(20) DEFAULT NULL COMMENT '角色ID',
    department VARCHAR(100) DEFAULT NULL COMMENT '部门',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    status INT DEFAULT 1 COMMENT '状态：1正常，0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (id),
    KEY idx_role_id (role_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted),
    CONSTRAINT fk_user_role_id FOREIGN KEY (role_id) REFERENCES t_role (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 4. 创建设备表（t_device_info）- 匹配 DeviceInfo.java Entity
CREATE TABLE IF NOT EXISTS t_device_info (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '设备ID',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_type VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
    location VARCHAR(200) DEFAULT NULL COMMENT '设备位置',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    status INT DEFAULT 1 COMMENT '设备状态：1在线，0离线',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_device_type (device_type),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备信息表';

-- 5. 创建报警事件表（t_alarm_event）- 匹配 AlarmEvent.java Entity
CREATE TABLE IF NOT EXISTS t_alarm_event (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '报警ID',
    device_id BIGINT(20) DEFAULT NULL COMMENT '设备ID',
    alarm_type VARCHAR(50) DEFAULT NULL COMMENT '报警类型',
    alarm_level INT DEFAULT 0 COMMENT '报警级别：0低，1中，2高',
    description VARCHAR(500) DEFAULT NULL COMMENT '报警描述',
    status INT DEFAULT 0 COMMENT '处理状态：0未处理，1已处理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
    handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
    handler_id BIGINT(20) DEFAULT NULL COMMENT '处理人ID',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_device_id (device_id),
    KEY idx_alarm_type (alarm_type),
    KEY idx_alarm_level (alarm_level),
    KEY idx_status (status),
    KEY idx_create_time (create_time),
    KEY idx_deleted (deleted),
    CONSTRAINT fk_alarm_device_id FOREIGN KEY (device_id) REFERENCES t_device_info (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报警事件表';

-- 6. 创建报警处理表（t_alarm_disposal）- 匹配 AlarmDisposal.java Entity
CREATE TABLE IF NOT EXISTS t_alarm_disposal (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '处理ID',
    alarm_id BIGINT(20) NOT NULL COMMENT '报警ID',
    handler_id BIGINT(20) DEFAULT NULL COMMENT '处理人ID',
    action VARCHAR(200) DEFAULT NULL COMMENT '处理方案',
    description VARCHAR(500) DEFAULT NULL COMMENT '处理备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '处理时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_alarm_id (alarm_id),
    KEY idx_handler_id (handler_id),
    KEY idx_deleted (deleted),
    CONSTRAINT fk_disposal_alarm_id FOREIGN KEY (alarm_id) REFERENCES t_alarm_event (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报警处理表';

-- 7. 创建传感器数据表（t_sensor_data）- 匹配 SensorData.java Entity
CREATE TABLE IF NOT EXISTS t_sensor_data (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '数据ID',
    device_id BIGINT(20) DEFAULT NULL COMMENT '设备ID',
    sensor_type VARCHAR(50) DEFAULT NULL COMMENT '传感器类型',
    value DECIMAL(10,2) DEFAULT NULL COMMENT '传感器数值',
    unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '采集时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_device_id (device_id),
    KEY idx_sensor_type (sensor_type),
    KEY idx_create_time (create_time),
    KEY idx_deleted (deleted),
    CONSTRAINT fk_sensor_device_id FOREIGN KEY (device_id) REFERENCES t_device_info (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='传感器数据表';

-- 8. 创建学生举报表（t_student_report）- 匹配 StudentReport.java Entity
CREATE TABLE IF NOT EXISTS t_student_report (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    student_id VARCHAR(50) DEFAULT NULL COMMENT '学生ID',
    report_type VARCHAR(50) DEFAULT NULL COMMENT '举报类型',
    content VARCHAR(500) DEFAULT NULL COMMENT '举报内容',
    location VARCHAR(200) DEFAULT NULL COMMENT '事件位置',
    status INT DEFAULT 0 COMMENT '处理状态：0未处理，1已处理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
    handler_id BIGINT(20) DEFAULT NULL COMMENT '处理人ID',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_report_type (report_type),
    KEY idx_status (status),
    KEY idx_create_time (create_time),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生举报表';

-- 初始化基础数据
INSERT INTO t_role (role_name, description) VALUES
('管理员', '系统管理员，拥有所有权限'),
('安保人员', '校园安保人员'),
('学生', '普通学生用户'),
('系统监管员', '系统监管'),
('设备管理员', '设备维护管理')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name);

-- 初始化管理员用户（密码: 123456）
INSERT INTO t_user (username, password, email, phone, role_id, department, status) VALUES
('admin', '123456', 'admin@smartcampus.com', '13800138000', 1, '管理部门', 1),
('security', '123456', 'security@smartcampus.com', '13800138001', 2, '安保部门', 1),
('monitor', '123456', 'monitor@smartcampus.com', '13800138002', 4, '监管部门', 1)
ON DUPLICATE KEY UPDATE username=VALUES(username);
