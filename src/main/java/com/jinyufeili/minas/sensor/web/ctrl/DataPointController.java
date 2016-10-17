/**
 * @(#)${FILE_NAME}.java, 6/16/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.sensor.web.ctrl;

import com.jinyufeili.minas.account.web.data.UserVO;
import com.jinyufeili.minas.account.web.logic.UserLogic;
import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.web.logic.DataPointLogic;
import com.jinyufeili.minas.web.exception.BadRequestException;
import com.jinyufeili.minas.web.exception.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author pw
 */
@RestController
public class DataPointController {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Value("${dataPointValidToken}")
    private String dataPointValidToken;

    @Autowired
    private DataPointLogic dataPointLogic;

    @Autowired
    private UserLogic userLogic;

    @RequestMapping(value = "/api/sensor/datas", method = RequestMethod.POST)
    public List<DataPoint> create(@RequestBody Map<String, Object> dataMap, @RequestParam String token) {
        if (!dataPointValidToken.equals(token)) {
            LOG.warn("invalid token, token={}", token);
            throw new BadRequestException("invalid token");
        }

        return dataPointLogic.createByDataMap(dataMap);
    }

    @RequestMapping("/api/sensor/data-points/{type}")
    public List<DataPoint> query(@PathVariable("type") String strType, @RequestParam(defaultValue = "10") int limit,
                                 Authentication authentication) {

        if (strType.contains("_HOME")) {
            UserVO userVO = userLogic.getByAuthentication(authentication);
            if (userVO.getRoom() == null || userVO.getRoom().getRegion() != 2 || userVO.getRoom().getBuilding() != 1 ||
                    userVO.getRoom().getUnit() != 2 || userVO.getRoom().getHouseNumber() != 601) {
                throw new ForbiddenException();
            }
        }

        DataPointType type = DataPointType.valueOf(strType);
        return dataPointLogic.query(type, limit);
    }
}