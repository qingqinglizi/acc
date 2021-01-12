package com.sso.acc.controller;

import com.sso.acc.ticket.manageTicket.ManageTicket;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Lee
 * Date: 2020/6/22 11:39
 * Description: request for logout
 */
@Controller
public class LogoutController {

    @RequestMapping("/logout")
    public String logoutSystem(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "logout";
        }
        String cookieName;
        String cookieValue;
        String cookiePath;
        for (Cookie cookie : cookies) {
            cookieName = cookie.getName();
            cookieValue = cookie.getValue();
            cookiePath = cookie.getPath();
            if ("FT".equals(cookieName) && ManageTicket.firstTicketMap.containsKey(cookieValue)) {
                ManageTicket.firstTicketMap.remove(cookieValue);
                removeCookie(cookieName, cookiePath, response);
            }
            if ("ST".equals(cookieName) && ManageTicket.secondTicketMap.containsKey(cookieValue)) {
                ManageTicket.secondTicketMap.remove(cookieValue);
                removeCookie(cookieName, cookiePath, response);
            }
        }
        return "logout";
    }

    /**
     * remove cookie
     *
     * @param cookieName cookieName
     * @param cookiePath cookiePath
     * @param response response
     */
    private void removeCookie(String cookieName, String cookiePath, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
