package com.mecoo.operation.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: lin
 * @date: 2025-07-10 12:36
 */
public class InstagramUtil {

    /**
     * 从URL里面提取ShortCode
     * 比如输入"https://www.instagram.com/reel/DLryM_tSSM2/" -> DLryM_tSSM2,
     * "https://www.instagram.com/p/DLrE-11zIwT/" -> DLrE-11zIwT,
     *
     * @param url
     * @return
     */
    public static String extractShortcode(String url) {
        // 正则表达式匹配/reel/或/p/后面的shortcode部分
        Pattern pattern = Pattern.compile("instagram\\.com/(?:reel|p)/([^/]+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null; // 如果没有匹配到，返回null
    }

}
