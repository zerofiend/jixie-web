package com.jixiebackstage.springboot.controller.dto;

import com.jixiebackstage.springboot.entity.Menu;
import lombok.Data;

import java.util.List;

/**
 * 接收前端登录请求的数据
 */
@Data
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token;
    private String role;
    private List<Menu> menus;
}
