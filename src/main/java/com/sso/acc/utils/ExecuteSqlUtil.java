package com.sso.acc.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import java.sql.*;
import java.util.Properties;

/**
 * @author Lee
 * Date: 2020/6/1 17:40
 * Description: sql util
 */
public class ExecuteSqlUtil {

    private static String driver;

    private static String url;

    private static String userName;

    private static String password;

    static{
        Resource resource = new ClassPathResource("application-sql.properties");
        Properties properties;
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
            driver = properties.getProperty("datasource.driver-class-name");
            url = properties.getProperty("datasource.url");
            userName = properties.getProperty("datasource.username");
            password = properties.getProperty("datasource.password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            System.out.println("连接数据库失败.");
            e.printStackTrace();
        }
        return connection;
    }

    public static Statement getStatement(Connection connection) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;
    }

    public static PreparedStatement getPreparedStatement(Connection connection, String sql) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    public static ResultSet executeQuery(Statement statement, String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public static ResultSet executeQuery(Connection connection, String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * 关闭连接
     *
     * @param connection connection
     */
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭执行方法
     *
     * @param statement statement
     */
    public static void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭结果集
     *
     * @param resultSet resultSet
     */
    public static void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//            要执行SQL语句，必须获得java.sql.Statement实例，Statement实例分为以下3 种类型：
//
//    执行静态SQL语句。通常通过Statement实例实现。
//    执行动态SQL语句。通常通过PreparedStatement实例实现。
//    执行数据库存储过程。通常通过CallableStatement实例实现。

    /*
    Statement接口提供了三种执行SQL语句的方法：executeQuery 、executeUpdate   和execute  

    ResultSet executeQuery(String sqlString)：执行查询数据库的SQL语句   ，返回一个结果集（ResultSet）对象。
    int executeUpdate(String sqlString)：用于执行INSERT、UPDATE或   DELETE语句以及SQL DDL语句，如：CREATE TABLE和DROP TABLE等  
    execute(sqlString):用于执行返回多个结果集、多个更新计数或二者组合的   语句。
*/



}
