package com.jixiebackstage.springboot.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jixiebackstage.springboot.entity.User;
import com.jixiebackstage.springboot.service.IUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenUtils {


    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    private static IUserService staticUserService;
    @Resource
    private IUserService userService;

    @PostConstruct
    public void setUserService() {
        staticUserService = userService;
    }

    /**
     * 生成 token
     *
     * @return
     */
    public static String genToken(String userId, String sign) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return JWT.create().withAudience(userId) // 将 user id 保存到 token 里面
                .withExpiresAt(date) //24小时有效后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 toke
    }

    /**
     * 获取当前用户信息
     */
    public static User getCurrentUser() {
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (token != null) {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.getById(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
