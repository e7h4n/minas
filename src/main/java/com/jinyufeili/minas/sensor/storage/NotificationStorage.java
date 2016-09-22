/**
 * @(#)${FILE_NAME}.java, 22/09/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.sensor.storage;

import com.jinyufeili.minas.sensor.data.Notification;
import com.jinyufeili.minas.sensor.data.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;

/**
 * @author pw
 */
@Repository
public class NotificationStorage {

    private static final RowMapper<Notification> ROW_MAPPER = ((rs, rowNum) -> {
        Notification notification = new Notification();

        notification.setId(rs.getInt("id"));
        notification.setType(NotificationType.findByInt(rs.getInt("type")));
        notification.setFlag(rs.getBoolean("flag"));

        return notification;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public int add(Notification notification) {
        MapSqlParameterSource source = new MapSqlParameterSource();

        source.addValue("type", notification.getType().toInt());
        source.addValue("flag", notification.getFlag());
        source.addValue("createdTime", System.currentTimeMillis());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        db.update("INSERT INTO sensor_notification" + " SET type = :type" + ", flag = :flag" +
                ", createdTime = :createdTime", source, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public Optional<Notification> getLatestByType(NotificationType type) {
        try {
            Notification notification = db.queryForObject(
                    "SELECT * FROM sensor_notification WHERE type = :type ORDER BY createdTime DESC LIMIT 1",
                    Collections.singletonMap("type", type.toInt()), ROW_MAPPER);
            return Optional.of(notification);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
