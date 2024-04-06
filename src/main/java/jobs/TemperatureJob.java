package jobs;

import exceptions.PropertyNotFoundException;
import models.Temperature;
import storage.Database;

import java.io.IOException;

public class TemperatureJob implements Runnable{
    private String sensorId;
    private int runInterval;
    public TemperatureJob(String sensorId, int runInterval) {
        this.sensorId=sensorId;
        this.runInterval=runInterval;
    }
    @Override
    public void run() {
        try {
            while(true) {
                Temperature t = Temperature.read(this.sensorId);
                if(t.isAvailable()) {
                    Database.save(t);
                    System.out.println("Saved "+t);
                } else {
                    System.err.println("Temperature reading failed");
                }
                Thread.sleep(runInterval* 1000L);
            }
        } catch (IOException | InterruptedException | PropertyNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
