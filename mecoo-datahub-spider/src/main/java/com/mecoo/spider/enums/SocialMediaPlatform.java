package com.mecoo.spider.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 社交媒体平台，对应数据库 post_sm 字段
 *
 * @author mecoo
 */
@Getter
@AllArgsConstructor
public enum SocialMediaPlatform implements IEnum< String> {


    FACEBOOK("facebook"),
    INSTAGRAM("instagram"),
    TWITTER("twitter"),
    YOUTUBE("youtube"),
    TIKTOK("tiktok"),
    UNKNOWN("unknown");

    @EnumValue
    public final String val;


    @Override
    public String getValue() {
        return val;
    }
}
