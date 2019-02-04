package com.ahg.example.springcloudsnssqs.controller;

import com.ahg.example.springcloudsnssqs.domain.JobDTO;
import com.ahg.example.springcloudsnssqs.service.AmazonSnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ashish
 * @since 2/2/19
 */
@RestController
public class SampleController {

    private AmazonSnsService amazonSnsService;

    @Autowired
    public SampleController(AmazonSnsService amazonSnsService) {
        this.amazonSnsService = amazonSnsService;
    }

    @PostMapping("/job")
    public void publishToSns(@RequestBody JobDTO jobDTO) {
        amazonSnsService.publish(jobDTO);
    }
}
