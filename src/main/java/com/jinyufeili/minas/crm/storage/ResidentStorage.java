package com.jinyufeili.minas.crm.storage;

import com.jinyufeili.minas.crm.data.Resident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by pw on 6/11/16.
 */
@Repository
public class ResidentStorage {

    private static final RowMapper<Resident> ROW_MAPPER = ((rs, rowNum) -> {
        Resident resident = new Resident();

        resident.setId(rs.getInt("id"));
        resident.setName(rs.getString("name"));
        resident.setMobilePhone(rs.getString("mobile_phone"));
        resident.setRoomId(rs.getInt("room_id"));
        resident.setUserId(rs.getInt("wechat_user_id"));

        return resident;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public List<Resident> queryByRoomId(int roomId) {
        return db.query("SELECT * FROM crm_resident WHERE room_id = :roomId",
                Collections.singletonMap("roomId", roomId), ROW_MAPPER);
    }

    public List<Resident> queryByMobilePhone(String mobilePhone) {
        return db.query("SELECT * FROM crm_resident WHERE mobile_phone = :mobilePhone",
                Collections.singletonMap("mobilePhone", mobilePhone), ROW_MAPPER);
    }

    public List<Resident> queryByName(String name) {
        return db.query("SELECT * FROM crm_resident WHERE name = :name", Collections.singletonMap("name", name),
                ROW_MAPPER);
    }

    public List<Resident> queryByUserIds(Set<Integer> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }

        return db.query("SELECT * FROM crm_resident WHERE wechat_user_id IN (:ids)",
                Collections.singletonMap("ids", userIds), ROW_MAPPER);
    }
}
