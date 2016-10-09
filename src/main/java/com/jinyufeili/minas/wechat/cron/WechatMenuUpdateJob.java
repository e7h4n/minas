/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.cron;

import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.service.DataPointService;
import com.jinyufeili.minas.wechat.data.AirStatus;
import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.OptionalDouble;

/**
 * @author pw
 */
@Component
@ConditionalOnProperty(value = "cron.weatherMenuUpdate", matchIfMissing = true)
public class WechatMenuUpdateJob {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public static final int ALLOWED_LAG = 10 * 60 * 1000;

    @Autowired
    private DataPointService dataPointService;

    @Autowired
    private WxMpService wechatService;

    @Scheduled(cron = "59 */4 * * * *")
    public void update() throws WxErrorException {
        DataPoint dataPoint = dataPointService.query(DataPointType.PM25, 1).get(0);
        if (System.currentTimeMillis() - dataPoint.getTimestamp() > ALLOWED_LAG) {
            LOG.info("latest data point timestamp = {}", dataPoint.getTimestamp());
            return;
        }

        List<DataPoint> dataPoints = dataPointService.query(DataPointType.PM25, 5);
        OptionalDouble optionalDouble = dataPoints.stream().mapToDouble(DataPoint::getValue).average();
        if (!optionalDouble.isPresent()) {
            updateAirIcon(AirStatus.NONE);
            return;
        }

        updateAirIcon(optionalDouble.getAsDouble() > 40 ? AirStatus.BAD : AirStatus.GOOD);
    }

    private void updateAirIcon(AirStatus airStatus) throws WxErrorException {
        WxMenu wxMenu = wechatService.menuGet();
        for (WxMenu.WxMenuButton button : wxMenu.getButtons()) {
            if (button.getName().indexOf("PM2.5") != -1) {
                String buttonName= "PM2.5";
                if (airStatus == AirStatus.GOOD) {
                    buttonName += " \uD83D\uDE00";
                } else if (airStatus == AirStatus.BAD) {
                    buttonName += " \uD83D\uDE37";
                }

                button.setName(buttonName);
            }
        }

        wechatService.menuCreate(wxMenu);
        LOG.info("wechat menu updated, status = {}", airStatus);
    }
}
