package ly.pp.justpiano3.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.local.SkinDownload;
import ly.pp.justpiano3.utils.ImageLoader;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.ScrollTextView;

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
        ScrollTextView nameTextView = view.findViewById(R.id.skin_name);
        TextView authorTextView = view.findViewById(R.id.skin_author);
        TextView downloadCountTextView = view.findViewById(R.id.download_count);
        TextView sizeTextView = view.findViewById(R.id.skin_size);
        ImageView picImageView = view.findViewById(R.id.skin_image);
        if (i == 0) {
            picImageView.setImageResource(R.drawable.icon);
            nameTextView.setText("还原默认皮肤");
            authorTextView.setText("还原极品钢琴默认皮肤");
            sizeTextView.setText("");
            downloadCountTextView.setText("");
            view.setOnClickListener(v -> skinDownload.handleSkin(2, "还原默认皮肤", "", "", ""));
        } else {
            try {
                JSONObject jSONObject = jsonArray.getJSONObject(i - 1);
                String skinId = jSONObject.getString("I");
                picImageView.setTag(skinId);
                String skinName = jSONObject.getString("N");
                String skinAuthor = jSONObject.getString("A");
                String size = String.format(Locale.getDefault(), "%.2f", jSONObject.getInt("S") / 1024f);
                picImageView.setImageResource(R.drawable.icon);
                imageLoader.bindBitmap("https://" + OnlineUtil.INSIDE_WEBSITE_URL + "/res/skins/" + skinId + ".jpg", picImageView);
                nameTextView.setText(skinName);
                authorTextView.setText("by:" + skinAuthor);
                sizeTextView.setText(size + "MB");
                int downloadNum = jSONObject.getInt("D");
                if (downloadNum > 10000) {
                    downloadCountTextView.setText("下载:" + (downloadNum / 10000) + "万次");
                } else {
                    downloadCountTextView.setText("下载:" + downloadNum + "次");
                }
                view.setOnClickListener(v -> skinDownload.handleSkin(0, skinName, skinId, size, skinAuthor));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
