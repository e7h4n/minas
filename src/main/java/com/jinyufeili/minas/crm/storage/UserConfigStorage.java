/**
 * @(#)${FILE_NAME}.java, 22/09/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.crm.storage;

import com.jinyufeili.minas.crm.data.UserConfigType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author pw
 */
@Repository
public class UserConfigStorage {

    public static final long now = System.currentTimeMillis();

    @Autowired
    private NamedParameterJdbcOperations db;

    public List<Integer> queryAllNot(UserConfigType configType, String value) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("type", configType.toInt());
        source.addValue("value", value);

        return db.queryForList("select userId from crm_user_config where type = :type and value != :value", source,
                Integer.class);
    }

    public Optional<String> get(int userId, UserConfigType type) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("userId", userId);
        source.addValue("type", type.toInt());

        try {
            String value = db.queryForObject("select value from crm_user_config where userId = :userId and type = :type",
                    source, String.class);
            return Optional.of(value);
        }catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean set(int userId, UserConfigType type, String value) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("userId", userId);
        source.addValue("value", value);
        source.addValue("type", type.toInt());
        source.addValue("createdTime", now);
        source.addValue("updatedTime", now);

        return db.update("insert into crm_user_config" + 
                " set userId = :userId" +
                ", type = :type" +
                ", value = :value" +
                ", createdTime = :createdTime" +
                ", updatedTime = :updatedTime" +
                " on duplicate key" +
                " update value = :value" +
                ", updatedTime = :updatedTime", source) > 0;
    }
}
