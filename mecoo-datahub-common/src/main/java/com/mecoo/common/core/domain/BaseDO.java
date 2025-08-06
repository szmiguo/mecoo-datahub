package com.mecoo.common.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author: lin
 * @date: 2025-07-01 10:51
 */
@Data
public class BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    public void initTime() {
        this.createTime = new Date();
        this.updateTime = getCreateTime();
    }

    public void initTime(Date date) {
        this.createTime = date;
        this.updateTime = date;
    }

    public void refreshUpdateTime() {
        this.updateTime = new Date();
    }

    public void refreshUpdateTime(Date date) {
        this.updateTime = date;
    }

    public static void batchInitTime(List<? extends BaseDO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Date currentTime = new Date();
        for (BaseDO baseDO : list) {
            baseDO.setCreateTime(currentTime);
            baseDO.setUpdateTime(currentTime);
        }
    }

    public static void batchInitTime(List<? extends BaseDO> list, Date date) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (BaseDO baseDO : list) {
            baseDO.setCreateTime(date);
            baseDO.setUpdateTime(date);
        }
    }


    public static void batchRefreshUpdateTime(List<? extends BaseDO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Date currentTime = new Date();
        for (BaseDO baseDO : list) {
            baseDO.setUpdateTime(currentTime);
        }
    }


}
