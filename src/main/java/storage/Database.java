package storage;

import dto.SolarDTO;
import dto.TemperatureDTO;
import dto.TemperatureDaySummaryDTO;
import dto.TemperatureMonthSummaryDTO;
import exceptions.PropertyNotFoundException;
import models.Solar;
import models.Temperature;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.ResultIterable;
import utils.Configuration;
import utils.DateUtils;
import org.jdbi.v3.core.Jdbi;

import java.util.*;
import java.util.stream.Collectors;

public class Database {
    private static Jdbi jdbi;

    public static void init() throws PropertyNotFoundException {
        String address = Configuration.getString("database.address");
        String database = Configuration.getString("database.name");
        String username = Configuration.getString("database.username");
        String password = Configuration.getString("database.password");
        jdbi = Jdbi.create("jdbc:mysql://"+address+"/"+database+"?zeroDateTimeBehavior=round", username, password);
    }
    public static void save(Solar s) {
        if(!s.isAvailable()) return;
        jdbi.useHandle(h -> {
            h.execute("INSERT INTO Solare (Data, ID_Inverter, Potenza, Energia, Vdc, Idc, Vac, Iac) VALUES (?,?,?,?,?,?,?,?)",
                    DateUtils.localDateTimeToTimestamp(s.getDate()),
                    s.getInverterID(),
                    s.getPower(),
                    s.getEnergy(),
                    s.getVdc(),
                    s.getIdc(),
                    s.getVac(),
                    s.getIac());
            h.close();
        });
    }

    public static void save(Temperature t) {
        if(!t.isAvailable()) return;
        jdbi.useHandle(h -> {
            h.execute("INSERT INTO Temperature (ID, Temperatura, Data) VALUES (?,?,?)",
                    t.getSensorID(),
                    t.getTemperature(),
                    DateUtils.localDateTimeToTimestamp(t.getTimestamp()));
            h.close();
        });
    }

    public static List<TemperatureDTO> getDailyTemperatures(String day) {
        Handle handle = jdbi.open();
        ResultIterable<Map<String, String>> rows = handle.select("SELECT * FROM Temperature WHERE Data BETWEEN ? AND ?", day+" 00:00:00", day+" 23:59:59").mapToMap(String.class);
        List<TemperatureDTO> result = rows.stream().map(entry -> {
            TemperatureDTO dto = new TemperatureDTO();
            dto.timestamp = entry.get("data");
            dto.temperature = Float.parseFloat(entry.get("temperatura"));
            return dto;
        }).collect(Collectors.toList());
        handle.close();
        return result;
    }

