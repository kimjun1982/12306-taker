package ticketquery.httprequest;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import defaultpackage.Config;
import defaultpackage.TravelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticketquery.selenium.SeleniumThread;
import ticketquery.selenium.SeleniumThreadHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class HttpRequestThread implements Runnable {
    private static final Logger log= LoggerFactory.getLogger(HttpRequestThread.class);
    private TravelInfo travelInfo;
    private URL ticketQueryUrl;
    private BlockingQueue<Integer> blockingQueue;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public HttpRequestThread(BlockingQueue<Integer> queue) throws Exception {
        this.blockingQueue = queue;
        this.travelInfo = TravelInfo.getTravelInfo();
        this.ticketQueryUrl = Helper.getTicketQueryURL(travelInfo, Config.requestBaseURL);
    }

    @Override
    public void run() {
        Helper.waitToStartUntil(travelInfo.getStartTimeToQuery());

        while (true) {
            try {
                if (SeleniumThread.isSeleniumBookingThreadInProgress()) {
                    Thread.sleep(100);
                    continue;
                }

                log.info("Http Request:: Producing data at {}", new Date());
                long startTiming = System.currentTimeMillis();

                JsonObject resultObj = sendRequest(ticketQueryUrl, 3);
                List<ResultDataEntity> resultList = parseQueryResult(resultObj);

                int indexOfRecommendation = analyzeResult(resultList);
                if (indexOfRecommendation > -1) {
                    blockingQueue.put(indexOfRecommendation);
                    log.info("Http Request:: Queued index: [ {}] from the bookable list. ", indexOfRecommendation);
                    log.info("Http Request:: Waiting data to be consumed [ {}]", LocalDateTime.now());

                    Thread.yield();
                    Thread.sleep(5000);
                    log.info("Http Request:: No Consumer after few seconds, refresh it by myself");
                    blockingQueue.poll();
                    continue;
                }
                log.info("Http Request::The whole http request take: {} ms", System.currentTimeMillis() - startTiming);
                SeleniumThreadHelper.sleep(travelInfo.getTimeIntervalBetweenHttpRequestQuery());

            } catch (Throwable t) {
                log.info("Http Request::System error to sleep 5 seconds!");
                t.printStackTrace();
                SeleniumThreadHelper.sleep(5000);
            }
        }
    }

    public JsonObject sendRequest(final URL url, int tolerantDelay) throws Exception {
        JsonObject result;
        Future<JsonObject> future;

        int i = 0;
        while (i++ < 5) {
            future = executor.submit(new Callable<JsonObject>() {
                @Override
                public JsonObject call() throws Exception {
                    return sendRequest(url);
                }
            });
            try {
                result = future.get(tolerantDelay, TimeUnit.SECONDS);
                return result;
            } catch (Throwable t) {
                log.info("Http Request:: sending http request timeout in %s second, resending %n", tolerantDelay);
                future.cancel(true);
                continue;
            }
        }
        return null;
    }


    public JsonObject sendRequest(URL url) throws Exception {
        Long start = System.currentTimeMillis();
        log.info(" Http Request:: sending http request");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        if (200 == conn.getResponseCode()) {
            InputStream inputStream = conn.getInputStream();

            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            inputStream.close();
            String objStr = stringBuffer.toString().replaceAll("--", "0").replaceAll("有", "1000").replaceAll("无", "0");
            JsonObject resultObj = new JsonParser().parse(objStr).getAsJsonObject();

            log.info(LocalDateTime.now() + " Http Request:: received http request response");
            log.info("Http Request:: spend time: %s ms %n ", System.currentTimeMillis() - start);
            return resultObj;

        } else {
            log.info(LocalDateTime.now() + " Http Request:: sendRequest() not response with 200, re-trying.");
            Thread.sleep(100);
            return sendRequest(url);
        }
    }

    public List<ResultDataEntity> parseQueryResult(JsonObject jsonResult) {
        JsonArray array = jsonResult.getAsJsonArray("data");
        log.info("Http Request:: %s trains returned,now checking availability...  %n", array.size());

        List<ResultDataEntity> resultList = new ArrayList<ResultDataEntity>();
        for (Object object : array) {
            JsonObject jsonObject = ((JsonObject) object).getAsJsonObject("queryLeftNewDTO");
            ResultDataEntity eachResult = new Gson().fromJson(jsonObject, ResultDataEntity.class);
            resultList.add(eachResult);
        }
        return resultList;
    }

    public int analyzeResult(List<ResultDataEntity> resultList) throws Exception {

        int indexInAvailableList = -1;
        int indexOfRecommendation = -1;
        int lastBiggestSeatCount = 0;
        log.info("Http Request:: Below trains are available");
        for (ResultDataEntity result : resultList) {
            if (result.getCanWebBuy().equalsIgnoreCase("Y")) {
                log.info(result.getAvailableTicketData());
                indexInAvailableList++;

                if (!Helper.isStationMatched(result))
                    continue;

                if (Helper.isTrainTimeFitted(result)) { // 匹配到期望时间段
                    // 只考虑一等，二等和无座
                    Integer seatCount = Integer.parseInt(result.getZy_num()) + Integer.parseInt(result.getZe_num())
                            + Integer.parseInt(result.getWz_num());
                    if (seatCount > lastBiggestSeatCount) {
                        lastBiggestSeatCount = seatCount;
                        indexOfRecommendation = indexInAvailableList;
                    }
                }
            }
        }
        if (indexOfRecommendation > -1) {
            log.info("Http Request::---Checking completed, proceeding to Selenium thread---");
        } else {
            log.info("Http Request::---Checking completed, NO tickets matching your requirement---");
        }

        return indexOfRecommendation;
    }

}
