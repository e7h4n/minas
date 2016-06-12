/**
 * @(#)${FILE_NAME}.java, 6/12/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.crm.web.ctrl;

import com.jinyufeili.minas.crm.data.Room;
import com.jinyufeili.minas.crm.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pw
 */
@RestController
public class RoomController {

    @Autowired
    private RoomService roomService;

    @RequestMapping("/api/rooms/by-location")
    @PreAuthorize("hasRole('筹备组')")
    public Room getByLocation(@RequestParam int region, @RequestParam int building, @RequestParam int unit,
                              @RequestParam int houseNumber) {
        return roomService.getByLocation(region, building, unit, houseNumber);
    }
}
