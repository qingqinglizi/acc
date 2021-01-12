package com.sso.acc.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lee
 * Date: 2020/6/5 10:11
 * Description: service exception
 */
@ControllerAdvice
public class DealException {

    /**
     * return Object info
     *
     * @param ex ex
     * @return Object
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Map errorHandler(Exception ex) {
        Map map = new HashMap();
        map.put("code", 500);
        //判断异常的类型,返回不一样的返回值
        if(ex instanceof AccRunTimeException){
            map.put("msg", "这是自定义异常");
        }
        return map;
    }

    /**
     * deal exception and return a view
     *
     * @param ex ex
     * @return view
     */
    @ExceptionHandler(value = AccRunTimeException.class)
    public ModelAndView myErrorHandler(AccRunTimeException ex) {
        ModelAndView modelAndView = new ModelAndView();
        //指定错误页面的模板页
        modelAndView.setViewName("error");
        modelAndView.addObject("code", ex.getCode());
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }
}
