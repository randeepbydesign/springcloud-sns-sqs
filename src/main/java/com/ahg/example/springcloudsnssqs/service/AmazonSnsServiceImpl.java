package com.ahg.example.springcloudsnssqs.service;

import com.ahg.example.springcloudsnssqs.domain.JobDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author ashish
 * @since 2/2/19
 */
@Slf4j
@Service
public class AmazonSnsServiceImpl implements AmazonSnsService {

    private static final String SNS_TOPIC_NAME = "springcloud-sns-sqs-test";
    private static final String MESSAGE_SUBJECT = "Job to execute";
    private NotificationMessagingTemplate notificationMessagingTemplate;

    @Autowired
    public AmazonSnsServiceImpl(NotificationMessagingTemplate notificationMessagingTemplate) {
        this.notificationMessagingTemplate = notificationMessagingTemplate;
    }

    @Override
    public void publish(JobDTO jobDTO) {
        log.info("Received job={}", jobDTO);
        notificationMessagingTemplate.sendNotification(SNS_TOPIC_NAME, jobDTO, MESSAGE_SUBJECT);
    }
}
