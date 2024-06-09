package com.example.dividendsproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();
        int n = Runtime.getRuntime().availableProcessors();//코어의 개수를 가져옴
        threadPool.setPoolSize(n);//쓰레드 개수 설정
        threadPool.initialize();
        taskRegistrar.setTaskScheduler(threadPool);

    }
}
