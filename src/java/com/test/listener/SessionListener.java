package com.test.listener;

import com.example.demo.UserController;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String username = (String) session.getAttribute("username");
        if (username != null) {
            // 只移除当前 session 的记录，避免同账号其他客户端被误退出
            UserController.removeLoggedInUser(username, session.getId());
            System.out.println("Session 销毁：已移除用户 " + username + " 的登录记录");
        }
    }
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // 可以选择性实现创建时的逻辑
    	
    }
}
