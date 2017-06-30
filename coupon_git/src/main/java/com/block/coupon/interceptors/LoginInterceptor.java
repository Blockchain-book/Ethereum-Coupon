package com.block.coupon.interceptors;

import com.block.coupon.po.BankStaffCustom;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by wufaxiang on 2016/12/22.
 */
public class LoginInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        // 获取请求的url
        String url = httpServletRequest.getRequestURI();
        // 除了登录界面，都进行拦截
        if(url.indexOf("login.action")>=0 || url.indexOf("register.action")>=0
                || url.indexOf("/merchant")>=0 || url.indexOf("/consumer")>=0){
            return true;
        }
        HttpSession session = httpServletRequest.getSession();
        BankStaffCustom bankStaffCustom = (BankStaffCustom) session.getAttribute("currenBankStaff");
        if(bankStaffCustom != null) {
            String position = bankStaffCustom.getPosition();
            if(position.equals("1") && !url.contains("toReCheck.action")) {
                return true;
            }else if(position.equals("2") && !url.contains("toFirstTrial")) {
                return true;
            }
        }
        httpServletRequest.getRequestDispatcher("/WEB-INF/jsp/bank/login.jsp").forward(httpServletRequest, httpServletResponse);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}