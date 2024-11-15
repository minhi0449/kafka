package co.kr.minhi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class KafkaService {

    @KafkaListener(topics = "my-topic-01", groupId = "group-my-topic-01")
    public void myTopic01Consumer(ConsumerRecord<String, String> record) {
        log.info("myTopic01Consumer...");
        log.info(record);
        log.info(record.value());

    }

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



    @KafkaListener(topics = "my-topic-03", groupId = "group-my-topic-03")
    public void myTopic03Consumer1(ConsumerRecord<String, String> record) {
        log.info("myTopic02Consumer1...");
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
