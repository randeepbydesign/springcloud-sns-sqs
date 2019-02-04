package com.ahg.example.springcloudsnssqs.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.cloud.aws.messaging.support.NotificationMessageArgumentResolver;
import org.springframework.cloud.aws.messaging.support.converter.NotificationRequestConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ashish
 * @since 2/2/19
 */
@Configuration
public class AwsConfiguration {

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new ProfileCredentialsProvider();
    }

    @Bean
    public NotificationMessagingTemplate notificationMessagingTemplate(final AmazonSNS amazonSNS) {
        return new NotificationMessagingTemplate(amazonSNS);
    }

    @Bean
    public AmazonSNSAsync amazonSNSAsync(final AWSCredentialsProvider credentialsProvider) {
        return AmazonSNSAsyncClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .build();
    }

    @Bean
    public AmazonSQSAsync amazonSQSAsync(final AWSCredentialsProvider credentialsProvider) {
        return AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .build();
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
        return new QueueMessagingTemplate(amazonSQSAsync);
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(final SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory,
                                                                         final QueueMessageHandler queueMessageHandler) {
        SimpleMessageListenerContainer msgListenerContainer = simpleMessageListenerContainerFactory.createSimpleMessageListenerContainer();
        msgListenerContainer.setMessageHandler(queueMessageHandler);
        return msgListenerContainer;
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(final AmazonSQSAsync amazonSQSAsync) {
        SimpleMessageListenerContainerFactory msgListenerContainerFactory = new SimpleMessageListenerContainerFactory();
        msgListenerContainerFactory.setAmazonSqs(amazonSQSAsync);
        msgListenerContainerFactory.setMaxNumberOfMessages(10);
        msgListenerContainerFactory.setWaitTimeOut(2);
        return msgListenerContainerFactory;
    }

    @Bean
    public QueueMessageHandler queueMessageHandler(final QueueMessageHandlerFactory queueMessageHandlerFactory) {
        QueueMessageHandler queueMessageHandler = queueMessageHandlerFactory.createQueueMessageHandler();
        return queueMessageHandler;
    }

    @Bean
    public QueueMessageHandlerFactory queueMessageHandlerFactory(final AmazonSQSAsync amazonSQS, final BeanFactory beanFactory) {

        ObjectMapper objectMapper = new ObjectMapper();

        MappingJackson2MessageConverter jacksonMessageConverter = new MappingJackson2MessageConverter();
        jacksonMessageConverter.setSerializedPayloadClass(String.class);
        jacksonMessageConverter.setObjectMapper(objectMapper);
        jacksonMessageConverter.setStrictContentTypeMatch(false);

        List<MessageConverter> payloadArgumentConverters = new ArrayList<>();
        payloadArgumentConverters.add(jacksonMessageConverter);

        // This is the converter that is invoked on SNS messages on SQS listener
        NotificationRequestConverter notificationRequestConverter = new NotificationRequestConverter(jacksonMessageConverter);

        payloadArgumentConverters.add(notificationRequestConverter);

        // It needs to be wrapped in this
        CompositeMessageConverter compositeMessageConverter = new CompositeMessageConverter(payloadArgumentConverters);

        Assert.notNull(amazonSQS, "SQS client cannot be null");
        Assert.notNull(beanFactory, "SQS client bean factory can not be null");
        QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
        factory.setAmazonSqs(amazonSQS);
        factory.setBeanFactory(beanFactory);

        factory.setArgumentResolvers(Arrays.asList(new NotificationMessageArgumentResolver(compositeMessageConverter)));

        return factory;
    }
}
