package com.robusta.commons.web.interceptor;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.robusta.commons.test.matchers.UtilityMatchers.isAnEmptyList;
import static com.robusta.commons.test.matchers.UtilityMatchers.isOfSize;
import static org.junit.Assert.assertThat;

public class PostHandleModelAttributesInterceptorTest {
    private PostHandleModelAttributesInterceptor interceptor;
    private List<String> testList = newArrayList();
    private Object handler;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        interceptor = new PostHandleModelAttributesInterceptor() {
            @Override
            protected void postHandleValidModelMap(HttpServletRequest request, HttpServletResponse response, Object handler, Map<String, Object> model) {
                testList.add("test");
            }
        };
    }

    @Test
    public void testPostHandle_whenModelAndViewIsNull_shouldNotProceedToPostHandleProcess_testListShouldBeEmpty() throws Exception {
        interceptor.postHandle(request, response, handler, null);
        assertThat(testList, isAnEmptyList(String.class));
    }

    @Test
    public void testPostHandle_whenModelAndViewIsValid_andUnderlyingModelIsEmpty_shouldProceedToPostHandleProcess_testListShouldBeNonEmpty() throws Exception {
        interceptor.postHandle(request, response, handler, new ModelAndView("test"));
        assertThat(testList, isAnEmptyList(String.class));
    }

    /**
     * @see ModelAndView - underlying model map will never be null.
     * @throws Exception
     */
    @Test
    public void testPostHandle_whenModelAndViewIsValid_andUnderlyingModelIsValid_shouldProceedToPostHandleProcess_testListShouldBeNonEmpty() throws Exception {
        interceptor.postHandle(request, response, handler, new ModelAndView("test", "model", new Object()));
        assertThat(testList, isOfSize(String.class, 1));
    }
}
