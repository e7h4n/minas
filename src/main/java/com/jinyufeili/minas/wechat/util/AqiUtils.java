/**
 * @(#)${FILE_NAME}.java, 6/16/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.util;

import com.jinyufeili.minas.wechat.data.AqiLevel;

import java.util.List;

/**
 * @author pw
 */
public class AqiUtils {

    public static AqiLevel getAqi(List<AqiLevel> levels, double value) {
        for (AqiLevel level : levels) {
            if (level.getMinConcentration() < value && level.getMaxConcentration() >= value) {
                return level;
            }
        }

        return null;
    }

    public static double calcAqiValue(AqiLevel level, double value) {
        return (level.getMaxIndex() - level.getMinIndex()) /
                (level.getMaxConcentration() - level.getMinConcentration()) * (value - level.getMinConcentration()) +
                level.getMinIndex();
    }
}