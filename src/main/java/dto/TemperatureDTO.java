package dto;

public class TemperatureDTO {
    public float temperature;
    public String timestamp;

    @Override
    public String toString() {
        return this.timestamp+": "+this.temperature;
    }
}
