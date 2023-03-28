package com.tba.wechat.util;

import cn.hutool.core.util.StrUtil;
import com.tba.wechat.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * sha1 工具类
 * </p>
 *
 * @author theAplyBoy
 * @version V1.0
 * @since 2021-12-10 17:18
 */
public class Sha1Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sha1Utils.class);

    private Sha1Utils() {
    }

    public static void main(String[] args) {
        String stamp = String.valueOf(System.currentTimeMillis());
        String loginid = "xx";
        System.out.println(stamp);
        System.out.println(encryption("mVqcbH" + loginid + stamp));
    }

    public static String encryption(String data) {
        if (StrUtil.isBlank(data)) {
            return null;
        }
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] byteArray = data.getBytes(StandardCharsets.UTF_8);
            byte[] md5Bytes = sha.digest(byteArray);
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = md5Byte & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException("sha1 encryption fail：", e);
        }
    }

    /**
     * 比较字符串经过sha1加密后与目标值是否一致
     *
     * @param text
     * @param signStr
     * @return 一致返回true，不一致返回false
     */
    public static boolean match(String text, String signStr) {
        String encryption = encryption(text);
        return StrUtil.equals(encryption, signStr);
    }
}
