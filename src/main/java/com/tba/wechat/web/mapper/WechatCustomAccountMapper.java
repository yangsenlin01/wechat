package com.tba.wechat.web.mapper;

import com.tba.wechat.config.mybatisplus.custommapper.CustomMapper;
import com.tba.wechat.web.domain.entity.WechatCustomAccount;

import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author YangSenLin
 * @since 2023-4-4 13:54
 */
public interface WechatCustomAccountMapper extends CustomMapper<WechatCustomAccount> {

    List<WechatCustomAccount> listAll();

}
