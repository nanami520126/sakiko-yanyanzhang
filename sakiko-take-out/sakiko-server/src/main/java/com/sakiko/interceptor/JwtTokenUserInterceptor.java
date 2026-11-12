package com.sakiko.interceptor;


import com.sakiko.context.BaseContext;
import com.sakiko.properties.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisTemplate redisTemplate;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        //2、从redis中校验令牌
        try {
            // 业务：原来的JWT解析，注释掉
            // Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            // Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            // String username = claims.get(JwtClaimsConstant.USERNAME).toString();

            // 从redis中查询令牌对应的员工id
            Object empId = redisTemplate.opsForValue().get("login:token:" + token);
            if (empId == null) {
                //令牌不存在，响应401
                response.setStatus(401);
                return false;
            }
            log.info("当前用户id：{}", empId);
            BaseContext.setCurrentId(Long.valueOf(empId.toString()));
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }

}
