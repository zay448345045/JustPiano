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
import ly.pp.justpiano3.activity.SoundDownload;
import ly.pp.justpiano3.utils.ImageLoader;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.ScrollText;

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
        view.setKeepScreenOn(true);
        view.setBackgroundResource(R.drawable.selector_ol_orange);
        ScrollText scrollText = view.findViewById(R.id.skin_name);
        TextView textView = view.findViewById(R.id.skin_author);
        TextView textView2 = view.findViewById(R.id.download_count);
        TextView textView3 = view.findViewById(R.id.skin_size);
        ImageView imageView = view.findViewById(R.id.skin_image);
        if (i == 0) {
            imageView.setImageResource(R.drawable.icon);
            scrollText.setText("还原默认音源");
            textView.setText("还原极品钢琴默认音源");
            textView3.setText("");
            textView2.setText("");
            view.setOnClickListener(v -> soundDownload.handleSound(2, "还原默认音源", "", 0, "", ".ss"));
        } else {
            try {
                JSONObject jSONObject = jsonArray.getJSONObject(i - 1);
                String soundId = jSONObject.getString("I");
                imageView.setTag(soundId);
                String soundName = jSONObject.getString("N");
                String soundAuthor = jSONObject.getString("A");
                int soundSize = jSONObject.getInt("S");
                String soundType = ".ss";
                try {
                    soundType = jSONObject.getString("T");
                } catch (Exception ignore) {
                    // nothing
                }
                imageView.setImageResource(R.drawable.icon);
                imageLoader.bindBitmap("http://" + OnlineUtil.INSIDE_WEBSITE_URL + "/res/sounds/" + soundId + ".jpg", imageView);
                scrollText.setText(soundName);
                textView.setText("by:" + soundAuthor);
                textView3.setText(soundSize + "KB");
                int downloadNum = jSONObject.getInt("D");
                if (downloadNum > 10000) {
                    textView2.setText("下载:" + (downloadNum / 10000) + "万次");
                } else {
                    textView2.setText("下载:" + downloadNum + "次");
                }
                String finalSoundType = soundType;
                view.setOnClickListener(v -> soundDownload.handleSound(0, soundName, soundId, soundSize, soundAuthor, finalSoundType));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
