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
import ly.pp.justpiano3.activity.local.SoundDownload;
import ly.pp.justpiano3.utils.ImageLoader;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.ScrollTextView;

public final class SoundDownloadAdapter extends BaseAdapter {
    public final SoundDownload soundDownload;
    private final JSONArray jsonArray;
    private final ImageLoader imageLoader;

    public SoundDownloadAdapter(SoundDownload soundDownload, JSONArray jSONArray) {
        this.soundDownload = soundDownload;
        jsonArray = jSONArray;
        imageLoader = ImageLoader.build(soundDownload, "sound_pic");
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
            view = soundDownload.layoutInflater.inflate(R.layout.skin_view, null);
        }
        view.setBackgroundResource(R.drawable.selector_ol_orange);
        ScrollTextView nameTextView = view.findViewById(R.id.skin_name);
        TextView authorTextView = view.findViewById(R.id.skin_author);
        TextView downloadCountTextView = view.findViewById(R.id.download_count);
        TextView sizeTextView = view.findViewById(R.id.skin_size);
        ImageView picImageView = view.findViewById(R.id.skin_image);
        if (i == 0) {
            picImageView.setImageResource(R.drawable.icon);
            nameTextView.setText("还原默认音源");
            authorTextView.setText("还原极品钢琴默认音源");
            sizeTextView.setText("");
            downloadCountTextView.setText("");
            view.setOnClickListener(v -> soundDownload.handleSound(2, "还原默认音源",
                    "", "", "", ".ss"));
        } else {
            try {
                JSONObject jSONObject = jsonArray.getJSONObject(i - 1);
                String soundId = jSONObject.getString("I");
                picImageView.setTag(soundId);
                String soundName = jSONObject.getString("N");
                String soundAuthor = jSONObject.getString("A");
                String soundType = ".ss";
                try {
                    soundType = jSONObject.getString("T");
                } catch (Exception ignore) {
                    // nothing
                }
                picImageView.setImageResource(R.drawable.icon);
                imageLoader.bindBitmap("http://" + OnlineUtil.INSIDE_WEBSITE_URL + "/res/sounds/" + soundId + ".jpg", picImageView);
                nameTextView.setText(soundName);
                authorTextView.setText("by:" + soundAuthor);
                String size = String.format(Locale.getDefault(), "%.2f", jSONObject.getInt("S") / 1024f);
                sizeTextView.setText(size + "MB");
                int downloadNum = jSONObject.getInt("D");
                if (downloadNum > 10000) {
                    downloadCountTextView.setText("下载:" + (downloadNum / 10000) + "万次");
                } else {
                    downloadCountTextView.setText("下载:" + downloadNum + "次");
                }
                String finalSoundType = soundType;
                view.setOnClickListener(v -> soundDownload.handleSound(0, soundName, soundId, size, soundAuthor, finalSoundType));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
