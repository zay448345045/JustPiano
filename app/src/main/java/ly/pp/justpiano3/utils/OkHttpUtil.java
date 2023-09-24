package ly.pp.justpiano3.utils;

import ly.pp.justpiano3.handler.OkHttpCookiesHandler;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * OkHttpUtil
 * OkHttpUtil
 *
 * @author Jhpz
 * @since create(2023 / 7 / 29)
 **/
public class OkHttpUtil {
    private static final OkHttpClient client;
    private static final String JESSIONID = "JESSIONID";

    static {
        client = new OkHttpClient.Builder()
                .cookieJar(new OkHttpCookiesHandler())
                .build();
    }

    public static OkHttpClient client() {
        return client;
    }

    public static String getJavaSessionId() {
        for (Cookie cookie : OkHttpUtil.client().cookieJar().loadForRequest(HttpUrl.get("/"))) {
            if (JESSIONID.equals(cookie.name())) {
                return cookie.value();
            }
        }
        return null;
    }
}
