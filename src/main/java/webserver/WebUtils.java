package webserver;

import java.util.HashMap;

public class WebUtils {
    static HashMap<String, String> parseQueryParameters(String uri) {
        HashMap<String, String> res = new HashMap<>();
        String[] splittedUri = uri.split("\\?");
        if(splittedUri.length > 1) {
            String queryString = splittedUri[1];
            String[] parameters = queryString.split("&");
            for(String parameter: parameters) {
                String[] splittedParameter = parameter.split("=");
                res.put(splittedParameter[0], splittedParameter[1]);
            }
        }
        return res;
    }
}
