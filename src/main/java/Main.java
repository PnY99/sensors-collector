import exceptions.PropertyNotFoundException;
import jobs.SolarJob;
import jobs.TemperatureJob;
import storage.Database;
import utils.Configuration;
import webserver.WebServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, PropertyNotFoundException, InterruptedException {
        Configuration.load(args[0]);
        Database.init();

        boolean webserver = Configuration.getBoolean("webserver.running");
        boolean sensorsReader = Configuration.getBoolean("reader.running");

        if(webserver) {
            WebServer.start(Configuration.getInt("webserver.port"));
        }

        if(sensorsReader) {
            TemperatureJob temperatureJob = new TemperatureJob(Configuration.getString("temperature.sensorId"), Configuration.getInt("temperature.interval"));
            SolarJob solarJob = new SolarJob(Configuration.getInt("solar.inverterId"), Configuration.getInt("solar.interval"));

            Thread temperatureThread = new Thread(temperatureJob);
            Thread solarThread = new Thread(solarJob);

            temperatureThread.start();
            solarThread.start();

            while (true) {
                if (!temperatureThread.isAlive()) {
                    System.out.println("Restarting temperature thread");
                    temperatureThread = new Thread(temperatureJob);
                    temperatureThread.start();
                }

                if (!solarThread.isAlive()) {
                    System.out.println("Restarting solar thread");
                    solarThread = new Thread(solarJob);
                    solarThread.start();
                }
                Thread.sleep(10000L);
            }
        }
    }
}