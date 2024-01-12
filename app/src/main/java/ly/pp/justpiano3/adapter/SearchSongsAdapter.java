package ly.pp.justpiano3.adapter;

import android.content.Intent;
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
import ly.pp.justpiano3.activity.online.PopUserInfo;
import ly.pp.justpiano3.activity.online.SearchSongs;
import ly.pp.justpiano3.listener.SearchSongsPlayClick;
import ly.pp.justpiano3.thread.AcceptFavorThread;
import ly.pp.justpiano3.utils.DeviceUtil;

public final class SearchSongsAdapter extends BaseAdapter {
    public final SearchSongs searchSongs;
    private final List<Map<String, Object>> songsList;

    public SearchSongsAdapter(SearchSongs searchSongs, int i, List<Map<String, Object>> list) {
        this.searchSongs = searchSongs;
        songsList = list;
    }

    @Override
    public int getCount() {
        return songsList.size();
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
            view = searchSongs.layoutinflater.inflate(R.layout.ol_c_view, null);
        }
        String songName = songsList.get(i).get("songName").toString().trim();
        String songId = (String) songsList.get(i).get("songID");
        ImageButton imageButton = view.findViewById(R.id.ol_favor_b);
        imageButton.setImageResource(R.drawable.favor_1);
        imageButton.setOnClickListener(v -> {
            Toast.makeText(searchSongs.getBaseContext(), "《" + songName + "》已加入网络收藏夹", Toast.LENGTH_SHORT).show();
            imageButton.setImageResource(R.drawable.favor);
            new AcceptFavorThread(songId, "F", OLBaseActivity.getAccountName()).start();
        });
        ((TextView) view.findViewById(R.id.ol_s_n)).setText(songName);
        ((TextView) view.findViewById(R.id.ol_nandu)).setText("难度:" + songsList.get(i).get("degree"));
        double degree = (double) songsList.get(i).get("degree");
        ((TextView) view.findViewById(R.id.ol_items)).setText(songsList.get(i).get("items").toString());
        TextView textView = view.findViewById(R.id.ol_topuser);
        String winnerUser = (String) songsList.get(i).get("topUser");
        if (!Objects.equals(winnerUser, "(暂无冠军)")) {
            textView.setOnClickListener(v -> {
                Intent intent = new Intent(searchSongs, PopUserInfo.class);
                intent.putExtra("head", 1);
                intent.putExtra("userName", winnerUser);
                searchSongs.startActivity(intent);
            });
        } else {
            textView.setOnClickListener(null);
        }
        textView.setText(winnerUser);
        ((TextView) view.findViewById(R.id.ol_topscore)).setText("高分:" + songsList.get(i).get("topScore"));
        ((TextView) view.findViewById(R.id.ol_playcount)).setText("播放量:" + songsList.get(i).get("playCount"));
        int songsTime = Integer.parseInt(songsList.get(i).get("length").toString());
        String songTimeHour = songsTime / 60 >= 10 ? String.valueOf(songsTime / 60) : "0" + songsTime / 60;
        String songTimeMinute = songsTime % 60 >= 10 ? String.valueOf(songsTime % 60) : "0" + songsTime % 60;
        ((TextView) view.findViewById(R.id.ol_length)).setText("时长:" + songTimeHour + ":" + songTimeMinute);
        ((TextView) view.findViewById(R.id.ol_update)).setText("冠军时间:" + songsList.get(i).get("update"));
        if (!DeviceUtil.isX86()) {
            view.findViewById(R.id.ol_play_button).setOnClickListener(new SearchSongsPlayClick(
                    this, songName, songId, Integer.parseInt((String) songsList.get(i).get("topScore")), degree));
        } else {
            Toast.makeText(searchSongs, "您的设备不支持弹奏", Toast.LENGTH_SHORT).show();
        }
        view.findViewById(R.id.ol_play_waterfall).setOnClickListener(new SearchSongsPlayClick(
                this, songId, new Intent(searchSongs, WaterfallActivity.class)));
        return view;
    }
}
