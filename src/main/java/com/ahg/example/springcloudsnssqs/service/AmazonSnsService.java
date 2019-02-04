package com.ahg.example.springcloudsnssqs.service;

import com.ahg.example.springcloudsnssqs.domain.JobDTO;

/**
 * @author ashish
 * @since 2/2/19
 */
public interface AmazonSnsService {

    /**
     * This publishes job to SNS
     *
     * @param jobDTO job to publish
     */
    void publish(JobDTO jobDTO);
}
