/**
 * @(#)${FILE_NAME}.java, 6/16/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.message.handler;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.crm.data.UserConfigType;
import com.jinyufeili.minas.crm.storage.UserConfigStorage;
import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.service.DataPointService;
import com.jinyufeili.minas.wechat.data.AqiLevel;
import com.jinyufeili.minas.wechat.util.AqiUtils;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author pw
 */
@Service
public class WeatherMessageHandler extends AbstractTextResponseMessageHandler {

    public static final int ALLOWED_LAG = 10 * 60 * 1000;

    private static final String[] HOME_USERS = {"obQ_WwJFoQCEw7RqINOxd7Y_59zI", "obQ_WwBgW__vZwHvHXF_CdW5sIEM"};

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataPointService dataPointService;

    @Autowired
    private UserConfigStorage userConfigStorage;

    @Autowired
    private UserService userService;

    @Override
    protected String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context) {
        String fromUserName = message.getFromUser();

        DataPoint temperature = dataPointService.getLatestDataPoint(DataPointType.TEMPERATURE);
        DataPoint humidity = dataPointService.getLatestDataPoint(DataPointType.HUMIDITY);
        DataPoint pm25 = dataPointService.getLatestDataPoint(DataPointType.PM25);
        DataPoint pressure = dataPointService.getLatestDataPoint(DataPointType.PRESSURE);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年M月d日 H点mm分");
        List<String> messages = new ArrayList<>();

        messages.add(String.format("%s", formatter.format(new Date(temperature.getTimestamp()))));
        messages.add(String.format("温湿度: %.1f℃ / %.1f%%", temperature.getValue(), humidity.getValue()));

        if (ArrayUtils.indexOf(HOME_USERS, fromUserName) != -1) {
            messages.add(String.format("气压: %.0fhPa", pressure.getValue() / 100.0));
        }

        if (System.currentTimeMillis() - pm25.getTimestamp() < ALLOWED_LAG) {
            double value = pm25.getValue();
            messages.add(String.format("PM2.5: %.0fug/m^3", value));

            AqiLevel usAqi = AqiUtils.getAqi(AqiLevel.US_AQI_LEVELS, value);
            if (usAqi == null) {
                messages.add("空气污染指数(美标): 超限");
            } else {
                messages.add(
                        String.format("空气指数(美标): %.0f - %s", AqiUtils.calcAqiValue(usAqi, value), usAqi.getName()));
            }

            AqiLevel cnAqi = AqiUtils.getAqi(AqiLevel.CN_AQI_LEVELS, value);
            if (usAqi == null) {
                messages.add("空气污染指数(国标): 超限");
            } else {
                messages.add(
                        String.format("空气指数(国标): %.0f - %s", AqiUtils.calcAqiValue(cnAqi, value), cnAqi.getName()));
                messages.add(cnAqi.getDesc());
            }
        }

        if (ArrayUtils.indexOf(HOME_USERS, fromUserName) != -1) {
            DataPoint temperatureHome = dataPointService.getLatestDataPoint(DataPointType.TEMPERATURE_HOME);
            DataPoint humidityHome = dataPointService.getLatestDataPoint(DataPointType.HUMIDITY_HOME);
            DataPoint pm25Home = dataPointService.getLatestDataPoint(DataPointType.PM25_HOME);
            DataPoint formaldehydeHome = dataPointService.getLatestDataPoint(DataPointType.FORMALDEHYDE_HOME);
            DataPoint co2Home = dataPointService.getLatestDataPoint(DataPointType.CO2_HOME);

            messages.add("\n家里");
            messages.add(String.format("温湿度: %.1f℃ / %.1f%%", temperatureHome.getValue(), humidityHome.getValue()));

            if (System.currentTimeMillis() - pm25Home.getTimestamp() < ALLOWED_LAG) {
                messages.add(String.format("PM2.5: %.0fug/m^3", pm25Home.getValue()));
            }

            if (System.currentTimeMillis() - formaldehydeHome.getTimestamp() < ALLOWED_LAG) {
                messages.add(String.format("甲醛: %.3fmg/m^3", formaldehydeHome.getValue() / 1000.0));
            }

            if (System.currentTimeMillis() - co2Home.getTimestamp() < ALLOWED_LAG) {
                messages.add(String.format("CO2: %.0f ppm", co2Home.getValue()));
            }
        }

        try {
            User user = userService.getByOpenId(fromUserName);
            Optional<String> config = userConfigStorage.get(user.getId(), UserConfigType.PM25_NOTIFICATION);
            if (config.isPresent() && config.get().equals("1")) {
                messages.add("\n已开启空气变化提醒。发送『关闭空气提醒』可以关闭该功能。");
            } else {
                messages.add("\n发送『打开空气提醒』可以打开空气提醒功能。空气变化早知道，及时关窗更健康。");
            }
            LOG.info("return pm25 message for user, userId={}, userName={}", user.getId(), user.getName());
        } catch (EmptyResultDataAccessException e) {
            LOG.info("guest can't use air notification. wechatOpenId={}", fromUserName);
        }

        return String.join("\n", messages);
    }

}
