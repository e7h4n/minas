package com.jinyufeili.minas.crm.storage;

import com.jinyufeili.minas.crm.data.Resident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return db
                .query("SELECT * FROM crm_resident WHERE room_id = :roomId", Collections.singletonMap("roomId", roomId),
                        ROW_MAPPER);
    }

    public List<Resident> queryByMobilePhone(String mobilePhone) {
        return db.query("SELECT * FROM crm_resident WHERE mobile_phone = :mobilePhone",
                Collections.singletonMap("mobilePhone", mobilePhone), ROW_MAPPER);
    }

    public List<Resident> queryByName(String name) {
        return db.query("SELECT * FROM crm_resident WHERE name = :name", Collections.singletonMap("name", name),
                ROW_MAPPER);
    }

    public Map<Integer, Resident> queryByUserIds(Set<Integer> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }

        return db.query("SELECT * FROM crm_resident WHERE wechat_user_id IN (:ids)",
                Collections.singletonMap("ids", userIds), ROW_MAPPER).stream()
                .collect(Collectors.toMap(Resident::getUserId, Function.identity()));
    }

    public List<Resident> queryByRoom(int region, int building, int unit, int houseNumber) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("region", region);
        source.addValue("building", building);
        source.addValue("unit", unit);
        source.addValue("houseNumber", houseNumber);

        return db.query("select r.* from crm_resident r" +
                " join crm_room room on room.id = r.room_id" +
                " where room.region = :region" +
                " and room.building = :building" +
                " and room.unit = :unit" +
                " and room.house_number = :houseNumber", source, ROW_MAPPER);
    }

    public boolean update(Resident resident) {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(resident);
        return db.update("update crm_resident" +
                " set name = :name" +
                ", mobile_phone = :mobilePhone" +
                ", wechat_user_id = :userId" +
                " where id = :id", source) > 0;
    }

    public Resident get(int residentId) {
        return db
                .queryForObject("select * from crm_resident where id = :id", Collections.singletonMap("id", residentId),
                        ROW_MAPPER);
    }

    public boolean create(Resident resident) {
        if (resident.getMobilePhone() == null) {
            resident.setMobilePhone("");
        }

        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(resident);

        return db.update("insert into crm_resident" +
                " set id = :id" +
                ", name = :name" +
                ", mobile_phone = :mobilePhone" +
                ", telephone = ''" +
                ", wechatOpenId = ''" +
                ", sex = 0" +
                ", address = ''" +
                ", room_id = :roomId" +
                ", wechat_user_id = :userId", source) > 0;
    }
}
