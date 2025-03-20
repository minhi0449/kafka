# kafka Study
> Apache Kafka와 Spring Boot를 연동하여 메시지 스트리밍 기능을 학습하기 위한 실습 프로젝트입니다.

## 프로젝트 구조

```
kafka/
├── spring-cloud-config-server/           # Spring Cloud Config Server 프로젝트
├── spring-kafka-consumer-practice/       # Kafka 소비자(Consumer) 실습 프로젝트
├── spring-kafka-producer-practice/       # Kafka 생산자(Producer) 실습 프로젝트
├── README.md                             # 프로젝트 설명 문서
└── docker-compose.yml                    # Kafka 환경 구성을 위한 Docker Compose 설정
```

## 주요 기능

### 1. Kafka 프로듀서(Producer)

- 다양한 토픽으로 메시지 발행 기능
- JSON 형식 및 문자열 메시지 발행 예제
- 비동기 메시지 발행 및 콜백 처리

### 2. Kafka 컨슈머(Consumer)

- 여러 토픽의 메시지 소비 기능
- 소비자 그룹을 활용한 부하 분산 구현
- 특정 파티션 지정 소비 및 오프셋 관리

## 주요 개념

### Kafka 핵심 구성 요소

- **브로커(Broker)**: Kafka 서버로, 토픽의 파티션을 호스팅하고 관리
- **토픽(Topic)**: 메시지가 저장되는 논리적 채널
- **파티션(Partition)**: 토픽을 여러 파티션으로 분할하여 병렬 처리 지원
- **오프셋(Offset)**: 파티션 내 메시지의 위치를 나타내는 순차적 ID
- **프로듀서(Producer)**: 토픽에 메시지를 발행하는 클라이언트
- **컨슈머(Consumer)**: 토픽의 메시지를 구독하는 클라이언트
- **컨슈머 그룹(Consumer Group)**: 부하 분산을 위한 컨슈머 집합

### 메시지 전달 모델

1. **발행-구독(Publish-Subscribe) 모델**
   - 여러 컨슈머 그룹이 동일한 토픽을 구독하여 모든 메시지를 중복 수신

2. **큐(Queue) 모델**
   - 동일 컨슈머 그룹 내 여러 컨슈머가 메시지를 나누어 소비 (부하 분산)

## 시작하기

### 필수 조건

- Java 11 이상
- Docker 및 Docker Compose
- IntelliJ IDEA (권장) 또는 다른 Java IDE

### 환경 설정

1. **레포지토리 클론**
   ```bash
   git clone https://github.com/minhi0449/kafka.git
   cd kafka
   ```

2. **Docker로 Kafka 환경 실행**
   ```bash
   docker-compose up -d
   ```
   이 명령은 Kafka 브로커와 ZooKeeper를 포함한 환경을 구성합니다.

3. **애플리케이션 빌드**
   ```bash
   ./gradlew build
   ```

### 실행 방법

1. **프로듀서 애플리케이션 실행**
   ```bash
   cd spring-kafka-producer-practice
   ./gradlew bootRun
   ```

2. **컨슈머 애플리케이션 실행**
   ```bash
   cd spring-kafka-consumer-practice
   ./gradlew bootRun
   ```

## 구현 예제

### 컨슈머 예제 코드

