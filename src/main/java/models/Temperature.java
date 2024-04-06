package models;

import exceptions.PropertyNotFoundException;
import utils.Configuration;
import utils.DateUtils;
import utils.ParsingUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

public class Temperature {
    private Float temperature;
    private String sensorID;
    boolean available;
    private LocalDateTime timestamp = LocalDateTime.now();

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public static Temperature read(String sensorID) throws IOException, PropertyNotFoundException {
        Temperature t = new Temperature();
        t.setSensorID(sensorID);
        Process cmd;
        if(Configuration.isDebug()) {
            cmd = Runtime.getRuntime().exec("sshpass -p "+Configuration.getString("debug.remote.password")+" ssh pi@"+Configuration.getString("debug.remote.address")+" cat /sys/bus/w1/devices/" + sensorID + "/temperature");
        } else {
            cmd = Runtime.getRuntime().exec("cat /sys/bus/w1/devices/" + sensorID + "/temperature");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
        String rawTemp=reader.readLine();
        if(rawTemp == null) {
            t.setAvailable(false);
            return t;
        }
        Float temperature = ParsingUtils.parseFloat(rawTemp)/1000;

        t.setTemperature(temperature);
        t.setAvailable(true);
        reader.close();
        return t;
    }

    @Override
    public String toString() {
        return DateUtils.localDateTimeToTimestamp(this.timestamp)+" - Temperature: "+this.temperature;
    }
}
