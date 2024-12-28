package webserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import dto.SolarDTO;
import storage.Database;
import utils.DateUtils;

import java.util.HashMap;
import java.util.List;

public class SolarService {
    private static final Gson gson = new Gson();
    public static HttpResponse getDailySolars(HttpExchange httpExchange) {
        HashMap<String, String> queryParameters = WebUtils.parseQueryParameters(httpExchange.getRequestURI().toString());
        String day = queryParameters.getOrDefault("day", DateUtils.today());

        List<SolarDTO> res = Database.getDailySolar(day);
        String json = gson.toJson(res);

        long cacheTimeout = 86400;
        if(day.equals(DateUtils.today())) {
            cacheTimeout = 60;
        }
        return new HttpResponse(200, json.getBytes(), true, cacheTimeout);
    }
}
