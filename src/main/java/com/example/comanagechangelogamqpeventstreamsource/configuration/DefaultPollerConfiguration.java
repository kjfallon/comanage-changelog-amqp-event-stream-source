package com.example.comanagechangelogamqpeventstreamsource.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.TimeUnit;

@Configuration
public class DefaultPollerConfiguration {

    public static final int FILE_POLLING_FREQUENCY_IN_MILLISECONDS = 250;

    @Bean(name = {"defaultPoller", "org.springframework.integration.context.defaultPollerMetadata"})
    public PollerMetadata defaultPoller() {
        PollerMetadata pollerMetadata = new PollerMetadata();
        PeriodicTrigger trigger = new PeriodicTrigger(FILE_POLLING_FREQUENCY_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
        pollerMetadata.setTrigger(trigger);
        return pollerMetadata;
    }

}
