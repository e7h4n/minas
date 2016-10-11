/**
 * @(#)${FILE_NAME}.java, 6/16/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.sensor.service;

import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.storage.DataPointStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author pw
 */
@Service
public class DataPointService {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataPointStorage dataPointStorage;

    private Map<DataPointType, DataPoint> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initCache() {
        for (DataPointType dataPointType : DataPointType.values()) {
            LOG.info("init cache for {}", dataPointType);
            getLatestDataPoint(dataPointType);
        }
    }

    public int create(DataPoint dataPoint) {
        int id = dataPointStorage.create(dataPoint);
        cache.put(dataPoint.getType(), dataPoint);
        return id;
    }

    public List<DataPoint> query(DataPointType type, int limit) {
        return dataPointStorage.query(type, limit);
    }

    public DataPoint get(int id) {
        return dataPointStorage.get(id);
    }

    public Optional<DataPoint> getLatestDataPoint(DataPointType dataPointType) {
        if (!cache.containsKey(dataPointType)) {
            List<DataPoint> dataPoints = query(dataPointType, 1);
            if (CollectionUtils.isEmpty(dataPoints)) {
                return Optional.empty();
            }
            DataPoint dataPoint = dataPoints.get(0);
            cache.put(dataPointType, dataPoint);
        }

        return Optional.of(cache.get(dataPointType));
    }
}
