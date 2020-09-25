package com.sso.acc.ticket.validateTicket;

import com.sso.acc.ticket.TicketProperties;
import com.sso.acc.ticket.manageTicket.ManageTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @Auther: Lee
 * @Date 2020/6/18 10:16
 * @Description:
 */
@Component
public class ValidateFirstTicket {

    @Autowired
    private TicketProperties ticketProperties;
    //为 public 不然没有权限
    public static ValidateFirstTicket validateFirstTicket;

    //被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法。
    // 被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行。
    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        validateFirstTicket = this;
    }

    private ValidateFirstTicket() {

    }

    public static ValidateFirstTicket getInstance() {
        if (validateFirstTicket == null) {
            validateFirstTicket = new ValidateFirstTicket();
        }
        return validateFirstTicket;
    }

    public boolean validateFirstTicket(String firstTicket) {
        if (firstTicket == null) {
            return false;
        }
        if (!ManageTicket.firstTicketMap.containsKey(firstTicket)) {
            return false;
        }
        Map sessionInfo = (Map) ManageTicket.firstTicketMap.get(firstTicket);
        long loginDate = (long) sessionInfo.get("loginDate");
        if (System.currentTimeMillis() - loginDate > validateFirstTicket.ticketProperties.getExpiredTimeFT() * 1000) {
            ManageTicket.firstTicketMap.remove(firstTicket);
            return false;
        }
        ManageTicket.sessionInfo.put("loginDate", System.currentTimeMillis());
        ManageTicket.sessionInfo.put("loginId", sessionInfo.get("loginId"));
        ManageTicket.firstTicketMap.put(firstTicket, ManageTicket.sessionInfo);
        return true;
    }
}
