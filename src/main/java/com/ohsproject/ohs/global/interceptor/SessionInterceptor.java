package com.ohsproject.ohs.global.interceptor;

import com.ohsproject.ohs.global.annotation.Login;
import com.ohsproject.ohs.global.exception.SessionNotValidException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private static final String SESSION_ATTRIBUTE_NAME = "memberId";

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (isLoginAnnotation(handler)) {
            checkLogin(request);
            return true;
        }

        HttpSession session = request.getSession(true);
        return true;
    }

    private boolean isLoginAnnotation(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        return handlerMethod.getMethodAnnotation(Login.class) != null;
    }

    private void checkLogin(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SESSION_ATTRIBUTE_NAME) == null) {
            throw new SessionNotValidException();
        }
    }
}
