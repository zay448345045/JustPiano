package ly.pp.justpiano3.handler;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * OkHttpCookiesHandler
 * OkHttp cookies 处理器
 *
 * @author Jhpz
 * @since create(2023 / 7 / 29)
 **/
public class OkHttpCookiesHandler implements CookieJar {

    private final List<Cookie> cookieStoreList = new ArrayList<>();

    @Override
    public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
        cookieStoreList.addAll(cookies);
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
        return cookieStoreList;
    }
}
