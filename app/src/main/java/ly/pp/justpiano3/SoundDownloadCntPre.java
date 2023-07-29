package ly.pp.justpiano3;


import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public final class SoundDownloadCntPre extends Thread {
    private final SoundDownload soundDownload;
    private final String fileName;

    SoundDownloadCntPre(SoundDownload soundDownload, String str) {
        this.soundDownload = soundDownload;
        fileName = str;
    }

    @Override
    public final void run() {
        try {
            // 创建HttpUrl.Builder对象，用于添加查询参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + soundDownload.jpapplication.getServer() + ":8910/JustPianoServer/server/DownloadSound").newBuilder();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("version", soundDownload.jpapplication.getVersion());
            formBuilder.add("path", fileName);
            // 创建Request对象，用于发送请求
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .post(formBuilder.build())
                    .build();
            // 同步执行请求，获取Response对象
            Response response = OkHttpUtil.client().newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
