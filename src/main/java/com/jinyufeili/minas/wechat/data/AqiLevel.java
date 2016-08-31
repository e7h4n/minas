/**
 * @(#)${FILE_NAME}.java, 6/16/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.data;

import com.jinyufeili.minas.wechat.util.AqiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pw
 */
public class AqiLevel {

    public static final List<AqiLevel> US_AQI_LEVELS = new ArrayList<>();

    public static final List<AqiLevel> CN_AQI_LEVELS = new ArrayList<>();

    static {
        US_AQI_LEVELS.add(new AqiLevel("良好", "", 0, 15, 0, 50));
        US_AQI_LEVELS.add(new AqiLevel("中等", "", 15, 40, 50, 100));
        US_AQI_LEVELS.add(new AqiLevel("对敏感人群不健康", "", 40, 65, 100, 150));
        US_AQI_LEVELS.add(new AqiLevel("不健康", "", 65, 150, 150, 200));
        US_AQI_LEVELS.add(new AqiLevel("非常不健康", "", 150, 250, 200, 300));
        US_AQI_LEVELS.add(new AqiLevel("有毒害", "", 250, 500, 300, 500));
    }

    static {
        CN_AQI_LEVELS.add(new AqiLevel("优", "各类人群可正常活动。", 0, 35.0, 0, 50));
        CN_AQI_LEVELS
                .add(new AqiLevel("良", "极少数异常敏感人群应减少户外活动。", 35, 75.0, 50, 100));
        CN_AQI_LEVELS
                .add(new AqiLevel("轻度污染", "儿童、老年人及心脏病、呼吸系统疾病患者应减少长时间、高强度的户外锻炼。", 75, 115.0,
                        100, 150));
        CN_AQI_LEVELS.add(new AqiLevel("中度污染",
                "儿童、老年人及心脏病、呼吸系统疾病患者避免长时间、高强度的户外锻炼，一般人群适量减少户外运动。", 115, 150.0, 150,
                200));
        CN_AQI_LEVELS.add(new AqiLevel("重度污染",
                "儿童、老年人及心脏病、肺病患者应停留在室内，停止户外运动，一般人群减少户外运动。", 150, 250.0, 200, 300));
        CN_AQI_LEVELS.add(new AqiLevel("严重污染", "儿童、老年人和病人应停留在室内，避免体力消耗，一般人群避免户外活动。", 250,
                350.0, 300, 400));
        CN_AQI_LEVELS.add(new AqiLevel("严重污染", "儿童、老年人和病人应停留在室内，避免体力消耗，一般人群避免户外活动。", 350,
                500.0, 400, 500));
    }

    private String name;

    private String desc;

    private double minConcentration;

    private double maxConcentration;

    private double minIndex;

    private double maxIndex;

    public AqiLevel(String name, String desc, double minConcentration, double maxConcentration, double minIndex,
                    double maxIndex) {
        this.name = name;
        this.desc = desc;
        this.maxConcentration = maxConcentration;
        this.minIndex = minIndex;
        this.maxIndex = maxIndex;
        this.minConcentration = minConcentration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getMaxConcentration() {
        return maxConcentration;
    }

    public void setMaxConcentration(double maxConcentration) {
        this.maxConcentration = maxConcentration;
    }

    public double getMinIndex() {
        return minIndex;
    }

    public void setMinIndex(double minIndex) {
        this.minIndex = minIndex;
    }

    public double getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(double maxIndex) {
        this.maxIndex = maxIndex;
    }

    public double getMinConcentration() {
        return minConcentration;
    }

    public void setMinConcentration(double minConcentration) {
        this.minConcentration = minConcentration;
    }
}
