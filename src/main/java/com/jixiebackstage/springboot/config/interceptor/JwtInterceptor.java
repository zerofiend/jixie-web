package com.jixiebackstage.springboot.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.jixiebackstage.springboot.Exception.ServiceException;
import com.jixiebackstage.springboot.common.Constants;
import com.jixiebackstage.springboot.config.AuthAccess;
import com.jixiebackstage.springboot.entity.User;
import com.jixiebackstage.springboot.service.IUserService;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {
    @Resource
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        } else {
            HandlerMethod h = (HandlerMethod) handler;
            AuthAccess authAccess = h.getMethodAnnotation(AuthAccess.class);
            if (authAccess != null) {
                return true;
            }
        }
        //  执行认证
        if (StrUtil.isBlank(token)) {
            throw new ServiceException(Constants.CODE_401, "没有token，请重新登录");
        }

        // 获取 token 中的 user id
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new ServiceException(Constants.CODE_401, "token验证失败");
        }
        //  根据userId查询数据库的id
        User user = userService.getUserById(Integer.parseInt(userId));
        if (user == null) {
            System.out.println("未查到用户，请重新输入");
            throw new ServiceException(Constants.CODE_401, "用户不存在，请重新输入");
        }
        // 用户密码加签验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token);  //  验证token
        } catch (JWTVerificationException e) {
            System.out.println("用户验证错误，请重新输入");
            throw new ServiceException(Constants.CODE_401, "用户不存在，请重新输入");
        }
        return true;
    }
}
