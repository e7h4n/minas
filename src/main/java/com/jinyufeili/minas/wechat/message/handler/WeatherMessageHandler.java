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

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final String[] HOME_USERS = {"obQ_WwJFoQCEw7RqINOxd7Y_59zI", "obQ_WwBgW__vZwHvHXF_CdW5sIEM"};

    @Autowired
    private DataPointService dataPointService;

    @Autowired
    private UserConfigStorage userConfigStorage;

    @Autowired
    private UserService userService;

    @Override
    protected String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context) {
        DataPoint temperature = dataPointService.query(DataPointType.TEMPERATURE, 1).get(0);
        DataPoint humidity = dataPointService.query(DataPointType.HUMIDITY, 1).get(0);
        DataPoint pm25 = dataPointService.query(DataPointType.PM25, 1).get(0);
        DataPoint pressure = dataPointService.query(DataPointType.PRESSURE, 1).get(0);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年M月d日 H点mm分");
        List<String> messages = new ArrayList<>();

        messages.add(String.format("%s", formatter.format(new Date(temperature.getTimestamp()))));
        messages.add(String.format("温湿度: %.1f℃ / %.1f%%", temperature.getValue(), humidity.getValue()));

        if (ArrayUtils.indexOf(HOME_USERS, message.getFromUserName()) != -1) {
            messages.add(String.format("气压: %.0fhPa", pressure.getValue() / 100.0));
        }

        if (System.currentTimeMillis() - pm25.getTimestamp() < ALLOWED_LAG) {
            double average =
                    dataPointService.query(DataPointType.PM25, 10).stream().mapToDouble(DataPoint::getValue).average()
                            .getAsDouble();

            messages.add(String.format("PM2.5: %.0fug/m^3", average));

            AqiLevel usAqi = AqiUtils.getAqi(AqiLevel.US_AQI_LEVELS, average);
            if (usAqi == null) {
                messages.add("空气污染指数(美标): 超限");
            } else {
                messages.add(String.format("空气指数(美标): %.0f - %s", AqiUtils.calcAqiValue(usAqi, average),
                        usAqi.getName()));
            }

            AqiLevel cnAqi = AqiUtils.getAqi(AqiLevel.CN_AQI_LEVELS, average);
            if (usAqi == null) {
                messages.add("空气污染指数(国标): 超限");
            } else {
                messages.add(String.format("空气指数(国标): %.0f - %s", AqiUtils.calcAqiValue(cnAqi, average),
                        cnAqi.getName()));
                messages.add(cnAqi.getDesc());
            }
        }

        if (ArrayUtils.indexOf(HOME_USERS, message.getFromUserName()) != -1) {
            DataPoint temperatureHome = dataPointService.query(DataPointType.TEMPERATURE_HOME, 1).get(0);
            DataPoint humidityHome = dataPointService.query(DataPointType.HUMIDITY_HOME, 1).get(0);
            DataPoint pm25Home = dataPointService.query(DataPointType.PM25_HOME, 1).get(0);

            messages.add("\n家里");
            messages.add(String.format("温湿度: %.1f℃ / %.1f%%", temperatureHome.getValue(), humidityHome.getValue()));
            if (System.currentTimeMillis() - pm25Home.getTimestamp() < ALLOWED_LAG) {
                messages.add(String.format("PM2.5: %.0fug/m^3", pm25Home.getValue()));
            }
        }

        try {
            User user = userService.getByOpenId(message.getFromUserName());
            Optional<String> config = userConfigStorage.get(user.getId(), UserConfigType.PM25_NOTIFICATION);
            if (config.isPresent() && config.get().equals("1")) {
                messages.add("\n已开启空气变化提醒。发送『关闭空气提醒』可以关闭该功能。");
            } else {
                messages.add("\n发送『打开空气提醒』可以打开空气提醒功能。空气变化早知道，及时关窗更健康。");
            }
        } catch (EmptyResultDataAccessException e) {
            LOG.info("guest can't use air notification. wechatOpenId={}", message.getFromUserName());
        }

        return String.join("\n", messages);
    }
}
