package com.tba.wechat.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * description
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-29 18:42
 */

@Getter
@Setter
@ToString
@TableName("wechat_message")
public class WechatMessage {

    @TableId
    private Long id;

    @NotBlank(message = "开发者微信号不能为空")
    private String toUserName;

    @NotBlank(message = "发送方帐号不能为空")
    private String fromUserName;

    @NotNull(message = "消息创建时间不能为空")
    private Long createTime;

    @NotBlank(message = "文本消息内容不能为空")
    private String content;

    @NotBlank(message = "消息id不能为空")
    private Long msgId;
}
