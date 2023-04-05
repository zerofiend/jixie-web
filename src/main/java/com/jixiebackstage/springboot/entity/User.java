package com.jixiebackstage.springboot.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 *
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-15
 */
@Getter
@Setter
@TableName("sys_user")
@ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户名")
    @Alias("用户名")
    private String username;

    @ApiModelProperty("密码")
    @Alias("密码")
    private String password;

    @ApiModelProperty("昵称")
    @Alias("昵称")
    private String nickname;

    @ApiModelProperty("专业")
    @Alias("专业")
    private String speciality;

    @ApiModelProperty("班级")
    @Alias("班级")
    private String gradeClass;

    @ApiModelProperty("故乡")
    @Alias("故乡")
    private String address;

    @ApiModelProperty("创建时间")
    @Alias("创建时间")
    private Timestamp createTime;

    @ApiModelProperty("头像url")
    @Alias("头像")
    private String avatarUrl;

    @ApiModelProperty("OJ题目通过量")
    @Alias("OJ题目通过量")
    private Integer passNum;

    @ApiModelProperty("角色")
    @Alias("角色")
    private String role;
    
}
