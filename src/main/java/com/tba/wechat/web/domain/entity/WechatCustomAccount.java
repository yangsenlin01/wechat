package com.tba.wechat.web.domain.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * description
 * </p>
 *
 * @author YangSenLin
 * @since 2023-4-4 13:52
 */

@Getter
@Setter
@ToString
@TableName("wechat_custom_account")
@KeySequence(value = "wechat_custom_account_seq")
public class WechatCustomAccount {

    @TableId
    private Long id;

    @NotBlank(message = "客服账号不能为空")
    private String kfAccount;

    @NotBlank(message = "客服昵称不能为空")
    private String kfNick;

    private String kfId;

    /**
     * 客服头像地址
     */
    private String kfHeadImgUrl;
}
