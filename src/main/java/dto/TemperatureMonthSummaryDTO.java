package dto;

import java.util.List;

public class TemperatureMonthSummaryDTO {
    public float max = Float.MIN_VALUE, min=Float.MAX_VALUE, average=0, average_max=0, average_min=0;
    public String time_max, time_min, month;
    public List<TemperatureDaySummaryDTO> day_summaries;
}