    public static TemperatureMonthSummaryDTO getMonthStatistics(String month) {
        Handle handle = jdbi.open();
        List<Map<String, String>> rows = handle.select("SELECT * FROM Temperature WHERE Data BETWEEN ? and ? ORDER By Data", month+"-01 00:00:00", month+"-31 23:59:59").mapToMap(String.class).list();
        String currentDay, previousDay="";
        String time_max, time_min;
        float max, min, average;
        int dailyReadingsCount = 1, monthReadingsCount=1;
        TemperatureMonthSummaryDTO monthSummary = new TemperatureMonthSummaryDTO();
        monthSummary.month = month;
        monthSummary.day_summaries = new ArrayList<>();
        if(rows.isEmpty()) return monthSummary;

        //init values with first row record
        Map<String, String> firstRow = rows.remove(0);
        currentDay = previousDay = firstRow.get("data").split(" ")[0];
        monthSummary.average = monthSummary.max = monthSummary.min = max = min = average = Float.parseFloat(firstRow.get("temperatura"));
        time_min = time_max = firstRow.get("data").split(" ")[1];
        monthSummary.time_max = monthSummary.time_min = firstRow.get("data");

        for(Map<String, String> row: rows) {
            currentDay = row.get("data").split(" ")[0];
            if(!currentDay.equals(previousDay)) {
                TemperatureDaySummaryDTO daySummary = new TemperatureDaySummaryDTO();
                daySummary.max = max;
                daySummary.min = min;
                daySummary.time_max = time_max;
                daySummary.time_min = time_min;
                daySummary.day = previousDay;
                daySummary.average = average/dailyReadingsCount;

                monthSummary.day_summaries.add(daySummary);
                monthSummary.average_max+=max;
                monthSummary.average_min+=min;

                previousDay = currentDay;
                max = Float.MIN_VALUE;
                min = Float.MAX_VALUE;
                average = 0;
                dailyReadingsCount = 0;
            }
            float temp = Float.parseFloat(row.get("temperatura"));
            String time = row.get("data").split(" ")[1];
            if(temp > max) {
                max = temp;
                time_max = time;
            }
            if(temp < min) {
                min = temp;
                time_min = time;
            }
            if(temp > monthSummary.max) {
                monthSummary.max = temp;
                monthSummary.time_max = currentDay+" "+time;
            }
            if(temp < monthSummary.min) {
                monthSummary.min = temp;
                monthSummary.time_min = currentDay+" "+time;
            }
            average+=temp;
            monthSummary.average+=temp;
            dailyReadingsCount++;
            monthReadingsCount++;
        }

        //last day
        TemperatureDaySummaryDTO dto = new TemperatureDaySummaryDTO();
        dto.max = max;
        monthSummary.average_max+=max;
        monthSummary.average_min+=min;
        dto.min = min;
        dto.time_max = time_max;
        dto.time_min = time_min;
        dto.day = previousDay;
        dto.average = average/dailyReadingsCount;
        monthSummary.day_summaries.add(dto);

        monthSummary.average = monthSummary.average/monthReadingsCount;
        monthSummary.average_max = monthSummary.average_max/monthSummary.day_summaries.size();
        monthSummary.average_min = monthSummary.average_min/monthSummary.day_summaries.size();

        handle.close();
        return monthSummary;
    }

    public static TemperatureDaySummaryDTO getDailySummary(String day) {
        Handle handle = jdbi.open();
        List<Map<String, String>> rows = handle.select("SELECT * FROM Temperature WHERE Data BETWEEN ? and ? ORDER By Data", day+" 00:00:00", day+" 23:59:59").mapToMap(String.class).list();
        TemperatureDaySummaryDTO dto = new TemperatureDaySummaryDTO();
        dto.day = day;
        dto.average = rows.stream().collect(Collectors.averagingDouble(e -> Double.parseDouble(e.get("temperatura")))).floatValue();
        Optional<Map<String, String>> minRow = rows.stream().min((e1, e2) -> Float.compare(Float.parseFloat(e1.get("temperatura")), Float.parseFloat(e2.get("temperatura"))));
        Optional<Map<String, String>> maxRow = rows.stream().max((e1, e2) -> Float.compare(Float.parseFloat(e1.get("temperatura")), Float.parseFloat(e2.get("temperatura"))));

        if(minRow.isPresent()) {
            dto.min = Float.parseFloat(minRow.get().get("temperatura"));
            dto.time_min = minRow.get().get("data").split(" ")[1];
        }
        if(maxRow.isPresent()) {
            dto.max = Float.parseFloat(maxRow.get().get("temperatura"));
            dto.time_max = maxRow.get().get("data").split(" ")[1];
        }
        handle.close();
        return dto;
    }

    public static List<SolarDTO> getDailySolar(String day) {
        Handle handle = jdbi.open();
        ResultIterable<Map<String, String>> rows =  handle.select("SELECT * FROM Solare WHERE Data BETWEEN ? AND ?", day+" 00:00:00", day+" 23:59:59").mapToMap(String.class);
        List<SolarDTO> res = rows.stream()
                .map(e -> {
                    SolarDTO dto = new SolarDTO();
                    dto.timestamp = e.get("data");
                    dto.inverter_id = Integer.parseInt(e.get("id_inverter"));
                    dto.energy = Float.parseFloat(e.get("energia"));
                    dto.power = Float.parseFloat(e.get("potenza"));
                    dto.iac = Float.parseFloat(e.get("iac"));
                    dto.vac = Float.parseFloat(e.get("vac"));
                    dto.idc = Float.parseFloat(e.get("idc"));
                    dto.vdc = Float.parseFloat(e.get("vdc"));
                    return dto;
                }).collect(Collectors.toList());
        handle.close();
        return res;
    }
}
