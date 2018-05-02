package com.hzz.security;
import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.User;
import com.hzz.model.UserRole;
import com.hzz.security.annotation.Privileges;
import com.hzz.service.UserRoleService;
import com.hzz.service.UserService;
import com.hzz.utils.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


@Component
public class SecurityInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            return doPreHandle(request, response, (HandlerMethod) handler);
        } catch (CommonException e) {

        } catch (Exception e) {
            logger.error("系统异常",e);
        }
        return false;
    }

    private boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws CommonException, IOException {
        HandlerMethod method = handler;
        Privileges privileges = method.getMethodAnnotation(Privileges.class);
        Long userId= (Long) request.getSession().getAttribute("userId");
        Long roleId=null;
        User user=userService.getUserById(userId);
        if(user!=null){
            UserRole userRole=userRoleService.getRole(user.getRoleId());
            if(userRole!=null)
                 roleId=userRole.getId();
        }
        if(privileges!=null&&!userRoleService.checkPrivileges(roleId,privileges.value())){
            writeError(response,400,"您没有权限访问页面");
            return false;
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        writeError(response, String.format("WECHAT_%d", code), message);
    }

    private void writeError(HttpServletResponse response, String code, String message) throws IOException {
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("errorMsg", message);
        writer.write(JsonMapper.nonEmptyMapper().toJson(map));
        writer.flush();
        writer.close();
    }

//    private String getParameterValue(HttpServletRequest request, String parameterName, boolean findCookie) {
//        String value = request.getParameter(parameterName);
//        if (StringUtil.isBlank(value)) {
//            if(findCookie){
//                Cookie[] cookies = request.getCookies();
//                if (cookies != null) {
//                    for (Cookie c : cookies) {
//                        if (c.getName().equalsIgnoreCase(parameterName)) {
//                            return c.getValue();
//                        }
//                    }
//                }
//            }
//            return request.getHeader(parameterName);
//        }
//        return value;
//    }
}
