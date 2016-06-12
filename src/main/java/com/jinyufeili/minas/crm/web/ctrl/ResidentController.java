/**
 * @(#)${FILE_NAME}.java, 6/12/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.crm.web.ctrl;

import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.service.ResidentService;
import com.jinyufeili.minas.web.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author pw
 */
@RestController
public class ResidentController {

    @Autowired
    private ResidentService residentService;

    @RequestMapping("/api/residents/by-room")
    @PreAuthorize("hasRole('筹备组')")
    public List<Resident> queryByRoom(@RequestParam int region, @RequestParam int building, @RequestParam int unit,
                                      @RequestParam int houseNumber) {
        return residentService.queryByRoom(region, building, unit, houseNumber);
    }

    @RequestMapping(value = "/api/residents/{residentId}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('筹备组')")
    public Resident update(@PathVariable int residentId, @RequestBody Resident resident) {
        if (residentId != resident.getId()) {
            throw new BadRequestException("invalid resident id");
        }

        residentService.update(resident);
        return residentService.get(residentId);
    }
}
