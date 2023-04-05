package com.jixiebackstage.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-06
 */
@Getter
@Setter
@TableName("t_comment")
@ApiModel(value = "Comment对象", description = "")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评论者id")
    private Integer userId;

    @ApiModelProperty("评论时间")
    private String time;

    @ApiModelProperty("父id")
    private Integer pid;

    @ApiModelProperty("最上级评论id")
    private Integer originId;

    @ApiModelProperty("关联文章id")
    private Integer articleId;

    @ApiModelProperty("被回复用户的id")
    private Integer replyId;

    @ApiModelProperty("回复文章的类型")
    private Integer type;

    @TableField(exist = false)
    private String nickname;

    @TableField(exist = false)
    private String avatarUrl;

    @TableField(exist = false)
    private List<Comment> children;

    @TableField(exist = false)
    private String replyName;
}
