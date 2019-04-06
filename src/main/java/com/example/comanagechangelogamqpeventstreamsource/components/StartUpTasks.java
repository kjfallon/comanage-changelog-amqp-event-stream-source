package com.example.comanagechangelogamqpeventstreamsource.components;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartUpTasks {

    @Value("${com.example.comanage.changelog.path}")
    private String changelog;

    @Value("${spring.rabbitmq.host}")
    private String amqpHost;

    @Value("${spring.cloud.stream.bindings.output.destination}")
    private String amqpExchange;

    public void appLaunchTasks() {

        log.info("comanage-changelog-processor: started");
        log.info("comanage-changelog-processor: tailing file: " + changelog);
        log.info("comanage-changelog-processor: sending AMQP events to the " + amqpExchange + " exchange on host " + amqpHost);
        log.info("comanage-changelog-processor: listening for log entries from the COmanage Changelog Provisioner...");

        // Uncomment this to demo encrypting a string for use in a property file
        //String stringToEncrypt = "guest";
        //log.info("The string " + stringToEncrypt + " encrypts to " + (encryptionDemonstrationBean.encrypt(stringToEncrypt)));
    }

    @Autowired
    StringEncryptor encryptionDemonstrationBean;

    @Bean(name="encryptionDemonstrationBean")
    static public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("changeme");
        config.setAlgorithm("PBEWithHmacSHA512AndAES_256");
        config.setKeyObtentionIterations("3000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.salt.RandomIVGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

}