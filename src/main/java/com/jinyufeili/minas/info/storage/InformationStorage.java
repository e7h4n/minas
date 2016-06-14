package com.jinyufeili.minas.info.storage;

import com.jinyufeili.minas.info.data.Information;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;

/**
 * Created by pw on 6/14/16.
 */
@Repository
public class InformationStorage {

    private static final RowMapper<Information> ROW_MAPPER = ((rs, rowNum) -> {
        Information information = new Information();

        information.setId(rs.getInt("id"));
        information.setContent(rs.getString("content"));
        information.setEventKey(rs.getString("event_key"));
        
        return information;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public Information getByKey(String eventKey) {
        return db.queryForObject("SELECT * FROM info_information WHERE event_key = :eventKey",
                Collections.singletonMap("eventKey", eventKey), ROW_MAPPER);
    }
}
