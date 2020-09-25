package com.sso.acc.ticket.validateTicket;

import com.sso.acc.ticket.TicketProperties;
import com.sso.acc.ticket.issueTicket.IssueTicket;
import com.sso.acc.ticket.manageTicket.ManageTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @Auther: Lee
 * @Date 2020/6/18 10:17
 * @Description:
 */
@Component
public class ValidateSecondTicket {

    @Autowired
    private TicketProperties ticketProperties;
    //为 public 不然没有权限
    public static ValidateSecondTicket validateSecondTicket;

    //被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法。
    // 被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行。
    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        validateSecondTicket = this;
    }

    private ValidateSecondTicket() {

    }

    public static ValidateSecondTicket getInstance() {
        if (validateSecondTicket == null) {
            validateSecondTicket = new ValidateSecondTicket();
        }
        return validateSecondTicket;
    }

    public boolean validateSecondTicket(String secondTicket) {
        if (secondTicket == null) {
            return false;
        }
        if (!ManageTicket.secondTicketMap.containsKey(secondTicket)) {
            //invalid ST
            return false;
        }
        Map sessionInfo = (Map) ManageTicket.secondTicketMap.get(secondTicket);
        long loginDate = (long) sessionInfo.get("loginDate");
        if (System.currentTimeMillis() - loginDate > validateSecondTicket.ticketProperties.getExpiredTimeST() * 1000) {
            ManageTicket.secondTicketMap.remove(secondTicket);
            return false;
        }
        ManageTicket.sessionInfo.put("loginDate", System.currentTimeMillis());
        ManageTicket.sessionInfo.put("loginId", sessionInfo.get("loginId"));
        ManageTicket.secondTicketMap.put(secondTicket, ManageTicket.sessionInfo);
        return true;
    }
}
