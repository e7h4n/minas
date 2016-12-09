package com.jinyufeili.minas.sensor.cron;

import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.data.StatisticsType;
import com.jinyufeili.minas.sensor.service.DataPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.TimeUnit;

/**
 * Created by pw on 03/12/2016.
 */
@Component
@ConditionalOnProperty(value = "cron.sensorDataStatistics", matchIfMissing = true)
public class SensorDataStatisticsJob {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataPointService dataPointService;

    private final static DataPointType[] AVG_DATAPOINT_TYPES = {
        DataPointType.PM25,
        DataPointType.PM25_HOME,
        DataPointType.PM25_OFFICIAL,
        DataPointType.CO2_HOME
    };

    @Scheduled(cron = "0 * * * * *")
    public void run() {
        Calendar startTimeCalendar = Calendar.getInstance();
        startTimeCalendar.set(Calendar.MINUTE, 0);
        startTimeCalendar.set(Calendar.SECOND, 0);
        startTimeCalendar.set(Calendar.MILLISECOND, 0);
        long startTime = startTimeCalendar.getTimeInMillis();
        long endTime = startTime + TimeUnit.HOURS.toMillis(1);

        for (DataPointType type : AVG_DATAPOINT_TYPES) {
            List<DataPoint> dataPoints = dataPointService.query(type, StatisticsType.MOMENTARY, startTime, endTime);
            OptionalDouble avgOpt = dataPoints.stream().mapToDouble(DataPoint::getValue).average();
            if (!avgOpt.isPresent()) {
                LOG.warn("can't get average data for type {} ", type);
                continue;
            }

            DataPoint dataPoint = new DataPoint();
            dataPoint.setStatisticsType(StatisticsType.HOURLY);
            dataPoint.setTimestamp(startTime);
            dataPoint.setType(type);
            dataPoint.setValue(avgOpt.getAsDouble());
            dataPointService.create(dataPoint);

            LOG.info("create average data, type={}, value={}", type, avgOpt.getAsDouble());
        }
    }
}
