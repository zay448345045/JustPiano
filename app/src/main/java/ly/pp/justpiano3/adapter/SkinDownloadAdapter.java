package ly.pp.justpiano3.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.SkinDownload;
import ly.pp.justpiano3.utils.ImageLoader;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.ScrollText;

public final class SkinDownloadAdapter extends BaseAdapter {
    public final SkinDownload skinDownload;
    private final JSONArray jsonArray;
    private final ImageLoader imageLoader;

    public SkinDownloadAdapter(SkinDownload skinDownload, JSONArray jSONArray) {
        this.skinDownload = skinDownload;
        jsonArray = jSONArray;
        imageLoader = ImageLoader.build(skinDownload, "skin_pic");
    }

    @Override
    public int getCount() {
        return jsonArray.length() + 1;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = skinDownload.layoutInflater.inflate(R.layout.skin_view, null);
        }
        view.setKeepScreenOn(true);
        ScrollText scrollText = view.findViewById(R.id.skin_name);
        TextView textView = view.findViewById(R.id.skin_author);
        TextView textView2 = view.findViewById(R.id.download_count);
        TextView textView3 = view.findViewById(R.id.skin_size);
        ImageView imageView = view.findViewById(R.id.skin_image);
        if (i == 0) {
            imageView.setImageResource(R.drawable.icon);
            scrollText.setText("还原默认皮肤");
            textView.setText("还原极品钢琴默认皮肤");
            textView3.setText("");
            textView2.setText("");
            view.setOnClickListener(v -> skinDownload.mo2992a(2, "还原默认皮肤", "", 0, ""));
        } else {
            try {
                JSONObject jSONObject = jsonArray.getJSONObject(i - 1);
                String string = jSONObject.getString("I");
                imageView.setTag(string);
                String string2 = jSONObject.getString("N");
                String string3 = jSONObject.getString("A");
                int i2 = jSONObject.getInt("S");
                imageView.setImageResource(R.drawable.icon);
                imageLoader.bindBitmap("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/PicSkin" + string, imageView);
                scrollText.setText(string2);
                textView.setText("by:" + string3);
                textView3.setText(i2 + "KB");
                int i3 = jSONObject.getInt("D");
                if (i3 > 10000) {
                    textView2.setText("下载:" + (i3 / 10000) + "万次");
                } else {
                    textView2.setText("下载:" + i3 + "次");
                }
                view.setOnClickListener(v -> skinDownload.mo2992a(0, string2, string, i2, string3));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
