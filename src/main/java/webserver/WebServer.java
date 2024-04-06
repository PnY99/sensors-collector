package webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exceptions.PropertyNotFoundException;
import utils.Configuration;
import utils.FunctionWIthException;

import java.io.*;
import java.net.InetSocketAddress;

public class WebServer {
    private static String html_dir;
    public static void start(int port) throws IOException, PropertyNotFoundException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(null);
        html_dir = Configuration.getString("webserver.htmlDirectory");

        httpServer.createContext("/api/temperatures", httpExchange -> {
            dispatchRequest(httpExchange, TemperatureController::getDailyTemperatures);
        });

        httpServer.createContext("/api/temperatures/statistics", httpExchange -> {
            dispatchRequest(httpExchange, TemperatureController::getMonthStatistics);
        });

        httpServer.createContext("/api/solar", httpExchange -> {
            dispatchRequest(httpExchange, SolarController::getDailySolars);
        });

        httpServer.createContext("/", httpExchange -> {
            dispatchRequest(httpExchange, WebServer::index);
        });

        new Thread(httpServer::start).start();
    }

    private static HttpResponse index(HttpExchange httpExchange) throws IOException {
        try {
            InputStream inputStream;
            if (httpExchange.getRequestURI().toString().equals("/")) {
                inputStream = new FileInputStream(html_dir+"/index.html");
            } else {
                inputStream = new FileInputStream(html_dir+httpExchange.getRequestURI().toString());
            }

            byte[] indexFile = inputStream.readAllBytes();
            inputStream.close();

            return new HttpResponse(200, indexFile);
        } catch (FileNotFoundException e) {
            return new HttpResponse(404, new byte[0]);
        }
    }

    private static void dispatchRequest(HttpExchange httpExchange, FunctionWIthException<HttpExchange, HttpResponse, Exception> handler) {
        try {
            HttpResponse response = handler.apply(httpExchange);
            httpExchange.sendResponseHeaders(response.getCode(), response.getResponseBody().length);
            httpExchange.getResponseBody().write(response.getResponseBody());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            try {
                httpExchange.sendResponseHeaders(500, 0);
            } catch (Exception ignored) {}
        } finally {
            httpExchange.close();
        }
    }
}
