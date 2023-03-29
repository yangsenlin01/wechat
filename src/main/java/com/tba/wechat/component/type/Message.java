package com.tba.wechat.component.type;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * {@link https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_standard_messages.html}
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-29 16:16
 */

@Setter
@Getter
public class Message {

    private String toUserName;
    private String fromUserName;
    private Long createTime;
    private String msgType;
    private Long msgId;
    private String msgDataId;
    private String idx;

}
