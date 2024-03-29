package com.jinyufeili.minas.crm.storage;

import com.jinyufeili.minas.crm.data.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by pw on 6/11/16.
 */
@Repository
public class RoomStorage {

    private static final RowMapper<Room> ROW_MAPPER = ((rs, rowNum) -> {
        Room room = new Room();

        room.setId(rs.getInt("id"));
        room.setRegion(rs.getInt("region"));
        room.setBuilding(rs.getInt("building"));
        room.setUnit(rs.getInt("unit"));
        room.setHouseNumber(rs.getInt("house_number"));
        room.setRoomArea(rs.getFloat("room_area"));
        room.setParkingSpaceArea(rs.getFloat("parking_space_area"));
        return room;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public Room getByHouseNumber(int region, int building, int unit, int houseNumber) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("region", region);
        parameterSource.addValue("building", building);
        parameterSource.addValue("unit", unit);
        parameterSource.addValue("houseNumber", houseNumber);
        return db.queryForObject("SELECT * FROM crm_room WHERE" +
                " region = :region" +
                " AND building = :building" +
                " AND unit = :unit" +
                " AND house_number = :houseNumber", parameterSource, ROW_MAPPER);
    }

    public Map<Integer, Room> getByIds(Set<Integer> roomIds) {
        if (CollectionUtils.isEmpty(roomIds)) {
            return Collections.emptyMap();
        }

        return db.query("SELECT * FROM crm_room WHERE id IN (:ids)", Collections.singletonMap("ids", roomIds),
                ROW_MAPPER).stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }

    public Room getByLocation(int region, int building, int unit, int houseNumber) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("region", region);
        source.addValue("building", building);
        source.addValue("unit", unit);
        source.addValue("houseNumber", houseNumber);

        return db.queryForObject("select * from crm_room" +
                " where region = :region" +
                " and building = :building" +
                " and unit = :unit" +
                " and house_number = :houseNumber", source, ROW_MAPPER);
    }
}
