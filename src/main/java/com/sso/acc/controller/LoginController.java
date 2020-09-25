package com.sso.acc.controller;

import com.sso.acc.ticket.issueTicket.IssueTicket;
import com.sso.acc.ticket.manageTicket.ManageTicket;
import com.sso.acc.ticket.validateTicket.ValidateFirstTicket;
import com.sso.acc.ticket.validateTicket.ValidateSecondTicket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: Lee
 * @Date 2020/6/1 17:18
 * @Description:
 */
@Controller
public class LoginController {

    @Value("${server.servlet.context-path}")
    private String rootPath;

    @RequestMapping("/login")
    public String getLoginView(@RequestParam(required = false) String service, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (service == null || cookies == null) {
            return "loginView";
        }
        boolean existST = false;
        boolean existFT = false;
        String firstCookieComment = null;
        String secondCookieComment = null;
        for (Cookie cookie : cookies) {
            if ("FT".equals(cookie.getName()) && rootPath.equals(cookie.getPath())) {
                existFT = true;
                firstCookieComment = cookie.getValue();
            }
            if ("ST".equals(cookie.getName())) {
                existST = true;
                secondCookieComment = cookie.getValue();
            }
        }
        if (existST) {
            //validate ST
            if (ValidateSecondTicket.getInstance().validateSecondTicket(secondCookieComment)) {
                response.sendRedirect(service);
                return null;
            }
            //remove ST
            removeCookie("ST", "/", response);
        }
        if (existFT) {
            //validate FT
            if (ValidateFirstTicket.getInstance().validateFirstTicket(firstCookieComment)) {
                IssueTicket issueTicket = new IssueTicket();
                String secondTicket = issueTicket.createSecondTicket(firstCookieComment);
                if (secondTicket == null) {
                    //remove FT
                    removeCookie("FT", rootPath, response);
                    response.sendRedirect("/login?service=" + service);
                    return null;
                }
                Cookie cookie = new Cookie("ST", secondTicket);
                cookie.setPath("/");
                response.addCookie(cookie);
                response.sendRedirect(service);
                return null;
            }
        }
        return "loginView";
    }

    /**
     * remove cookie
     *
     * @param cookieName
     * @param cookiePath
     * @param response
     */
    public void removeCookie(String cookieName, String cookiePath, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


}
