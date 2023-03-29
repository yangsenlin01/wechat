package com.tba.wechat.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 实体类
 *
 * @author 1050696985@qq.com
 * @since 2023-03-29 11:20:56
 */

@Getter
@Setter
@ToString
@TableName("wechat_token")
public class WechatToken {

    @TableId
    private Long id;

    /**
     * token
     */
    @NotBlank(message = "token不能为空")
    private String token;

    /**
     * 过期时间
     */
    @NotNull(message = "过期时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiresTime;

}