package com.sso.acc.controller;

import com.sso.acc.ticket.ProxyTicket.ProxyTicket;
import com.sso.acc.ticket.issueTicket.IssueTicket;
import com.sso.acc.ticket.manageTicket.ManageTicket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * @author Lee
 * Date: 2020/6/2 14:07
 * Description: create ticket
 */
@Controller
@RequestMapping("/createTicket")
public class CreateTicketController {

    @Value("${server.servlet.context-path}")
    private String rootPath;

    @RequestMapping("/test1")
    public String test1() {
        return "test";
    }

    @RequestMapping("/getFirstTicket")
    public String getFirstTicket(String loginId, HttpServletResponse response) {
        ProxyTicket proxyTicket = new ProxyTicket();
        String firstTicket = proxyTicket.createFirstTicket();
        ManageTicket.sessionInfo.put("loginDate", System.currentTimeMillis());
        ManageTicket.sessionInfo.put("loginId", loginId);
        ManageTicket.firstTicketMap.put(firstTicket, ManageTicket.sessionInfo);
        Cookie cookie = new Cookie("FT", firstTicket);
        response.addCookie(cookie);
        return firstTicket;
    }

    @RequestMapping("/getSecondTicket")
    public void getSecondTicket(String loginId, String service, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        String firstTicket = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("FT".equals(cookie.getName())) {
                    firstTicket = cookie.getValue();
                    break;
                }
            }
        }
        if (firstTicket == null) {
            //"empty FT.";
            response.sendRedirect("/acc/login");
            return;
        }
        IssueTicket issueTicket = new IssueTicket();
        String secondTicket = issueTicket.createSecondTicket(firstTicket);
        if (secondTicket == null) {
            //expired or invalid FT.";
            response.sendRedirect("/acc/login");
            return;
        }
        ManageTicket.sessionInfo.put("loginDate", System.currentTimeMillis());
        ManageTicket.sessionInfo.put("loginId", loginId);
        ManageTicket.secondTicketMap.put(secondTicket, ManageTicket.sessionInfo);
        Cookie cookie = new Cookie("ST", secondTicket);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.sendRedirect(service);
    }
}
