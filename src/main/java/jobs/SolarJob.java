package jobs;

import exceptions.PropertyNotFoundException;
import models.Solar;
import storage.Database;

import java.io.IOException;

public class SolarJob implements Runnable{
    private int inverterId, runInterval;
    public SolarJob(int inverterId, int runInterval) {
        this.inverterId = inverterId;
        this.runInterval = runInterval;
    }
    @Override
    public void run() {
        try {
            while(true) {
                Solar s = Solar.read(inverterId);
                if(s.isAvailable()) {
                    Database.save(s);
                    System.out.println("Saved "+s);
                } else {
                    System.err.println("Solar reading failed");
                }
                Thread.sleep(runInterval* 1000L);
            }
        } catch (IOException | InterruptedException | PropertyNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
