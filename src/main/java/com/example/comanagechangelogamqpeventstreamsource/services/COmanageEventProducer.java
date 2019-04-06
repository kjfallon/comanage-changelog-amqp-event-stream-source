package com.example.comanagechangelogamqpeventstreamsource.services;

import com.example.comanagechangelogamqpeventstreamsource.configuration.COmanageMessageSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.file.tail.ApacheCommonsFileTailingMessageProducer;

import java.io.File;

@Slf4j
@EnableBinding(Source.class)
public class COmanageEventProducer {

    @Autowired
    private Source source;

    @Autowired
    private COmanageMessageSelector selector;

    @Value("${com.example.comanage.changelog.path}")
    private String fullPathOfFileToTail;

    public MessageProducerSupport fileContentProducer() {
        ApacheCommonsFileTailingMessageProducer fileTailProducer = new ApacheCommonsFileTailingMessageProducer();
        File fileToTail = new File(fullPathOfFileToTail);
        fileTailProducer.setFile(fileToTail);
        log.info("Created FileTailingMessageProducer for " + fileToTail.getAbsolutePath());
        return fileTailProducer;
    }

    @Bean
    public IntegrationFlow pollingFileTailFlow() {
        IntegrationFlowBuilder flowBuilder = IntegrationFlows.from(this.fileContentProducer());
        flowBuilder.filter(selector);
        flowBuilder.channel(this.source.output());
        return flowBuilder.get();
    }

}
