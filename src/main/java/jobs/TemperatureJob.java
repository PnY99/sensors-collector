package jobs;

import exceptions.PropertyNotFoundException;
import models.Temperature;
import storage.Database;

import java.io.IOException;

public class TemperatureJob implements Runnable{
    private String sensorId;
    public TemperatureJob(String sensorId) {
        this.sensorId=sensorId;
    }
    @Override
    public void run() {
        try {
            Temperature t = Temperature.read(this.sensorId);
            if(t.isAvailable()) {
                Database.save(t);
                System.out.println("Saved "+t);
            } else {
                System.err.println("Temperature reading unavailable");
            }
        } catch (IOException | PropertyNotFoundException e) {
            System.err.println("Temperature reading failed: "+e.getMessage());
        }
    }
}
