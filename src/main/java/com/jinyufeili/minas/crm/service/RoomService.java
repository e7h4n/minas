package com.jinyufeili.minas.crm.service;

import com.jinyufeili.minas.crm.data.Room;
import com.jinyufeili.minas.crm.storage.RoomStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pw on 6/11/16.
 */
@Service
public class RoomService {

    @Autowired
    private RoomStorage roomStorage;

    public Room getByAbbr(String content) {
        int[] strArray = Arrays.stream(content.split("")).mapToInt(Integer::valueOf).toArray();
        int region = strArray[0];
        int building = strArray[1];
        int unit;
        int houseNumber;
        if (region == 1) {
            unit = 1;
            houseNumber = Integer.valueOf(content.substring(2));
        } else {
            unit = strArray[2];
            houseNumber = Integer.valueOf(content.substring(3));
        }
        return roomStorage.getByHouseNumber(region, building, unit, houseNumber);
    }

    public Map<Integer, Room> getByIds(Set<Integer> roomIds) {
        return roomStorage.getByIds(roomIds);
    }

    public Room getByLocation(int region, int building, int unit, int houseNumber) {
        return roomStorage.getByLocation(region, building, unit, houseNumber);
    }
}
