/**
 * @(#)${FILE_NAME}.java, 22/09/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.crm.storage;

import com.jinyufeili.minas.crm.data.UserConfigType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author pw
 */
@Repository
public class UserConfigStorage {

    @Autowired
    private NamedParameterJdbcOperations db;

    public List<Integer> queryAllNot(UserConfigType configType, String value) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("type", configType.toInt());
        source.addValue("value", value);

        return db.queryForList("select userId from crm_user_config where type = :type and value != :value", source,
                Integer.class);
    }
}
