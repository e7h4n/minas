/**
 * @(#)${FILE_NAME}.java, 6/16/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.sensor.service;

import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.data.StatisticsType;
import com.jinyufeili.minas.sensor.storage.DataPointStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author pw
 */
@Service
public class DataPointService {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataPointStorage dataPointStorage;

    @CacheEvict(value = "latestDataPoint", key = "#dataPoint.type")
    public int createMomentary(DataPoint dataPoint) {
        dataPoint.setStatisticsType(StatisticsType.MOMENTARY);
        return create(dataPoint);
    }

    @Cacheable(value = "latestDataPoint")
    public Optional<DataPoint> getLatestMomentaryDataPoint(DataPointType dataPointType) {
        List<DataPoint> dataPoints = query(dataPointType, StatisticsType.MOMENTARY, 1);
        if (CollectionUtils.isEmpty(dataPoints)) {
            return Optional.empty();
        }
        DataPoint dataPoint = dataPoints.get(0);

        return Optional.of(dataPoint);
    }

    public List<DataPoint> query(DataPointType type, StatisticsType statisticsType, int limit) {
        return dataPointStorage.query(type, statisticsType, limit);
    }

    public DataPoint get(int id) {
        return dataPointStorage.get(id);
    }

    public List<DataPoint> query(DataPointType type, StatisticsType statisticsType, long startTimeInclusive,
                                 long endTimeExclusive) {
        return dataPointStorage.query(type, statisticsType, startTimeInclusive, endTimeExclusive);
    }

    public int create(DataPoint dataPoint) {
        return dataPointStorage.create(dataPoint);
    }
}
