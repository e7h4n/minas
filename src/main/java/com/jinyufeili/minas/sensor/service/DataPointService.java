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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author pw
 */
@Service
public class DataPointService {

    @Autowired
    private DataPointStorage dataPointStorage;

    public int create(DataPoint dataPoint) {
        return dataPointStorage.create(dataPoint);
    }

    public List<DataPoint> query(DataPointType type, int limit) {
        return dataPointStorage.query(type, limit);
    }

    public DataPoint get(int id) {
        return dataPointStorage.get(id);
    }
}
