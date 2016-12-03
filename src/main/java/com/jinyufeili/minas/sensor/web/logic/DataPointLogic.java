/**
 * @(#)${FILE_NAME}.java, 6/16/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.sensor.web.logic;

import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.data.StatisticsType;
import com.jinyufeili.minas.sensor.service.DataPointService;
import com.jinyufeili.minas.web.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@Service
public class DataPointLogic {

    @Autowired
    private DataPointService dataPointService;

    public List<DataPoint> createByDataMap(Map<String, Object> dataMap) {
        long timestamp = (Long) dataMap.get("timestamp");
        dataMap.remove("timestamp");
        List<DataPoint> dataPoints = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            DataPointType type = DataPointType.valueOf(entry.getKey());
            double value = (double) entry.getValue();
            DataPoint dataPoint = new DataPoint();
            dataPoint.setTimestamp(timestamp);
            dataPoint.setType(type);
            dataPoint.setValue(value);
            dataPoints.add(dataPoint);
        }

        return dataPoints.stream().map(dataPointService::createMomentary).map(dataPointService::get)
                .collect(Collectors.toList());
    }

    public List<DataPoint> query(DataPointType type, StatisticsType statisticsType, int limit) {
        return dataPointService.query(type, statisticsType, limit);
    }

    public DataPoint getLatest(DataPointType type) {
        Optional<DataPoint> latestDataPoint = dataPointService.getLatestMomentaryDataPoint(type);
        if (!latestDataPoint.isPresent()) {
            throw new NotFoundException();
        }

        return latestDataPoint.get();
    }

    public List<DataPoint> query(DataPointType type, StatisticsType statisticsType, long startTime, long endTime) {
        return dataPointService.query(type, statisticsType, startTime, endTime);
    }
}
