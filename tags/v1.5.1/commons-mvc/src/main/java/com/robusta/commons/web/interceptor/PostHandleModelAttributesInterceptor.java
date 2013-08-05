package com.robusta.commons.web.interceptor;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public abstract class PostHandleModelAttributesInterceptor extends HandlerInterceptorAdapter {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(modelAndView != null && !modelAndView.getModelMap().isEmpty()) {
            postHandleValidModelMap(request, response, handler, modelAndView.getModel());
        }
    }

    protected abstract void postHandleValidModelMap(HttpServletRequest request, HttpServletResponse response, Object handler, Map<String, Object> model);
}
