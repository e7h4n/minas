/**
 * @(#)${FILE_NAME}.java, 22/09/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.cron;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.crm.data.UserConfigType;
import com.jinyufeili.minas.crm.storage.UserConfigStorage;
import com.jinyufeili.minas.sensor.data.*;
import com.jinyufeili.minas.sensor.service.DataPointService;
import com.jinyufeili.minas.sensor.storage.NotificationStorage;
import com.jinyufeili.minas.wechat.data.AqiLevel;
import com.jinyufeili.minas.wechat.util.AqiUtils;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author pw
 */
@Component
@ConditionalOnProperty(value = "cron.weatherNotification", matchIfMissing = true)
public class WeatherNotificationJob {

    private static final long DATA_EXPIRE_TIME = TimeUnit.MINUTES.toMillis(10);

    private static final double THRESHOLD = 40.0;

    @Autowired
    private UserService userService;

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataPointService dataPointService;

    @Autowired
    private NotificationStorage notificationStorage;

    private long MIN_NOTIFICATION_DURATION = TimeUnit.HOURS.toMillis(1);

    private int MIN_HOUR = 8;

    private int MAX_HOUR = 22;

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private UserConfigStorage userConfigStorage;

    private double MIN_FLAG_GAP = 5;

    @Scheduled(cron = "0 */10 * * * *")
    public void update() {

        List<DataPoint> dataPoints = dataPointService.query(DataPointType.PM25, StatisticsType.MOMENTARY, 1);
        if (dataPoints.size() == 0) {
            LOG.info("no data points found");
            return;
        }

        DataPoint latestDataPoint = dataPoints.get(0);
        if (System.currentTimeMillis() - latestDataPoint.getTimestamp() > DATA_EXPIRE_TIME) {
            LOG.info("data expired");
            return;
        }

        Optional<Notification> notificationOpt = notificationStorage.getLatestByType(NotificationType.PM25);
        if (notificationOpt.isPresent() &&
                System.currentTimeMillis() - notificationOpt.get().getCreatedTime() < MIN_NOTIFICATION_DURATION) {
            LOG.info("notification too fast, just wait a moment");
            return;
        }

        List<DataPoint> averagePoints = dataPointService.query(DataPointType.PM25, StatisticsType.MOMENTARY, 30);
        double averageValue = averagePoints.stream().mapToDouble(DataPoint::getValue).average().getAsDouble();
        boolean flag = averageValue > THRESHOLD;
        if (notificationOpt.isPresent()) {
            boolean lastFlag = notificationOpt.get().getFlag();
            if (flag == lastFlag) {
                LOG.info("nothing to notify");
                return;
            }

            if (Math.abs(averageValue - THRESHOLD) < MIN_FLAG_GAP) {
                LOG.info("flag gap is too small");
                return;
            }
        }

        Calendar calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= MIN_HOUR && hour < MAX_HOUR) {
            List<Integer> userIds = userConfigStorage.queryAllNot(UserConfigType.PM25_NOTIFICATION, "-1");

            SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");
            Date now = new Date(latestDataPoint.getTimestamp());
            String time = sdfDate.format(now);
            String advice = flag ? "小区空气有点脏，请注意关窗净化。" : "小区空气很好，可以开窗透气。";
            AqiLevel aqi = AqiUtils.getAqi(AqiLevel.US_AQI_LEVELS, averageValue);

            StringBuilder remarkBuilder = new StringBuilder(
                    String.format("一小时浓度均值：%dug/m^3\n美标评级：%s", Math.round(averageValue), aqi.getName()));

            remarkBuilder.append("\n");
            remarkBuilder.append("\n最近数据:");
            averagePoints.forEach(p -> {
                remarkBuilder.append(
                        String.format("\n%s %.0fug/m^3", sdfDate.format(new Date(p.getTimestamp())), p.getValue()));
            });

            userIds.forEach(id -> {
                User user = userService.get(id);
                user.getOpenId();

                WxMpTemplateMessage message = new WxMpTemplateMessage();
                message.setToUser(user.getOpenId());
                message.setTemplateId("8ttt8oMgKhALmtE349yjEFMHUtJUNGY-XFngPxG6z6s");
                message.getData().add(new WxMpTemplateData("first", advice));
                message.getData().add(new WxMpTemplateData("keyword1", "二区户外"));
                message.getData().add(new WxMpTemplateData("keyword2", time));
                message.getData().add(new WxMpTemplateData("remark", remarkBuilder.toString()));

                try {
                    wxMpService.templateSend(message);
                } catch (WxErrorException e) {
                    LOG.error("", e);
                }
            });

            LOG.info("air notification sent to {} users", userIds.size());
        } else {
            LOG.info("silent when night");
        }

        Notification notification = new Notification();
        notification.setType(NotificationType.PM25);
        notification.setFlag(flag);
        this.notificationStorage.add(notification);
    }
}
