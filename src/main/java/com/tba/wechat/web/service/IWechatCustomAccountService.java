package com.tba.wechat.web.service;

import com.tba.wechat.config.mybatisplus.service.ICustomService;
import com.tba.wechat.web.domain.entity.WechatCustomAccount;

/**
 * <p>
 * description
 * </p>
 *
 * @author YangSenLin
 * @since 2023-4-4 13:55
 */
public interface IWechatCustomAccountService extends ICustomService<WechatCustomAccount> {

    /**
     * 添加客服
     *
     * @param token
     * @param account
     * @param nickName
     * @param pwd
     * @return
     */
    boolean addCustomAccount(String token, String account, String nickName, String pwd);

    /**
     * 修改客服
     *
     * @return
     */
    boolean modifyCustomAccount();

    /**
     * 上传客服头像
     *
     * @return
     */
    boolean uploadCustomHeadImg();

    /**
     * 客服发消息给用户
     *
     * @return
     */
    boolean sendMessageByCustom();

}
