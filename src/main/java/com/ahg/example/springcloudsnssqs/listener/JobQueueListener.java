package com.ahg.example.springcloudsnssqs.listener;

import com.ahg.example.springcloudsnssqs.domain.JobDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author ashish
 * @since 2/2/19
 */
@Slf4j
@Component
public class JobQueueListener {

    private static final String SQS_NAME = "springcloud-sns-sqs-test";


    @SqsListener(value = SQS_NAME, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void onMessage(@NotificationMessage JobDTO jobDTO, @Header("SenderId") String senderId) {
        log.info("job={} senderId={}", jobDTO, senderId);
    }
}
