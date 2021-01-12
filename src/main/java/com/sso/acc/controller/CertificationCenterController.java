package com.sso.acc.controller;

import com.sso.acc.entity.User;
import com.sso.acc.exception.AccRunTimeException;
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
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Lee
 * Date: 2020/6/1 17:37
 * Description: check ticket
 */
@Controller
@RequestMapping("/certification")
public class CertificationCenterController {

    @Value("${acc.login.sql}")
    private String loginSql;

    @Value("${server.servlet.context-path}")
    private String rootPath;

    private final String cookieNameST = "ST";

    private final String cookiePath = "/";

    /**
     * check login info
     *
     * @param user     user
     * @param service  service
     * @param request  request
     * @param response response
     * @return check login result
     */
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
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            ExecuteSqlUtil.closeStatement(preparedStatement);
            ExecuteSqlUtil.closeConnection(connection);
        }
        return "loginView";
    }

    private void writeFirstTicketToCookie(String loginId, HttpServletRequest request, HttpServletResponse response) throws AccRunTimeException, UnknownHostException {
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

    @ResponseBody
    //方法中少了 @ResponseBody 注释，所以无法将 Boolean 类型的数据传到前台，
    // 会报错：IllegalArgumentException: Unknown return value type: java.lang.Boolean
    @RequestMapping(value = "/checkSecondTicket", method = RequestMethod.GET)
    public boolean checkSecondTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cookieValue = request.getHeader("Cookie");
        if (ValidateSecondTicket.getInstance().validateSecondTicket(cookieValue)) {
            return true;
        }
        removeCookie(response);
        return false;
    }

    /**
     * remove cookie
     *
     * @param response response
     */
    private void removeCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieNameST, null);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
