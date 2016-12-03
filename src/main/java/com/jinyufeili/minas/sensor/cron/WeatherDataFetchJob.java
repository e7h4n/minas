/**
 * @(#)${FILE_NAME}.java, 11/10/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.sensor.cron;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.service.DataPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author pw
 */
@Component
@ConditionalOnProperty(value = "cron.weatherDataFetch", matchIfMissing = true)
public class WeatherDataFetchJob {

    public static final String API_URL =
            "http://www.pm25.in/api/querys/aqis_by_station.json?token=5j1znBVAsnSf5xQyNQyq&station_code=1011A";

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataPointService dataPointService;

    @Scheduled(cron = "0 */10 * * * *")
    public void fetchData() {
        HttpRequest httpRequest = HttpRequest.get(API_URL);
        if (httpRequest.code() != 200) {
            return;
        }

        String responseBody = httpRequest.body();

        List<Map<String, Object>> dataPoints;
        try {
            dataPoints = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (CollectionUtils.isEmpty(dataPoints)) {
            return;
        }

        Map<String, Object> remoteDataPoint = dataPoints.get(0);
        int pm25 = (Integer) remoteDataPoint.get("pm2_5");
        String timePoint = (String) remoteDataPoint.get("time_point");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date;
        try {
            date = sdf.parse(timePoint);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        DataPoint dataPoint = new DataPoint();
        dataPoint.setValue(pm25);
        dataPoint.setTimestamp(date.getTime());
        dataPoint.setType(DataPointType.PM25_OFFICIAL);
        dataPointService.createMomentary(dataPoint);

        LOG.info("createMomentary pm25 offical data, value={}, time={}", pm25, date);
    }
}
