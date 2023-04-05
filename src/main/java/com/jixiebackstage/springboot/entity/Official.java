package com.jixiebackstage.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-09
 */
@Getter
@Setter
@ApiModel(value = "Official对象", description = "")
public class Official implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("发布人")
    private String user;

    @ApiModelProperty("时间")
    private String time;

    @ApiModelProperty("发布人id")
    private Integer userId;

    @ApiModelProperty("是否置顶")
    private Boolean top;

    @ApiModelProperty("文件分类")
    private Integer type;


}
