package webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import utils.Configuration;
import utils.FunctionWIthException;
import utils.HashedCache;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class WebServer {
    private static String html_dir;
    private static HashedCache<String, HttpResponse> cache;
    private static HashMap<String, File> staticPages;
    public static void start(int port) throws IOException {
        html_dir = Configuration.getString("webserver.htmlDirectory");
        cache = new HashedCache<>(200);
        staticPages = new HashMap<>();
        indexStaticContent(Path.of(html_dir));


        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(null);

        httpServer.createContext("/api/temperatures", httpExchange -> {
            dispatchCachedRequest(httpExchange, TemperatureService::getDailyTemperatures);
        });

        httpServer.createContext("/api/temperatures/statistics", httpExchange -> {
            dispatchCachedRequest(httpExchange, TemperatureService::getMonthStatistics);
        });

        httpServer.createContext("/api/solar", httpExchange -> {
            dispatchCachedRequest(httpExchange, SolarService::getDailySolars);
        });

        httpServer.createContext("/", httpExchange -> {
            dispatchCachedRequest(httpExchange, WebServer::index);
        });

        new Thread(httpServer::start).start();
    }

    private static HttpResponse index(HttpExchange httpExchange) throws IOException {
        try {
            File requestedPage;
            if (httpExchange.getRequestURI().toString().equals("/")) {
                requestedPage = staticPages.get("/index.html");
            } else {
                requestedPage = staticPages.get(httpExchange.getRequestURI().toString());
            }

            if(requestedPage == null) {
                throw new FileNotFoundException();
            }

            InputStream inputStream = new FileInputStream(requestedPage);
            byte[] indexFile = inputStream.readAllBytes();
            inputStream.close();

            return new HttpResponse(200, indexFile, true, 2678400);
        } catch (FileNotFoundException e) {
            return new HttpResponse(404, new byte[0], true, 2678400);
        }
    }

    private static void indexStaticContent(Path root) throws IOException {
        Files.walk(root).forEach(file -> {
            if(Files.isRegularFile(file)) {
                String name = file.toString().replace(html_dir, "");
                staticPages.put(name, file.toFile());
            }
        });
    }

    private static void dispatchCachedRequest(HttpExchange httpExchange, FunctionWIthException<HttpExchange, HttpResponse, Exception> handler) {
        try {
            String uri = httpExchange.getRequestURI().toString();

            //cache lookup
            HttpResponse response = cache.getItem(uri);
            if(response == null) {
                response = handler.apply(httpExchange);
                if(response.isCacheable()) {
                    cache.putItem(uri, response, response.getCacheTimeout());
                }
            }

            //send response
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
