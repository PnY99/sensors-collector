package webserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import dto.TemperatureDTO;
import dto.TemperatureDaySummaryDTO;
import dto.TemperatureMonthSummaryDTO;
import storage.Database;
import utils.Configuration;
import utils.DateUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public class TemperatureController {
    private static final Gson gson = new Gson();
    static HttpResponse getDailyTemperatures(HttpExchange httpExchange) {
        HashMap<String, String> queryParameters = WebUtils.parseQueryParameters(httpExchange.getRequestURI().toString());
        String day = queryParameters.getOrDefault("day", DateUtils.today());

        List<TemperatureDTO> readings = Database.getDailyTemperatures(day);

        String json = gson.toJson(readings);

        long cacheTimeout;
        if(day.equals(DateUtils.today())) {
            cacheTimeout = 60;
        } else {
            cacheTimeout = 2678400;
        }
        return new HttpResponse(200, json.getBytes(), true, cacheTimeout);
    }

    static HttpResponse getMonthStatistics(HttpExchange httpExchange) {
        HashMap<String, String> queryParameters = WebUtils.parseQueryParameters(httpExchange.getRequestURI().toString());
        String month = queryParameters.get("month");
        String day = queryParameters.get("day");

        long cacheTimeout = 2678400;

        if(month != null) {
            TemperatureMonthSummaryDTO data = Database.getMonthStatistics(month);
            String json = gson.toJson(data);

            if(month.equals(DateUtils.currentMonth())) {
                cacheTimeout = 1800;
            }
            return new HttpResponse(200, json.getBytes(), true, cacheTimeout);
        } else if(day != null) {
            TemperatureDaySummaryDTO data = Database.getDailySummary(day);
            String json = gson.toJson(data);

            if(day.equals(DateUtils.currentMonth())) {
                cacheTimeout = 1800;
            }
            return new HttpResponse(200, json.getBytes(), true, cacheTimeout);
        } else {
            return new HttpResponse(400, new byte[0], true, cacheTimeout);
        }
    }
}
