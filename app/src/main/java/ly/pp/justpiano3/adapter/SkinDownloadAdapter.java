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
            view.setOnClickListener(v -> skinDownload.handleSkin(2, "还原默认皮肤", "", 0, ""));
        } else {
            try {
                JSONObject jSONObject = jsonArray.getJSONObject(i - 1);
                String skinId = jSONObject.getString("I");
                imageView.setTag(skinId);
                String skinName = jSONObject.getString("N");
                String skinAuthor = jSONObject.getString("A");
                int size = jSONObject.getInt("S");
                imageView.setImageResource(R.drawable.icon);
                imageLoader.bindBitmap("https://" + OnlineUtil.INSIDE_WEBSITE_URL + "/res/skins/" + skinId + ".jpg", imageView);
                scrollText.setText(skinName);
                textView.setText("by:" + skinAuthor);
                textView3.setText(size + "KB");
                int downloadNum = jSONObject.getInt("D");
                if (downloadNum > 10000) {
                    textView2.setText("下载:" + (downloadNum / 10000) + "万次");
                } else {
                    textView2.setText("下载:" + downloadNum + "次");
                }
                view.setOnClickListener(v -> skinDownload.handleSkin(0, skinName, skinId, size, skinAuthor));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
