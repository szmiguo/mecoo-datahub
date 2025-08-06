package com.mecoo.spider.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum TrackTaskType implements IEnum<String> {


    POST("post");

    @EnumValue
    public final String val;


    @Override
    public String getValue() {
        return val;
    }
}
