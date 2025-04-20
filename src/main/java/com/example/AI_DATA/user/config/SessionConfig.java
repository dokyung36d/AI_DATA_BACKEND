package com.example.AI_DATA.user.config;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.HashMap;
import java.util.Map;


@Configuration
@WebListener
@EnableRedisHttpSession
public class SessionConfig implements HttpSessionListener {
    public static Map<String, HttpSession> sessions = new HashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        sessions.put(httpSessionEvent.getSession().getId(), httpSessionEvent.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        if (sessions.get(httpSessionEvent.getSession().getId()) != null) {
            sessions.get(httpSessionEvent.getSession().getId()).invalidate();
            sessions.remove(httpSessionEvent.getSession().getId());
        }
    }

    public synchronized static boolean checkSessionAlreadyExist(String compareId) {

        for (String sessionId : sessions.keySet()) {
            HttpSession httpSession =sessions.get(sessionId);

            if (httpSession == null || httpSession.getAttribute("loginId") == null) { return false; }
            if (httpSession.getAttribute("loginId").equals(compareId)) {
                removeSessionForDoubleLogin(sessionId);
                return true;
            }
        }

        return false;
    }

    public static void removeSessionForDoubleLogin(String userId) {
        if (userId == null || userId.length() == 0) { return; }

        sessions.get(userId).invalidate();
        sessions.remove(userId);
    }
}
