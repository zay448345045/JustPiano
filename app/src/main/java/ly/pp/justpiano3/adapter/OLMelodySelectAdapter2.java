package ly.pp.justpiano3.adapter;

import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLMelodySelect;
import ly.pp.justpiano3.activity.PopUserInfo;
import ly.pp.justpiano3.activity.WaterfallActivity;
import ly.pp.justpiano3.listener.OLMelodySongsPlayClick;
import ly.pp.justpiano3.thread.AcceptFavorThread;

import java.util.HashMap;
import java.util.List;

public final class OLMelodySelectAdapter2 extends BaseAdapter {
    public final OLMelodySelect olMelodySelect;
    private final int length;
    private final List<HashMap> songsList;

    public OLMelodySelectAdapter2(OLMelodySelect oLMelodySelect, int i, List<HashMap> list) {
        olMelodySelect = oLMelodySelect;
        length = i;
        songsList = list;
    }

    @Override
    public int getCount() {
        return length;
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
            view = olMelodySelect.layoutInflater2.inflate(R.layout.ol_c_view, null);
        }
        view.setKeepScreenOn(true);
        String trim = songsList.get(i).get("songName").toString().trim();
        String songId = (String) songsList.get(i).get("songID");
        ImageButton imageButton = view.findViewById(R.id.ol_favor_b);
        imageButton.setImageResource(R.drawable.favor_1);
        imageButton.setOnClickListener(v -> {
            Toast.makeText(olMelodySelect.getBaseContext(), "《" + trim + "》已加入网络收藏夹", Toast.LENGTH_SHORT).show();
            imageButton.setImageResource(R.drawable.favor);
            new AcceptFavorThread(olMelodySelect, songId, "F", olMelodySelect.jpapplication.getAccountName()).start();
        });
        TextView songName = view.findViewById(R.id.ol_s_n);
        songName.setText(trim);
        songName.setMovementMethod(ScrollingMovementMethod.getInstance());
        songName.setHorizontallyScrolling(true);
        ((TextView) view.findViewById(R.id.ol_nandu)).setText("难度:" + songsList.get(i).get("degree"));
        double d = (double) songsList.get(i).get("degree");
        ((TextView) view.findViewById(R.id.ol_items)).setText(songsList.get(i).get("items").toString());
        TextView textView = view.findViewById(R.id.ol_topuser);
        String topUserKitiName = songsList.get(i).get("topUser").toString();
        if (!topUserKitiName.equals("(暂无冠军)")) {
            textView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra("head", 1);
                intent.putExtra("userKitiName", topUserKitiName);
                intent.setClass(olMelodySelect, PopUserInfo.class);
                olMelodySelect.startActivity(intent);
            });
        } else {
            textView.setOnClickListener(null);
        }
        textView.setText(topUserKitiName);
        ((TextView) view.findViewById(R.id.ol_topscore)).setText("得分:" + songsList.get(i).get("topScore"));
        int songsTime = Integer.parseInt(songsList.get(i).get("length").toString());
        String str1 = songsTime / 60 >= 10 ? "" + songsTime / 60 : "0" + songsTime / 60;
        String str2 = songsTime % 60 >= 10 ? "" + songsTime % 60 : "0" + songsTime % 60;
        ((TextView) view.findViewById(R.id.ol_length)).setText("时长:" + str1 + ":" + str2);
        ((TextView) view.findViewById(R.id.ol_update)).setText("冠军时间:" + songsList.get(i).get("update"));
        ((TextView) view.findViewById(R.id.ol_playcount)).setText("播放量:" + songsList.get(i).get("playCount"));
        view.findViewById(R.id.ol_play_button).setOnClickListener(new OLMelodySongsPlayClick(this, trim, songId, (Integer) songsList.get(i).get("topScore"), d));
        ImageView waterFallImageView = view.findViewById(R.id.ol_play_waterfall);
        waterFallImageView.setOnClickListener(new OLMelodySongsPlayClick(this, songId, new Intent().setClass(olMelodySelect, WaterfallActivity.class)));
        return view;
    }
}
