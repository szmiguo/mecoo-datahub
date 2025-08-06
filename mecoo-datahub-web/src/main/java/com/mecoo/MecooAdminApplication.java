package com.mecoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author mecoo
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MecooAdminApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(MecooAdminApplication.class, args);
        System.out.println("Start Mecoo DataHub Success ...");
    }
}