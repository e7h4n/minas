/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.cron;

import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.data.StatisticsType;
import com.jinyufeili.minas.sensor.service.DataPointService;
import com.jinyufeili.minas.wechat.data.AirStatus;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMenuService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 * @author pw
 */
@Component
@ConditionalOnProperty(value = "cron.weatherMenuUpdate", matchIfMissing = true)
public class WechatMenuUpdateJob {

    public static final int ALLOWED_LAG = 10 * 60 * 1000;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataPointService dataPointService;

    @Autowired
    private WxMpService wechatService;

    @Autowired
    private WxMpMenuService wxMpMenuService;

    @Scheduled(cron = "59 */4 * * * *")
    public void update() throws WxErrorException {
        DataPoint dataPoint = dataPointService.query(DataPointType.PM25, StatisticsType.MOMENTARY, 1).get(0);
        if (System.currentTimeMillis() - dataPoint.getTimestamp() > ALLOWED_LAG) {
            LOG.info("latest data point timestamp = {}", dataPoint.getTimestamp());
            return;
        }

        List<DataPoint> dataPoints = dataPointService.query(DataPointType.PM25, StatisticsType.MOMENTARY, 5);
        OptionalDouble optionalDouble = dataPoints.stream().mapToDouble(DataPoint::getValue).average();
        if (!optionalDouble.isPresent()) {
            updateAirIcon(AirStatus.NONE);
            return;
        }

        updateAirIcon(optionalDouble.getAsDouble() > 40 ? AirStatus.BAD : AirStatus.GOOD);
    }

    private void updateAirIcon(AirStatus airStatus) throws WxErrorException {
        List<WxMenuButton> buttons = new ArrayList<>();

        WxMenuButton airButton = new WxMenuButton();
        String buttonName = "PM2.5";
        if (airStatus == AirStatus.GOOD) {
            buttonName += " \uD83D\uDE00";
        } else if (airStatus == AirStatus.BAD) {
            buttonName += " \uD83D\uDE37";
        }
        airButton.setName(buttonName);
        airButton.setType(WxConsts.BUTTON_CLICK);
        airButton.setKey("WEATHER");
        buttons.add(airButton);

        WxMenuButton homeButton = new WxMenuButton();
        homeButton.setType(WxConsts.BUTTON_VIEW);
        homeButton.setUrl("https://m.jinyufeili.com/");
        homeButton.setName("我的社区");
        buttons.add(homeButton);

        WxMenuButton infoButton = new WxMenuButton();
        infoButton.setName("生活信息");
        buttons.add(infoButton);

        List<WxMenuButton> infoSubButtons = new ArrayList<>();
        infoButton.setSubButtons(infoSubButtons);

        WxMenuButton kdButton = new WxMenuButton();
        kdButton.setName("快递电话 \uD83D\uDE9A");
        kdButton.setType(WxConsts.BUTTON_CLICK);
        kdButton.setKey("INFO_KD");
        infoSubButtons.add(kdButton);

        WxMenuButton bbButton = new WxMenuButton();
        bbButton.setName("宝宝办证 \uD83D\uDC76");
        bbButton.setType(WxConsts.BUTTON_CLICK);
        bbButton.setKey("MEDIA_cQjH8feoqx4PKadkwOl39ICypLofVp45swILpLXxxmQ");
        infoSubButtons.add(bbButton);

        WxMenuButton yyButton = new WxMenuButton();
        yyButton.setName("医院信息 \uD83C\uDFE5");
        yyButton.setType(WxConsts.BUTTON_CLICK);
        yyButton.setKey("MEDIA_cQjH8feoqx4PKadkwOl39Hnh5I9RbVtPXTp6z3Y0J64");
        infoSubButtons.add(yyButton);

        WxMenuButton dhButton = new WxMenuButton();
        dhButton.setName("常用电话 \uD83D\uDCDE");
        dhButton.setType(WxConsts.BUTTON_CLICK);
        dhButton.setKey("MEDIA_cQjH8feoqx4PKadkwOl39IrMX7HhuSc9J17Imd76tNU");
        infoSubButtons.add(dhButton);

        WxMenu wxMenu = new WxMenu();
        wxMenu.setButtons(buttons);

        wechatService.getMenuService().menuCreate(wxMenu);
        LOG.info("wechat menu updated, status = {}", airStatus);
    }
}
