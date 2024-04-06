package webserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import dto.TemperatureDTO;
import dto.TemperatureDaySummaryDTO;
import dto.TemperatureMonthSummaryDTO;
import storage.Database;
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
        return new HttpResponse(200, json.getBytes());
    }

    static HttpResponse getMonthStatistics(HttpExchange httpExchange) {
        HashMap<String, String> queryParameters = WebUtils.parseQueryParameters(httpExchange.getRequestURI().toString());
        String month = queryParameters.get("month");
        String day = queryParameters.get("day");

        if(month != null) {
            TemperatureMonthSummaryDTO data = Database.getMonthStatistics(month);
            String json = gson.toJson(data);
            return new HttpResponse(200, json.getBytes());
        } else if(day != null) {
            TemperatureDaySummaryDTO data = Database.getDailySummary(day);
            String json = gson.toJson(data);
            return new HttpResponse(200, json.getBytes());
        } else {
            return new HttpResponse(400, new byte[0]);
        }
    }
}
