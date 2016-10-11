/**
 * @(#)${FILE_NAME}.java, 6/16/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.sensor.storage;

import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author pw
 */
@Repository
public class DataPointStorage {

    private static final RowMapper<DataPoint> ROW_MAPPER = ((rs, rowNum) -> {
        DataPoint dataPoint = new DataPoint();

        dataPoint.setId(rs.getInt("id"));
        dataPoint.setValue(rs.getDouble("value"));
        dataPoint.setTimestamp(rs.getTimestamp("time").getTime());
        dataPoint.setType(DataPointType.valueOf(rs.getString("type")));

        return dataPoint;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public int create(DataPoint dataPoint) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("value", dataPoint.getValue());
        source.addValue("time", new Date(dataPoint.getTimestamp()));
        source.addValue("type", dataPoint.getType().toString());

        KeyHolder kh = new GeneratedKeyHolder();
        db.update("insert into sensor_datapoint set value = :value, time = :time, type = :type" +
                " on duplicate key update value = :value", source, kh);

        if (kh.getKey() != null) {
            return kh.getKey().intValue();
        }

        return db.queryForObject("select id from sensor_datapoint where type = :type and time = :time", source,
                Integer.class);
    }

    public List<DataPoint> query(DataPointType type, int limit) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("type", type.toString());
        source.addValue("limit", limit);
        return db.query("select * from sensor_datapoint where type = :type order by time desc limit :limit", source,
                ROW_MAPPER);
    }

    public DataPoint get(int id) {
        return db.queryForObject("select * from sensor_datapoint where id = :id", Collections.singletonMap("id", id),
                ROW_MAPPER);
    }
}