```java
@Log4j2
@RequiredArgsConstructor
@Service
public class KafkaService {
    /**
     * my-topic-01 토픽의 메시지를 소비하는 소비자
     */
    @KafkaListener(topics = "my-topic-01", groupId = "group-my-topic-01")
    public void myTopic01Consumer(ConsumerRecord<String, String> record) {
        log.info("myTopic01Consumer...");
        log.info(record);
        log.info(record.value());
    }

    /**
     * my-topic-02 토픽의 메시지를 소비하는 여러 소비자 (그룹 내 부하 분산)
     */
    @KafkaListener(topics = "my-topic-02", groupId = "group-my-topic-02")
    public void myTopic02Consumer1(ConsumerRecord<String, String> record) {
        log.info("myTopic02Consumer1...");
        log.info(record.value());
    }

    @KafkaListener(topics = "my-topic-02", groupId = "group-my-topic-02")
    public void myTopic02Consumer2(ConsumerRecord<String, String> record) {
        log.info("myTopic02Consumer2...");
        log.info(record.value());
    }

    /**
     * my-topic-03 토픽의 메시지를 소비하는 여러 소비자 (그룹 내 부하 분산)
     */
    @KafkaListener(topics = "my-topic-03", groupId = "group-my-topic-03")
    public void myTopic03Consumer1(ConsumerRecord<String, String> record) {
        log.info("myTopic03Consumer1...");
        log.info(record.value());
    }

    @KafkaListener(topics = "my-topic-03", groupId = "group-my-topic-03")
    public void myTopic03Consumer2(ConsumerRecord<String, String> record) {
        log.info("myTopic03Consumer2...");
        log.info(record.value());
    }

    @KafkaListener(topics = "my-topic-03", groupId = "group-my-topic-03")
    public void myTopic03Consumer3(ConsumerRecord<String, String> record) {
        log.info("myTopic03Consumer3...");
        log.info(record.value());
    }
}
```

### 프로듀서 예제 코드

```java
@Log4j2
@RequiredArgsConstructor
@RestController
public class KafkaController {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/send/{topic}/{message}")
    public String sendMessage(@PathVariable String topic, @PathVariable String message) {
        log.info("Send message to {} : {}", topic, message);
        
        // 비동기적으로 메시지 전송
        ListenableFuture<SendResult<String, String>> future = 
            kafkaTemplate.send(topic, message);
        
        // 콜백 등록
        future.addCallback(
            result -> log.info("Message sent successfully: {}", result),
            ex -> log.error("Failed to send message: {}", ex.getMessage())
        );
        
        return "Message sent to topic: " + topic;
    }
}
```

## 학습 시나리오

### 1. 단일 토픽 기본 메시징

- `my-topic-01` 토픽을 통한 기본 메시지 발행 및 소비

### 2. 메시지 부하 분산

- `my-topic-02` 토픽과 동일 컨슈머 그룹을 사용한 메시지 부하 분산
- 두 컨슈머 간 메시지 분배 확인

### 3. 다중 컨슈머 및 고가용성

- `my-topic-03` 토픽에 세 개의 컨슈머를 사용한 부하 분산
- 컨슈머 장애 시 리밸런싱 테스트

### 4. 이벤트 기반 아키텍처 시뮬레이션

- 여러 토픽과 컨슈머 그룹을 이용한 마이크로서비스 통신 시뮬레이션
- 비동기 이벤트 처리 패턴 구현

## 고급 기능 및 확장

### 스키마 레지스트리

- Avro 또는 Protobuf를 사용한 메시지 스키마 관리
- 스키마 진화 및 호환성 유지

### 스트림 처리

- Kafka Streams API를 활용한 실시간 데이터 처리
- 집계, 조인, 윈도우 연산 등 구현

### 모니터링 및 관리

- Kafka UI 도구를 통한 모니터링
- 성능 측정 및 최적화 방법

## 참고 자료

- [Apache Kafka 공식 문서](https://kafka.apache.org/documentation/)
- [Spring for Apache Kafka 문서](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [Confluent Kafka 가이드](https://developer.confluent.io/get-started/)

## 트러블슈팅

### 일반적인 이슈

1. **연결 문제**
   - Kafka 브로커 연결 설정 확인
   - 방화벽 또는 네트워크 문제 확인

2. **소비자 그룹 리밸런싱 이슈**
   - 컨슈머 그룹 ID 설정 확인
   - 세션 타임아웃 설정 검토

3. **메시지 손실 이슈**
   - 프로듀서 acks 설정 확인
   - 적절한 파티션 수 및 복제 계수 설정

## 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다. 자세한 내용은 LICENSE 파일을 참조하세요.

## 기여하기

버그 리포트, 기능 요청, 풀 리퀘스트는 GitHub 리포지토리를 통해 제출해 주세요.

---

*이 README는 Kafka와 Spring Boot 학습을 위한 가이드라인입니다. 실제 프로젝트 구현에 따라 내용을 적절히 수정하여 사용하세요.*
```

이제 이 전체 블록을 복사하여 README.md 파일에 붙여넣기 하시면 됩니다.
