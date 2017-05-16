package defaultpackage;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ticketquery.httprequest.SslUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Config {
    public static String url = "https://kyfw.12306.cn/otn/login/init";

    public static String pathToChromeBrowser = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";

    public static String pathToChromeDriver = "/Users/lsz/sandbox/devtool/selenium/chromedriver";

    public static String requestBaseURL = "https://kyfw.12306.cn/otn/leftTicket/query";

    public static String travelInfoClasspath = "travelInformation.example";

}
