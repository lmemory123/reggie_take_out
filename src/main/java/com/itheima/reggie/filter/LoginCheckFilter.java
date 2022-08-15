package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = ((HttpServletRequest) servletRequest).getRequestURI();
        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg",
                "*login*",
//                "/front/page/login.html"
        };
        Boolean check = this.check(uri, uris);
        if (check) {
            log.info("本次请求不需要过滤");
            filterChain.doFilter(request, response);
            return;
        }


        /*后台系统校验*/
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户登陆了不需要过滤");
            Long empID = (Long) request.getSession().getAttribute(("employee"));
            log.info("线程id=" + Thread.currentThread().getId());
            BaseContext.setThreadLocal(empID);
            filterChain.doFilter(request, response);
            return;
        }


        /*移动端校验用户*/
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户登陆了不需要过滤");
            Long userID = (Long) request.getSession().getAttribute(("user"));
            log.info("线程id=" + Thread.currentThread().getId());
            BaseContext.setThreadLocal(userID);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登陆，拦截请求");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        log.info("拦截到请求" + request.getRequestURI());

    }

    /**
     * 判断是非需要过滤
     *
     * @param requestURI
     * @param urls
     * @return
     */
    public Boolean check(String requestURI, String[] urls) {
        for (String url : urls) {
            PATH_MATCHER.match(url, requestURI);
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }


}
