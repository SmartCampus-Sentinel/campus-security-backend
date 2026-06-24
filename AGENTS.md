# AGENTS.md

## Project overview

Campus security platform backend (校园智能安防平台后端). Single-module Spring Boot 2.7.18 project with integrated MQTT data collection and separate Python AI service.

## Critical: Dual codebase structure

**Active code lives at `src/main/`** — package `com.smartcampus`, entry point `SmartCampusApplication.java`.

`src/core/` is a **dormant/experimental module** with package `com.campus.security.core`. Never add code there.

## Build & run

```bash
# Java backend (Maven not in PATH, use IDEA bundled Maven)
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" clean compile
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run

# Python AI service
cd src/ai && pip install -r requirements.txt && python src/main.py  # port 8081

# Tests
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" test  # 18 tests
```

- App starts on port 8080 with context-path `/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- AI service docs: `http://localhost:8081/docs`

## Infrastructure

- **Docker Compose**: MySQL (3307→3306), Redis (6379), EMQ X MQTT broker (1883, dashboard 18083)
- **Security**: Spring Security + JWT (BCrypt passwords, stateless sessions)
- **MQTT topics**: `campus/sensor/#`, `campus/camera/#`
- **FFmpeg**: configured for RTSP→stream conversion

## Package layout

```
src/main/java/com/smartcampus/
├── common/Result.java              — unified API response wrapper
├── config/                         — SecurityConfig, MqttConfig, FfmpegConfig, MybatisPlusConfig, WebClientConfig, AsyncConfig, RedisConfig, WebSocketConfig, GlobalExceptionHandler
├── controller/                     — REST endpoints (Auth, User, Role, AlarmEvent, DeviceInfo, SensorData, StudentReport, AlarmDisposal, VideoStream)
├── dto/                            — request/response DTOs
├── entity/                         — MyBatis-Plus entities
├── mapper/                         — MyBatis-Plus mapper interfaces
├── security/                       — LoginUser, JwtAuthenticationFilter, UserDetailsServiceImpl
├── service/                        — service interfaces + MqttMessageHandler, VideoStreamService, WebSocketPushService
├── service/impl/                   — service implementations
├── utils/JwtUtils.java             — JWT utility
└── SmartCampusApplication.java     — entry point
```

## Key conventions

- **Lombok everywhere** — `@Data`, `@RequiredArgsConstructor`, `@Slf4j`
- **MyBatis-Plus** — entities use `@TableName`, `@TableId`, `@TableField`. Mapper XML in `resources/mapper/`.
- **Result<T>** — standard API response: `Result.success(data)`, `Result.error(msg)`
- **Swagger 2 annotations** (`io.swagger.annotations.*`)
- **Controller injection** — use `@RequiredArgsConstructor` + `private final` (not `@Autowired`)
- **Time fields** — always server-generated (`LocalDateTime.now()`), never trust client

## Testing notes

- **18 tests exist** across 4 test classes (AuthControllerTest, RoleControllerTest, SensorDataServiceImplTest, AlarmDisposalServiceImplTest)
- **`@MockBean` doesn't work** in this environment — use `@Spy` + `@InjectMocks` with standalone MockMvc
- **H2 in-memory DB** available for tests (dependency in pom.xml)
- Test config: `src/test/resources/application-test.yml`

## DB tables

`sys_user`, `sys_role`, `device_info`, `alarm_event`, `alarm_disposal`, `student_report`, `sensor_data`

SQL init script: `src/main/resources/sql/init.sql`

## Project status

All planned features implemented. 18 tests passing. 41 API endpoints across 9 controllers.

### Optional future enhancements
- Rate limiting / API throttling
- File upload (MinIO/OSS) for screenshots and videos
- Alarm statistics dashboard aggregation
- OpenPose model fine-tuning for campus-specific scenarios
