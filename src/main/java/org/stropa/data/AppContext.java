package org.stropa.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.stropa.data.config.DefaultSchedulingConfig;

import javax.annotation.Resource;

@Configuration
@ImportResource("file:${configLocation}")
@Import(DefaultSchedulingConfig.class)
public class AppContext {

    private static final Logger logger = LoggerFactory.getLogger(AppContext.class);

    @Resource
    private String foo;

    @Bean
    public String getBar() {
        logger.debug(foo);
        return "bar";
    }

}
