package ly.pp.justpiano3.utils;

import ly.pp.justpiano3.handler.OkHttpCookiesHandler;
import okhttp3.OkHttpClient;

/**
 * OkHttpUtil
 * OkHttpUtil
 *
 * @author Jhpz
 * @since create(2023 / 7 / 29)
 **/
public final class OkHttpUtil {
    private static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .cookieJar(new OkHttpCookiesHandler())
                .build();
    }

    public static OkHttpClient client() {
        return client;
    }
}
