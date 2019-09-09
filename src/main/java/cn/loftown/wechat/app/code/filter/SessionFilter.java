package cn.loftown.wechat.app.code.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;


@Component // 将过滤器交给容器，也就是让过滤器起作用
@WebFilter(filterName = "SessionFilter", urlPatterns = "/*")
public class SessionFilter implements Filter {
    // 标示符：表示当前用户未登录(可根据自己项目需要改为json样式)
    String NO_LOGIN = "您还未登录";
    // 不需要登录就可以访问的路径(比如:注册登录等)
    String[] includeUrls = new String[] { "/css/", "/js/", ".html", "/img/", "/getticket", "/messageback",
            "/login", "/index" };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);// session为空
        String uri = request.getRequestURI();
        // 是否需要过滤
        boolean needFilter = isNeedFilter(uri);
        // 不需要过滤直接传给下一个过滤器
        if (!needFilter) {
            filterChain.doFilter(request, response);
        } else { // 需要过滤器
            // session中包含user对象,则是登录状态
            if (session != null && session.getAttribute("shopid") != null) {
                System.out.println("shopid:" + session.getAttribute("shopid"));
                filterChain.doFilter(request, response);
            } else {
                // 重定向到登录页
                // response.sendRedirect("http://mmm.sss.com.cn/smallProgram/mi/login");
                request.getRequestDispatcher("http://mmm.sss.com.cn/smallProgram/mi/login").forward(request,
                        response);
                return;
            }
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    /**
     * @Author: xxxxx
     * @Description: 是否需要过滤
     * @Date: 2018-03-12 13:20:54
     * @param uri
     */
    public boolean isNeedFilter(String uri) {
//        for (String includeUrl : includeUrls) {
//            if (uri.contains(includeUrl)) {
//                return false;
//            }
//        }
//
//        return true;
        return false;
    }
}
