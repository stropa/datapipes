package org.stropa.data.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.stropa.data.schedule.DataTransferTask;

import javax.annotation.Resource;
import java.util.Map;

@Configuration
public class DefaultSchedulingConfig implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSchedulingConfig.class);

    @Resource
    private TaskScheduler taskScheduler;

    @Bean
    public TaskScheduler getDefaultScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(100);
        return scheduler;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, DataTransferTask> taskMap = applicationContext.getBeansOfType(DataTransferTask.class);
        for (String name : taskMap.keySet()) {
            DataTransferTask dataTransferTask = taskMap.get(name);
            taskScheduler.schedule(dataTransferTask, dataTransferTask.getTrigger());
        }
    }
}
