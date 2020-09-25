package com.sso.acc.ticket;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Auther: Lee
 * @Date 2020/6/5 10:33
 * @Description:
 */
@Component
// 自定义位置&文件名
@PropertySource(value = {"/application-encrypt.properties"})
@ConfigurationProperties(prefix = "encrypt")
@Data
public class TicketProperties {

    private String algorithmMethod;

    private int iteration;

    private int expiredTimeFT;

    private int expiredTimeST;
}
