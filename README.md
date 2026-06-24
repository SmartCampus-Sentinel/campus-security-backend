# 项目名称：校园智能安防平台-后端
## 1. 项目介绍
- 所属项目：校园智能安防平台
- 模块职责：提供平台全量后端服务，整合三大核心模块：
  1. 核心业务服务：接口层、业务逻辑层、数据访问层，负责用户权限、报警管理等核心业务
  2. AI分析服务：基于YOLOv8/OpenPose模型的安全事件检测，提供推理接口
  3. 数据采集服务：对接摄像头、传感器，采集视频流和环境数据
- 关联仓库：无项目内依赖仓库，为前端仓库提供接口支持

## 2. 技术栈
### 2.1 核心业务服务技术栈
- 开发语言：Java 17
- 核心框架：Spring Boot 3.x、Spring Security、MyBatis-Plus
- 数据存储：MySQL 8.0（结构化数据）、Redis 6.2（缓存）
- 其他组件：RabbitMQ（消息队列）、Swagger3（接口文档）、Logback（日志）
### 2.2 AI分析服务技术栈
- 开发语言：Python 3.14
- 核心框架：PyTorch 2.0+、FastAPI、OpenCV 4.8+
- 模型：YOLOv8（目标检测）、OpenPose（姿态识别）
### 2.3 数据采集服务技术栈
- 开发语言：Java + Python
- 核心组件：FFmpeg（视频流转封装）、EMQ X（MQTT服务器）

## 3. 目录结构
```
src/
├── core/（核心业务服务模块）
│   ├── src/main/java/com/campus/security/core/
│   │   ├── controller/（接口层：AlarmController、DeviceController等）
│   │   ├── service/（业务逻辑层：接口+实现）
│   │   ├── mapper/（数据访问层）
│   │   ├── model/（数据模型：实体类、DTO、VO）
│   │   ├── config/（核心配置：Security、MyBatis等）
│   │   ├── utils/（核心工具类）
│   │   └── CoreApplication.java（核心服务启动类）
│   └── src/main/resources/（核心配置资源）
│       ├── application.yml（核心配置）
│       └── sql/（初始化SQL脚本）
├── ai/（AI分析服务模块）
│   ├── src/
│   │   ├── model/（模型文件：ONNX格式）
│   │   ├── config/（模型配置）
│   │   ├── service/（推理服务）
│   │   ├── api/（FastAPI接口）
│   │   ├── utils/（AI工具类）
│   │   └── main.py（AI服务启动类）
│   ├── requirements.txt（Python依赖）
│   └── train.py（模型训练脚本）
├── data-collection/（数据采集服务模块）
│   ├── src/main/java/com/campus/security/data/
│   │   ├── controller/（数据采集接口）
│   │   ├── service/（采集服务：视频、传感器）
│   │   ├── config/（MQTT、FFmpeg配置）
│   │   └── DataCollectionApplication.java（采集服务启动类）
│   └── src/main/resources/（采集服务配置）
├── config/（全局配置文件目录）
│   ├── .env.example（环境变量示例）
│   └── docker-compose.yml（Docker部署配置）
├── utils/（通用工具类目录）
│   ├── common/（通用工具：日期、加密等）
│   └── http/（HTTP请求工具）
└── test/（测试代码目录）
    ├── core/（核心服务测试）
    ├── ai/（AI服务测试）
    └── data/（采集服务测试）
docs/（本地辅助文档）
pom.xml（Java项目依赖）
README.md
```

## 4. 核心功能实现

### 4.1 事务管理配置
- **配置文件**: `src/main/java/com/smartcampus/config/TransactionConfig.java`
- **功能**: 配置Spring事务管理器，确保MyBatis SqlSession和JDBC连接被Spring正确管理
- **关键注解**: `@EnableTransactionManagement` + `DataSourceTransactionManager` Bean
- **解决问题**: 消除"SqlSession was not registered for synchronization"警告，确保事务正确提交

### 4.2 登录认证接口
- **控制器**: `src/main/java/com/smartcampus/controller/AuthController`
- **接口地址**: `POST /api/auth/login`
- **业务逻辑**:
  - 接收用户名和密码
  - 调用UserService验证用户凭证
  - 生成UUID令牌（建议后续改为JWT）
  - 返回用户ID、用户名和令牌

### 4.3 用户认证Service
- **业务逻辑**: `src/main/java/com/smartcampus/service/impl/UserServiceImpl`
- **@Transactional(readOnly=true)**: 登录查询使用只读事务，提升性能
- **数据库查询**: 使用MyBatis-Plus LambdaQueryWrapper进行用户验证

## 5. 开发环境搭建
### 5.1 前置依赖
- 需安装：
  1. Java 17+、Maven 3.8+（核心业务服务）
  2. Python 3.14、pip 22+（AI分析服务）
  3. MySQL 8.0、Redis 6.2（数据存储）
  4. FFmpeg（视频处理，需配置环境变量）
  5. Git（版本控制）
- 需部署：RabbitMQ 3.10+（消息队列，可通过Docker快速部署）

### 4.2 环境配置步骤
1. 克隆仓库：`git clone https://github.com/【组织名称】/campus-security-backend.git`
2. 进入项目目录：`cd campus-security-backend`
3. 安装依赖：
   - 核心业务服务：`mvn clean install`（根目录执行，自动安装Java依赖）
   - AI分析服务：`cd src/ai && pip install -r requirements.txt`
