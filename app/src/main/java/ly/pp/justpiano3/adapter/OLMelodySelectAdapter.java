package ly.pp.justpiano3.adapter;

import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.local.WaterfallActivity;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.OLMelodySelect;
import ly.pp.justpiano3.activity.online.PopUserInfo;
import ly.pp.justpiano3.listener.OLMelodySongsPlayClick;
import ly.pp.justpiano3.thread.AcceptFavorThread;
import ly.pp.justpiano3.utils.DeviceUtil;

public final class OLMelodySelectAdapter extends BaseAdapter {
    public final OLMelodySelect olMelodySelect;
    private final int length;
    private final List<Map<String, Object>> songsList;

    public OLMelodySelectAdapter(OLMelodySelect oLMelodySelect, int length, List<Map<String, Object>> list) {
        olMelodySelect = oLMelodySelect;
        this.length = length;
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
        String songName = songsList.get(i).get("songName").toString().trim();
        String songId = (String) songsList.get(i).get("songID");
        ImageButton imageButton = view.findViewById(R.id.ol_favor_b);
        imageButton.setImageResource(R.drawable.favor_1);
        imageButton.setOnClickListener(v -> {
            Toast.makeText(olMelodySelect.getBaseContext(), "《" + songName + "》已加入网络收藏夹", Toast.LENGTH_SHORT).show();
            imageButton.setImageResource(R.drawable.favor);
            new AcceptFavorThread(songId, "F", OLBaseActivity.getAccountName()).start();
        });
        TextView songNameTextView = view.findViewById(R.id.ol_s_n);
        songNameTextView.setText(songName);
        songNameTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        songNameTextView.setHorizontallyScrolling(true);
        ((TextView) view.findViewById(R.id.ol_nandu)).setText("难度:" + songsList.get(i).get("degree"));
        double degree = (double) songsList.get(i).get("degree");
        ((TextView) view.findViewById(R.id.ol_items)).setText(songsList.get(i).get("items").toString());
        TextView winnerTextView = view.findViewById(R.id.ol_topuser);
        String winnerUser = (String) songsList.get(i).get("topUser");
        if (!Objects.equals(winnerUser, "(暂无冠军)")) {
            winnerTextView.setOnClickListener(v -> {
                Intent intent = new Intent(olMelodySelect, PopUserInfo.class);
                intent.putExtra("head", 1);
                intent.putExtra("userName", winnerUser);
                olMelodySelect.startActivity(intent);
            });
        } else {
            winnerTextView.setOnClickListener(null);
        }
        winnerTextView.setText(winnerUser);
        ((TextView) view.findViewById(R.id.ol_topscore)).setText("得分:" + songsList.get(i).get("topScore"));
        int songsTime = Integer.parseInt(songsList.get(i).get("length").toString());
        String songTimeHour = songsTime / 60 >= 10 ? String.valueOf(songsTime / 60) : "0" + songsTime / 60;
        String songTimeMinute = songsTime % 60 >= 10 ? String.valueOf(songsTime % 60) : "0" + songsTime % 60;
        ((TextView) view.findViewById(R.id.ol_length)).setText("时长:" + songTimeHour + ":" + songTimeMinute);
        ((TextView) view.findViewById(R.id.ol_update)).setText("冠军时间:" + songsList.get(i).get("update"));
        ((TextView) view.findViewById(R.id.ol_playcount)).setText("播放量:" + songsList.get(i).get("playCount"));
        if (!DeviceUtil.isX86()) {
            view.findViewById(R.id.ol_play_button).setOnClickListener(new OLMelodySongsPlayClick(
                    this, songName, songId, (Integer) songsList.get(i).get("topScore"), degree));
        } else {
            Toast.makeText(olMelodySelect, "您的设备不支持弹奏", Toast.LENGTH_SHORT).show();
        }
        view.findViewById(R.id.ol_play_waterfall).setOnClickListener(new OLMelodySongsPlayClick(
                this, songId, new Intent(olMelodySelect, WaterfallActivity.class)));
        return view;
    }
}
