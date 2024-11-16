export const commentaries = [
    {
        startTime: 0,
        content: "Spring의 기본 개념: Spring 프레임워크는 **Java EE(Enterprise Edition) 개발을 쉽게 하기 위해** 등장했습니다. Spring은 객체 지향 프로그래밍을 기반으로 하며, **DI(의존성 주입)**와 **AOP(관점 지향 프로그래밍)** 기능을 통해 확장 가능한 애플리케이션을 개발할 수 있게 도와줍니다."
    },
    {
        startTime: 35,
        content: "의존성 주입(DI) 예제: Spring에서는 `@Autowired` 어노테이션을 통해 **자동으로 객체를 주입**받을 수 있습니다. 이를 통해 객체 간 결합도를 낮출 수 있습니다.\n\n예제 코드:\n\n```java\n@Service\npublic class UserService {\n    private final UserRepository userRepository;\n\n    @Autowired\n    public UserService(UserRepository userRepository) {\n        this.userRepository = userRepository;\n    }\n}\n```"
    },
    {
        startTime: 75,
        content: "Spring Boot와 내장 서버: **Spring Boot**는 **톰캣(Tomcat)과 같은 내장 서버**를 포함하여, 웹 애플리케이션 개발과 배포를 더 쉽게 해줍니다. 단순히 애플리케이션을 실행하면 **내장 서버가 자동으로 시작**되며, 추가 설정 없이 배포할 수 있습니다."
    },
    {
        startTime: 115,
        content: "REST API 개발의 핵심: Spring에서 `@RestController`와 `@RequestMapping`을 활용하여 **RESTful 서비스를 구축**할 수 있습니다.\n\n예제 코드:\n\n```java\n@RestController\n@RequestMapping(\"/api/users\")\npublic class UserController {\n    @GetMapping(\"/{id}\")\n    public ResponseEntity<User> getUserById(@PathVariable Long id) {\n        User user = userService.findById(id);\n        return ResponseEntity.ok(user);\n    }\n}\n```"
    },
    {
        startTime: 155,
        content: "Spring Data JPA를 통한 데이터베이스 연동: **Spring Data JPA**는 **데이터베이스와의 상호작용을 간소화**합니다. CRUD 기능을 자동으로 제공하며, **복잡한 쿼리**도 쉽게 작성할 수 있습니다.\n\n예제 코드:\n\n```java\n@Entity\npublic class User {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String name;\n    private String email;\n}\n\npublic interface UserRepository extends JpaRepository<User, Long> {\n    List<User> findByName(String name);\n}\n```"
    },
    {
        startTime: 200,
        content: "트랜잭션 관리: Spring의 `@Transactional` 어노테이션을 사용하면 **트랜잭션을 쉽게 관리**할 수 있습니다. 트랜잭션은 여러 데이터베이스 작업을 하나의 작업 단위로 묶어, **모두 성공하거나 모두 실패**하도록 보장합니다.\n\n예제 코드:\n\n```java\n@Service\npublic class PaymentService {\n    @Transactional\n    public void processPayment(Long userId, Double amount) {\n        // 로직 작성\n    }\n}\n```"
    },
    {
        startTime: 245,
        content: "Spring Security를 통한 인증과 권한 관리: **Spring Security**는 **애플리케이션의 보안** 관리를 담당합니다. 로그인, 로그아웃, 권한 부여를 쉽게 구현할 수 있습니다.\n\n예제 코드:\n\n```java\n@Configuration\npublic class SecurityConfig extends WebSecurityConfigurerAdapter {\n    @Override\n    protected void configure(HttpSecurity http) throws Exception {\n        http.authorizeRequests()\n            .antMatchers(\"/admin/**\").hasRole(\"ADMIN\")\n            .antMatchers(\"/user/**\").hasRole(\"USER\")\n            .anyRequest().authenticated()\n            .and().formLogin();\n    }\n}\n```"
    },
    {
        startTime: 295,
        content: "글로벌 예외 처리: `@ControllerAdvice`와 `@ExceptionHandler`를 사용하면 **전역에서 예외를 처리**할 수 있습니다.\n\n예제 코드:\n\n```java\n@ControllerAdvice\npublic class GlobalExceptionHandler {\n    @ExceptionHandler(UserNotFoundException.class)\n    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {\n        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());\n    }\n}\n```"
    },
    {
        startTime: 335,
        content: "Spring MVC 아키텍처: Spring MVC는 **모델(Model)-뷰(View)-컨트롤러(Controller)** 아키텍처를 기반으로 하여 웹 애플리케이션을 개발합니다. 각 구성 요소가 명확히 구분되어 애플리케이션의 유지보수와 확장성을 높여줍니다."
    },
    {
        startTime: 375,
        content: "RESTful API와 상태 관리: REST API는 **무상태(stateless)** 특성을 가집니다. 각 요청은 독립적이므로 클라이언트가 상태를 관리하거나 별도의 인증 토큰을 요청마다 전송해야 합니다."
    },
    {
        startTime: 420,
        content: "Spring AOP(Aspect-Oriented Programming): Spring AOP는 **공통 기능을 모듈화**하여 코드 중복을 줄이고 유지보수를 용이하게 합니다. 예를 들어, 로깅이나 트랜잭션 관리와 같은 부가 기능을 핵심 로직에 영향을 주지 않고 처리할 수 있습니다."
    },
    {
        startTime: 460,
        content: "Bean 생명 주기 관리: Spring은 **빈(Bean)의 생명 주기를 관리**하여 개발자가 복잡한 객체 생명 주기 관리를 할 필요 없이 애플리케이션 개발에 집중할 수 있도록 도와줍니다."
    },
    {
        startTime: 505,
        content: "Spring의 프로파일(Profile) 관리: **@Profile** 어노테이션을 통해 개발, 테스트, 운영 환경에 따라 Bean의 구성을 다르게 할 수 있습니다. 이를 통해 환경에 맞는 Bean을 손쉽게 활성화할 수 있습니다."
    },
    {
        startTime: 550,
        content: "Spring의 메시지 큐 통합: **RabbitMQ**, **Kafka**와 같은 메시지 큐를 Spring과 통합하여 애플리케이션 간의 비동기 통신을 구현할 수 있습니다."
    },
    {
        startTime: 590,
        content: "Spring Actuator를 사용한 모니터링: **Spring Boot Actuator**는 애플리케이션의 상태를 모니터링할 수 있는 여러 엔드포인트를 제공합니다. 이를 통해 애플리케이션의 헬스 체크, 메트릭스 등을 쉽게 확인할 수 있습니다."
    },
    {
        startTime: 640,
        content: "Spring의 스케줄링 기능: Spring의 `@Scheduled` 어노테이션을 통해 **정기 작업을 자동으로 수행**할 수 있습니다. 예를 들어, 매일 자정에 데이터 백업 작업을 실행할 수 있습니다."
    },
    {
        startTime: 685,
        content: "Spring Cloud와 마이크로서비스: Spring Cloud는 **마이크로서비스 아키텍처** 구현을 돕는 다양한 도구와 기능을 제공합니다. 예를 들어, 서비스 디스커버리, 로드 밸런싱, 설정 관리 등을 쉽게 구현할 수 있습니다."
    },
    {
        startTime: 730,
        content: "Spring의 테스트 지원: Spring은 **@SpringBootTest**를 통해 애플리케이션의 통합 테스트를 손쉽게 지원합니다. 이를 통해 실제 환경과 유사한 조건에서 테스트할 수 있습니다."
    },
    {
        startTime: 770,
        content: "Spring의 Caching 지원: `@Cacheable` 어노테이션을 사용하면 자주 사용되는 데이터를 캐시에 저장하여 성능을 높일 수 있습니다."
    },
    {
        startTime: 820,
        content: "Spring의 데이터 유효성 검사: `@Valid`와 `@Validated` 어노테이션을 사용하여 요청 데이터의 유효성을 쉽게 검사할 수 있습니다."
    },
    {
        startTime: 860,
        content: "Spring의 국제화(i18n) 지원: Spring은 **MessageSource**를 통해 여러 언어와 지역에 맞게 애플리케이션을 국제화할 수 있습니다."
    }
];
