package com.ohsproject.ohs.global.interceptor;

import com.ohsproject.ohs.global.annotation.Login;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        if (!(handler instanceof HandlerMethod) || !isLoginAnnotation(handler)) {
            return true;
        }

        checkSession(request);
        return true;
    }

    private boolean isLoginAnnotation(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        return handlerMethod.getMethodAnnotation(Login.class) != null;
    }

    private void checkSession(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.isNew()) {
            throw new RuntimeException("세션 없음");
        }

        Object member = session.getAttribute("member");
        if (member == null) {
            throw new RuntimeException("잘못된 세션");
        }
    }
}
