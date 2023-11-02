package com.ohsproject.ohs.global.resolver;

import com.ohsproject.ohs.global.annotation.CurrentMember;
import com.ohsproject.ohs.member.dto.request.MemberInfo;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Component
public class CurrentMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String SESSION_ATTRIBUTE_NAME = "memberId";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpSession session = Objects.requireNonNull(webRequest.getNativeRequest(HttpServletRequest.class)).getSession();
        Long id = (Long) session.getAttribute(SESSION_ATTRIBUTE_NAME);
        return new MemberInfo(id);
    }
}
