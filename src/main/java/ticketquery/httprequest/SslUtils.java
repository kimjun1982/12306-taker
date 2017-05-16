package ticketquery.httprequest;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SslUtils {
    public static void ignoreSsl() throws Exception {
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier((var0, var1) -> true);
    }

    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustManagers = getTrustManagers();
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustManagers, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    private static TrustManager[] getTrustManagers() throws Exception {
        class TM implements TrustManager, X509TrustManager {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public boolean isServerTrusted(X509Certificate[] certs) {
                return true;
            }

            public boolean isClientTrusted(X509Certificate[] certs) {
                return true;
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            }
        }
        return new TM[]{new TM()};
    }
}