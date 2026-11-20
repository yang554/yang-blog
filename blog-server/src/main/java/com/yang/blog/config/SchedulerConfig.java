package com.yang.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.logging.Logger;


@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    Logger log = Logger.getLogger(String.valueOf(SchedulerConfig.class));
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);              // 线程池大小
        taskScheduler.setThreadNamePrefix("scheduled-task-"); // 线程名前缀
        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}