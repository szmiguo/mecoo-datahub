package com.mecoo.common.config;

import cn.hutool.core.convert.Converter;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.convert.impl.NumberConverter;
import cn.hutool.core.util.ReflectUtil;

import com.mecoo.common.annotation.IntPrimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 如果在用 hutool 的转换器的时候，比如用 hutool 的 BeanUtil 拷贝 JavaBean 的时候，
 * 会用如下逻辑覆盖默认的转换逻辑
 * @author: lin
 * @date: 2023-07-12 17:46
 */
@Configuration
public class EnumConverterConfig {

    @Bean
    public void registerConverter() {
        ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
        //自定义hutool 的转换器
        //如果目标类型是 Int 型的时候，会执行如下逻辑
        converterRegistry.putCustom(Integer.class, (Converter<Number>) (value, defaultValue) -> {
            if (value == null) {
                return null;
            }
            //如果原始值为枚举，且包含 IntPrimeValue 注解，那么就从枚举值里面取 IntPrimeValue 定义的字段的值作为目标值
            if (value instanceof Enum && value.getClass().isAnnotationPresent(IntPrimeValue.class)) {
                IntPrimeValue anno = value.getClass().getAnnotation(IntPrimeValue.class);
                return (Number) ReflectUtil.getFieldValue(value, anno.valueField());
            }
            return new NumberConverter().convert(value, defaultValue);
        });

    }


}
