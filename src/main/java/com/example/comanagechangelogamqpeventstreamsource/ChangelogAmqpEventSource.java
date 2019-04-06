package com.example.comanagechangelogamqpeventstreamsource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.CountDownLatch;

import com.example.comanagechangelogamqpeventstreamsource.components.StartUpTasks;

@SpringBootApplication
public class ChangelogAmqpEventSource {

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext springBootAppContext = SpringApplication.run(ChangelogAmqpEventSource.class, args);

		// Display information and perform any required housekeeping at launch
		springBootAppContext.getBean(StartUpTasks.class).appLaunchTasks();

		// Do not immediately exit, instead wait for shutdown request
		final CountDownLatch closeLatch = springBootAppContext.getBean(CountDownLatch.class);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				closeLatch.countDown();
			}
		});
		closeLatch.await();
	}

	@Bean
	public CountDownLatch closeLatch() {
		return new CountDownLatch(1);
	}

}
