package com.tba.wechat.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 实体类
 *
 * @author 1050696985@qq.com
 * @since 2023-03-29 11:20:56
 */

@Getter
@Setter
@ToString
@TableName("wechat_white_list")
public class WechatWhiteList {

    @TableId
    private Long id;

    @NotBlank(message = "用户的openId不能为空")
    private String openId;

}