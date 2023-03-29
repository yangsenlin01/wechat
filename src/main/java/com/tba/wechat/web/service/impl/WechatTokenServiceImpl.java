package com.tba.wechat.web.service.impl;

import com.tba.wechat.config.mybatisplus.service.impl.CustomServiceImpl;
import com.tba.wechat.web.domain.entity.WechatToken;
import com.tba.wechat.web.mapper.WechatTokenMapper;
import com.tba.wechat.web.service.IWechatTokenService;
import org.springframework.stereotype.Service;

/**
 * (WechatToken) Service实现类
 *
 * @author 1050696985@qq.com
 * @since 2023-03-29 11:20:56
 */
@Service
public class WechatTokenServiceImpl extends CustomServiceImpl<WechatTokenMapper, WechatToken> implements IWechatTokenService {
}
