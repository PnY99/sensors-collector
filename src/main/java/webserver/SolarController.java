package webserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import dto.SolarDTO;
import storage.Database;
import utils.DateUtils;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public class SolarController {
    private static final Gson gson = new Gson();
    public static HttpResponse getDailySolars(HttpExchange httpExchange) {
        HashMap<String, String> queryParameters = WebUtils.parseQueryParameters(httpExchange.getRequestURI().toString());
        String day = queryParameters.getOrDefault("day", DateUtils.today());
        List<SolarDTO> res = Database.getDailySolar(day);
        String json = gson.toJson(res);

        return new HttpResponse(200, json.getBytes());
    }
}