4. 配置环境变量：
   - 复制config/.env.example为config/.env
   - 修改核心配置（数据库、Redis、MQTT等）：
     ```
     # 数据库配置
     DB_HOST=localhost
     DB_PORT=3306
     DB_NAME=campus_security
     DB_USERNAME=root
     DB_PASSWORD=123456
     # Redis配置
     REDIS_HOST=localhost
     REDIS_PORT=6379
     REDIS_PASSWORD=
     # MQTT配置
     MQTT_HOST=localhost
     MQTT_PORT=1883
     MQTT_USERNAME=mqtt_admin
     MQTT_PASSWORD=mqtt_123456
     # 服务端口配置
     CORE_SERVICE_PORT=8080
     AI_SERVICE_PORT=8081
     DATA_COLLECTION_PORT=8082
     ```
5. 初始化数据：
   - 启动MySQL，创建数据库campus_security
   - 执行src/core/src/main/resources/sql/init.sql脚本初始化表结构和基础数据

## 5. 启动方式
### 5.1 开发环境启动（按模块启动）
1. 核心业务服务：
   - 方式1：IDE启动（如IDEA），运行src/core/src/main/java/com/campus/security/core/CoreApplication.java
   - 方式2：命令行启动：`mvn -f src/core/pom.xml spring-boot:run`
   - 启动成功后，接口文档访问地址：http://localhost:8080/swagger-ui.html
2. AI分析服务：
   - 进入AI模块目录：`cd src/ai`
   - 启动命令：`python main.py`
   - 启动成功后，AI接口文档访问地址：http://localhost:8081/docs
3. 数据采集服务：
   - 方式1：IDE启动，运行src/data-collection/src/main/java/com/campus/security/data/DataCollectionApplication.java
   - 方式2：命令行启动：`mvn -f src/data-collection/pom.xml spring-boot:run`

### 5.2 测试环境部署（简化）
1. 打包各模块：
   - 核心业务服务：`mvn -f src/core/pom.xml package -DskipTests`
   - 数据采集服务：`mvn -f src/data-collection/pom.xml package -DskipTests`
   - AI服务无需打包，直接复制源码部署
2. 使用Docker Compose部署：`docker-compose -f config/docker-compose.yml up -d`（需提前配置Docker环境）

## 6. 分支管理规范
- master：稳定版本分支，仅用于发布，禁止直接提交
- dev：开发主分支，所有功能开发完成后合并至此
- feature/xxx：功能分支，从dev创建，命名格式：feature/模块名-功能名
  - 示例：feature/core-alarm-push（核心服务报警推送功能）、feature/ai-yolov8-train（AI模块YOLOv8训练优化）
- bugfix/xxx：bug修复分支，从dev创建，命名格式：bugfix/问题描述
  - 示例：bugfix/data-collection-mqtt-disconnect（数据采集服务MQTT断连问题）

## 7. 提交规范
提交信息格式：`type(scope): description`
- type：feat（新增功能）、fix（修复bug）、docs（文档修改）、style（代码格式调整）、refactor（重构）、test（测试相关）、chore（构建/依赖调整）
- scope：模块名称（core-xxx：核心业务；ai-xxx：AI分析；data-xxx：数据采集）
- description：简要描述提交内容（中文）
- 示例：`feat(core-user): 新增用户角色权限分配功能`
- 示例：`fix(ai-detect): 修复夜间场景火焰识别误报问题`

## 8. 核心功能说明
### 8.1 核心业务服务
- 用户认证与权限管理：基于Spring Security+JWT实现用户登录、权限校验，支持多角色管理
- 报警管理：接收AI分析和传感器数据，评估风险等级，通过WebSocket/APP/短信推送报警信息，记录处置全流程
- 设备管理：维护设备信息，监控设备状态，支持设备远程控制和心跳检测
### 8.2 AI分析服务
- 目标检测：基于YOLOv8识别消防通道占用、危险区域闯入、火焰烟雾等异常
- 姿态识别：基于OpenPose检测实验室人员防护用具佩戴情况
- 提供HTTP推理接口，接收图像数据，返回检测结果（异常类型、置信度、坐标）
### 8.3 数据采集服务
- 视频流采集：通过FFmpeg对接摄像头RTSP流，转封装为HTTP-FLV流供前端播放，截取视频帧供AI分析
- 传感器数据采集：通过MQTT协议接收烟感、温感传感器数据，进行格式解析和异常过滤

## 9. 常见问题排查
- 问题1：核心服务启动失败，提示数据库连接异常
  解决方案：检查.env文件中数据库配置是否正确，确认MySQL服务已启动，且数据库campus_security已创建
- 问题2：AI服务启动报错，提示缺少PyTorch依赖
  解决方案：确认Python版本符合要求，执行`pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118`安装适配CUDA的PyTorch版本
- 问题3：数据采集服务无法获取摄像头视频流
  解决方案：检查摄像头RTSP地址配置是否正确，确认摄像头已接入内网且可访问，FFmpeg已配置环境变量
- 问题4：Swagger接口文档无法访问
  解决方案：确认核心服务已启动，访问地址是否正确（http://localhost:8080/swagger-ui.html），检查Swagger配置类是否启用

## 10. 维护人员
- 核心维护：本源不爱onani、Chen（模型训练）
- 协作人员：GeminiMortal
