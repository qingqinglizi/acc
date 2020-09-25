package com.sso.acc.ticket.issueTicket;

import com.sso.acc.exception.ServiceException;
import com.sso.acc.ticket.TicketProperties;
import com.sso.acc.ticket.manageTicket.ManageTicket;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Map;

/**
 * @Auther: Lee
 * @Date 2020/6/3 11:12
 * @Description:发布票据
 */
@Component
public class IssueTicket {

    @Autowired
    private TicketProperties ticketProperties;
    //为 public 不然没有权限
    public static IssueTicket issueTicket;

    //被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法。
    // 被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行。
    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        issueTicket = this;
    }

    /**
     * Generate secondary ticket
     * @param firstTicket
     * @return
     */
    public String createSecondTicket(String firstTicket) {
        try {
            String secondTicket = null;
            //check firstTicket validity
            if (validateFirstTicket(firstTicket)) {
                return generateSecondaryTicket(firstTicket);
            }
            return secondTicket;
        } catch (Exception e) {
          throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * Check the validity of the bill
     * @param firstTicket
     * @return
     */
    public boolean validateFirstTicket(String firstTicket) {
        if (firstTicket == null) {
            return false;
        }
//        HttpSession session = ServletUtil.getRequest().getSession();
//        Object firstTicketInfo = session.getAttribute(firstTicket);
//        if (firstTicketInfo == null) {
//            return false;
//        }
        if (ManageTicket.firstTicketMap.containsKey(firstTicket)) {
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
        return false;
    }

    /**
     * create secondary ticket by first ticket, ip, dateTime and salt etc.
     * @param firstTicket
     * @return
     */
    public String generateSecondaryTicket(String firstTicket) {
        try {
            String usableElement = firstTicket.substring(3);
            char[] randomChar = getRandomCharacter(usableElement, 4);
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostIp = inetAddress.getHostAddress();
            long dateString = System.currentTimeMillis();
            String openText = hostIp.replace(".", "") + dateString + new String(randomChar);
            String salt = RandomStringUtils.randomAlphanumeric(5);
            SimpleHash encryptText = new SimpleHash(issueTicket.ticketProperties.getAlgorithmMethod(), openText, salt, issueTicket.ticketProperties.getIteration());
            return "ST-" + encryptText.toString();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public char[] getRandomCharacter(String string, int arrayLength) {
        if (string == null || arrayLength <= 0) {
            return null;
        }
        if (arrayLength > string.length()) {
            arrayLength = string.length();
        }
        char[] randomCharacter = new char[arrayLength];
        int index;
        for (int i = 0; i < arrayLength; i ++) {
            index = (int)(Math.random() * arrayLength);
            randomCharacter[i] = string.charAt(index);
        }
        return randomCharacter;
    }
}
