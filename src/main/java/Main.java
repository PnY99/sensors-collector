import exceptions.PropertyNotFoundException;
import jobs.SolarJob;
import jobs.TemperatureJob;
import storage.Database;
import utils.Configuration;
import webserver.WebServer;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final ScheduledExecutorService scheduledExecutors = Executors.newScheduledThreadPool(2);
    public static void main(String[] args) throws IOException, PropertyNotFoundException, InterruptedException {
        if(args.length == 0) {
            Configuration.load("application.properties");
        } else {
            Configuration.load(args[0]);
        }
        Database.init();

        boolean webserver = Configuration.getBoolean("webserver.running");
        boolean sensorsReader = Configuration.getBoolean("reader.running");

        if(webserver) {
            WebServer.start(Configuration.getInt("webserver.port"));
        }

        if(sensorsReader) {
            int temperatureInterval = Configuration.getInt("temperature.interval");
            int solarInterval = Configuration.getInt("solar.interval");
            TemperatureJob temperatureJob = new TemperatureJob(Configuration.getString("temperature.sensorId"));
            SolarJob solarJob = new SolarJob(Configuration.getInt("solar.inverterId"));

            scheduledExecutors.scheduleAtFixedRate(temperatureJob, 0, temperatureInterval, TimeUnit.SECONDS);
            scheduledExecutors.scheduleAtFixedRate(solarJob, 0, solarInterval, TimeUnit.SECONDS);
        }
    }
}