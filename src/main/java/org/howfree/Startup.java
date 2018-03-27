package org.howfree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 采集启动程序
 * @Author: Bob Li
 * @Date: 3/27/2018 2:54 PM
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan("org.howfree")
public class Startup {
    public static void main(String[] args) {

        SpringApplication.run(Startup.class, args);
    }
}
