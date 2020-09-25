package com.sso.acc.controller;

import com.sso.acc.entity.User;
import com.sso.acc.ticket.ProxyTicket.ProxyTicket;
import com.sso.acc.ticket.manageTicket.ManageTicket;
import com.sso.acc.ticket.validateTicket.ValidateSecondTicket;
import com.sso.acc.utils.ExecuteSqlUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Auther: Lee
 * @Date 2020/6/1 17:37
 * @Description:
 */
@Controller
@RequestMapping("/certification")
public class CertificationCenterController {

    @Value("${acc.login.sql}")
    private String loginSql;

    @Value("${server.servlet.context-path}")
    private String rootPath;

    @RequestMapping(value = "/checkLoginResult", method = RequestMethod.POST)
    public String certificationLoginInfo(User user, String service, HttpServletRequest request, HttpServletResponse response) {
        if (user == null) {
            return "loginView";
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet;
        String correctPassword = null;
        try {
            connection = ExecuteSqlUtil.getConnection();
            preparedStatement = connection.prepareStatement(loginSql);
            preparedStatement.setString(1, user.getLoginId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                correctPassword = resultSet.getString("password");
            }
            if (user.getPassword().equals(correctPassword)) {
                writeFirstTicketToCookie(user.getLoginId(), request, response);
                response.sendRedirect("/acc/createTicket/getSecondTicket?service=" + service + "&loginId=" + user.getLoginId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ExecuteSqlUtil.closeStatement(preparedStatement);
            ExecuteSqlUtil.closeConnection(connection);
            return "loginView";
        }
    }

    public void writeFirstTicketToCookie(String loginId, HttpServletRequest request, HttpServletResponse response) {
        ProxyTicket proxyTicket = new ProxyTicket();
        String firstTicket = proxyTicket.createFirstTicket();
        ManageTicket.sessionInfo.put("loginDate", System.currentTimeMillis());
        ManageTicket.sessionInfo.put("loginId", loginId);
        ManageTicket.firstTicketMap.put(firstTicket, ManageTicket.sessionInfo);
        request.setAttribute("loginId", loginId);
        Cookie cookie = new Cookie("FT", firstTicket);
        cookie.setPath(rootPath);
        response.addCookie(cookie);
    }

//    @RequestMapping(value = "/checkFirstTicket", method = RequestMethod.POST)
//    public void checkFirstTicket(String service, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            if ("FT".equals(cookie.getName())) {
//                if (validateFirstTicket(cookie)) {
//
//                }
//                response.sendRedirect("/login?service=" + service);
//            }
//        }
//        response.sendRedirect("/login?service=" + service);
//    }

//    public boolean validateFirstTicket(Cookie cookie) {
//        if (!ManageTicket.firstTicketMap.containsKey(cookie.getValue())) {
//            return false;
//        }
//        Map sessionInfo = (Map) ManageTicket.firstTicketMap.get(cookie.getValue());
//        long loginDate = (long) sessionInfo.get("loginDate");
//        if (System.currentTimeMillis() - loginDate > ftExpiredTime * 1000) {
//            return false;
//        }
//        return true;
//    }

    @ResponseBody
    //方法中少了 @ResponseBody 注释，所以无法将 Boolean 类型的数据传到前台，
    // 会报错：IllegalArgumentException: Unknown return value type: java.lang.Boolean
    @RequestMapping(value = "/checkSecondTicket", method = RequestMethod.GET)
    public boolean checkSecondTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            if ("ST".equals(cookie.getName())) {
//                if (ValidateSecondTicket.getInstance().validateSecondTicket(cookie.getValue())) {
//                    return true;
//                }
//            }
//        }
        String cookieValue = request.getHeader("Cookie");
        if (ValidateSecondTicket.getInstance().validateSecondTicket(cookieValue)) {
            return true;
        }
        removeCookie("ST", "/", response);
        return false;
    }

    /**
     * remove cookie
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
