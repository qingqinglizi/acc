package com.sso.acc.ticket.issueTicket;

import com.sso.acc.exception.AccRunTimeException;
import com.sso.acc.ticket.TicketProperties;
import com.sso.acc.ticket.manageTicket.ManageTicket;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author Lee
 * Date: 2020/6/3 11:12
 * Description: 发布票据
 */
@Component
public class IssueTicket {

    @Autowired
    private TicketProperties ticketProperties;
    //为 public 不然没有权限
    public static IssueTicket issueTicket;

    private final int randomArrayLnegth = 4;

    //被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法。
    // 被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行。
    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        issueTicket = this;
    }

    /**
     * Generate secondary ticket
     *
     * @param firstTicket firstTicket
     * @return String
     */
    public String createSecondTicket(String firstTicket) throws AccRunTimeException, UnknownHostException {
        //check firstTicket validity
        return validateFirstTicket(firstTicket) ? generateSecondaryTicket(firstTicket) : null;
    }

    /**
     * Check the validity of the bill
     *
     * @param firstTicket firstTicket
     * @return validate result
     */
    private boolean validateFirstTicket(String firstTicket) {
        if (firstTicket == null || !ManageTicket.firstTicketMap.containsKey(firstTicket)) {
            return false;
        }
        Map firstTicketSessionInfo = (Map) ManageTicket.firstTicketMap.get(firstTicket);
        long firstTicketTimeMillis = (long) firstTicketSessionInfo.get("loginDate");
        long timeInterval = System.currentTimeMillis() - firstTicketTimeMillis;
        if (timeInterval > issueTicket.ticketProperties.getExpiredTimeFT() * 1000) {
            ManageTicket.firstTicketMap.remove(firstTicket);
            return false;
        }
        ManageTicket.sessionInfo.put("loginDate", System.currentTimeMillis());
        ManageTicket.sessionInfo.put("loginId", firstTicketSessionInfo.get("loginId"));
        ManageTicket.firstTicketMap.put(firstTicket, ManageTicket.sessionInfo);
        return true;
    }

    /**
     * create secondary ticket by first ticket, ip, dateTime and salt etc.
     *
     * @param firstTicket firstTicket
     * @return String
     */
    private String generateSecondaryTicket(String firstTicket) throws AccRunTimeException, UnknownHostException {
        String usableElement = firstTicket.substring(3);
        char[] randomChar = getRandomCharacter(usableElement);
        InetAddress inetAddress = InetAddress.getLocalHost();
        String hostIp = inetAddress.getHostAddress();
        long dateString = System.currentTimeMillis();
        String openText = hostIp.replace(".", "") + dateString + new String(randomChar);
        String salt = RandomStringUtils.randomAlphanumeric(5);
        SimpleHash encryptText = new SimpleHash(issueTicket.ticketProperties.getAlgorithmMethod(), openText, salt, issueTicket.ticketProperties.getIteration());
        return "ST-" + encryptText.toString();
    }

    private char[] getRandomCharacter(String string) {
        if (string == null) {
            return null;
        }
        char[] randomCharacter = new char[randomArrayLnegth];
        int index;
        for (int i = 0; i < randomArrayLnegth; i++) {
            index = (int) (Math.random() * randomArrayLnegth);
            randomCharacter[i] = string.charAt(index);
        }
        return randomCharacter;
    }
}
