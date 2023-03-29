package com.tba.wechat.web.service.impl;

import com.tba.wechat.config.mybatisplus.service.impl.CustomServiceImpl;
import com.tba.wechat.web.domain.entity.WechatMessage;
import com.tba.wechat.web.mapper.WechatMessageMapper;
import com.tba.wechat.web.service.IWechatMessageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * description
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-29 18:46
 */

@Service
public class WechatMessageServiceImpl extends CustomServiceImpl<WechatMessageMapper, WechatMessage> implements IWechatMessageService {
}
