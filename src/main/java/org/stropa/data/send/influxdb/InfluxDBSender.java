package org.stropa.data.send.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Serie;
import org.stropa.data.send.DataSender;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InfluxDBSender implements DataSender {

    private InfluxDB influxDB;
    private String databaseName;
    private TimeUnit precision = TimeUnit.SECONDS;

    @Override
    public Object sendData(Map<String, Object> data) {
        Serie[] series = new Serie[data.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Serie.Builder builder = new Serie.Builder(entry.getKey());
            builder.columns("value");
            builder.values(entry.getValue());
            series[i] = builder.build();
            i++;
        }
        influxDB.write(databaseName, precision, series);


        return null;
    }


    public InfluxDB getInfluxDB() {
        return influxDB;
    }

    public void setInfluxDB(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public TimeUnit getPrecision() {
        return precision;
    }

    public void setPrecision(TimeUnit precision) {
        this.precision = precision;
    }
}
