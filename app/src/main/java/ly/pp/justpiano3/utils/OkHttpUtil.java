package ly.pp.justpiano3.utils;

import ly.pp.justpiano3.handler.OkHttpCookiesHandler;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttpUtil
 *
 * @author Jhpz
 * @since create(2023 / 7 / 29)
 **/
public final class OkHttpUtil {
    private static final Integer HTTP_PORT = 8910;
    private static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .cookieJar(new OkHttpCookiesHandler())
                .build();
    }

    public static OkHttpClient client() {
        return client;
    }

    public static String sendPostRequest(String function, FormBody formBody) {
        try (Response response = client.newCall(new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme("http")
                        .host(OnlineUtil.server)
                        .port(HTTP_PORT)
                        .addPathSegment("JustPianoServer")
                        .addPathSegment("server")
                        .addPathSegment(function)
                        .build())
                .post(formBody)
                .build()).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
