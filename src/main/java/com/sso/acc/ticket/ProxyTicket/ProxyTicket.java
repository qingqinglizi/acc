package com.sso.acc.ticket.ProxyTicket;

import com.sso.acc.ticket.TicketProperties;
import com.sso.acc.exception.ServiceException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.net.InetAddress;

/**
 * @author Lee
 * Date: 2020/6/3 10:19
 * Description: 代理票据
 */
@Component
public class ProxyTicket {

    @Autowired
    private TicketProperties ticketProperties;

    //为 public 不然没有权限
    public static ProxyTicket proxyTicket;

    //被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法。
    // 被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行。
    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        proxyTicket = this;
    }

    /**
     * create first ticket
     *
     * @return first ticket
     */
    public String createFirstTicket() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostIp = inetAddress.getHostAddress();
            long dateString = System.currentTimeMillis();
            String openText = hostIp.replace(".", "") + dateString;
            String salt = RandomStringUtils.randomAlphanumeric(5);
            return "FT-" + encryptMessage(openText, salt);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * encrypt important message
     *
     * @param openText openText
     * @param salt salt
     * @return encrypt result
     */
    private String encryptMessage(String openText, String salt) {
        SimpleHash encryptText = new SimpleHash(proxyTicket.ticketProperties.getAlgorithmMethod(), openText, salt, proxyTicket.ticketProperties.getIteration());
        return encryptText.toString();
    }
}
