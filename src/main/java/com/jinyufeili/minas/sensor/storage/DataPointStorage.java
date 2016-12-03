/**
 * @(#)${FILE_NAME}.java, 6/16/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.sensor.storage;

import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.data.StatisticsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final RowMapper<DataPoint> ROW_MAPPER = ((rs, rowNum) -> {
        DataPoint dataPoint = new DataPoint();

        dataPoint.setId(rs.getInt("id"));
        dataPoint.setValue(rs.getDouble("value"));
        dataPoint.setTimestamp(rs.getTimestamp("time").getTime());
        dataPoint.setType(DataPointType.valueOf(rs.getString("type")));
        dataPoint.setStatisticsType(StatisticsType.valueOf(rs.getString("statisticsType")));

        return dataPoint;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public int create(DataPoint dataPoint) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("value", dataPoint.getValue());
        source.addValue("time", new Date(dataPoint.getTimestamp()));
        source.addValue("type", dataPoint.getType().toString());
        source.addValue("statisticsType", dataPoint.getStatisticsType().toString());

        KeyHolder kh = new GeneratedKeyHolder();
        db.update("INSERT INTO sensor_datapoint" +
                " SET value = :value" +
                ", time = :time" +
                ", type = :type" +
                ", statisticsType = :statisticsType" +
                " ON DUPLICATE KEY UPDATE value = :value", source, kh);

        // when ON DUPLICATE KEY UPDATE update a row, there will be two generated key to cause kh.getKey() error
        if (kh.getKeyList().size() <= 1 && kh.getKey() != null) {
            return kh.getKey().intValue();
        }

        return db.queryForObject("SELECT id FROM sensor_datapoint" +
                        " WHERE type = :type" +
                        " AND time = :time" +
                        " AND statisticsType = :statisticsType", source,
                Integer.class);
    }

    public List<DataPoint> query(DataPointType type, StatisticsType statisticsType, int limit) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("type", type.toString());
        source.addValue("limit", limit);
        source.addValue("statisticsType", statisticsType.toString());

        return db.query("SELECT * FROM sensor_datapoint" +
                " WHERE type = :type" +
                " AND statisticsType = :statisticsType" +
                " ORDER BY time DESC LIMIT :limit", source, ROW_MAPPER);
    }

    public DataPoint get(int id) {
        return db.queryForObject("SELECT * FROM sensor_datapoint WHERE id = :id", Collections.singletonMap("id", id),
                ROW_MAPPER);
    }

    public List<DataPoint> query(DataPointType type, StatisticsType statisticsType, long startTimeInclusive,
                                 long endTimeExclusive) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("type", type.toString());
        params.addValue("statisticsType", statisticsType.toString());
        params.addValue("startTime", new Date(startTimeInclusive));
        params.addValue("endTime", new Date(endTimeExclusive));

        return db.query("SELECT * FROM sensor_datapoint" +
                " WHERE type = :type" +
                " AND statisticsType = :statisticsType" +
                " AND time >= :startTime" +
                " AND time < :endTime", params, ROW_MAPPER);
    }
}
