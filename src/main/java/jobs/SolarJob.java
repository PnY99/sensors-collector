package jobs;

import exceptions.PropertyNotFoundException;
import models.Solar;
import storage.Database;

import java.io.IOException;

public class SolarJob implements Runnable{
    private int inverterId;
    public SolarJob(int inverterId) {
        this.inverterId = inverterId;
    }
    @Override
    public void run() {
        try {
            Solar s = Solar.read(inverterId);
            if (s.isAvailable()) {
                Database.save(s);
                System.out.println("Saved " + s);
            } else {
                System.err.println("Solar reading unavailable");
            }
        } catch (Exception e) {
            System.err.println("Solar reading failed: "+e.getMessage());
        }
    }
}
