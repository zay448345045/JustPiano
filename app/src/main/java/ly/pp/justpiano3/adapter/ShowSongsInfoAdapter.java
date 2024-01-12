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
import ly.pp.justpiano3.activity.online.PopUserInfo;
import ly.pp.justpiano3.activity.online.ShowSongsInfo;
import ly.pp.justpiano3.listener.ShowSongsInfoPlayClick;
import ly.pp.justpiano3.thread.AcceptFavorThread;
import ly.pp.justpiano3.utils.DeviceUtil;

public final class ShowSongsInfoAdapter extends BaseAdapter {
    public final ShowSongsInfo showSongsInfo;
    private List<Map<String, Object>> songList;

    public ShowSongsInfoAdapter(ShowSongsInfo showSongsInfo, List<Map<String, Object>> songList) {
        this.showSongsInfo = showSongsInfo;
        this.songList = songList;
    }

    public void updateSongList(List<Map<String, Object>> songList) {
        this.songList = songList;
    }

    @Override
    public int getCount() {
        return songList.size();
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
            view = showSongsInfo.layoutInflater.inflate(R.layout.ol_c_view, null);
        }
        String songName = songList.get(i).get("songName").toString().trim();
        String songId = (String) songList.get(i).get("songID");
        ImageButton imageButton = view.findViewById(R.id.ol_favor_b);
        imageButton.setImageResource(showSongsInfo.keywords.equals("F") ? R.drawable.favor : R.drawable.favor_1);
        imageButton.setOnClickListener(v -> {
            if (showSongsInfo.keywords.equals("F")) {
                Toast.makeText(showSongsInfo.getBaseContext(), "《" + songName + "》已移出网络收藏夹", Toast.LENGTH_SHORT).show();
                showSongsInfo.updateSongInfo(i);
                new AcceptFavorThread(songId, "U", OLBaseActivity.getAccountName()).start();
                return;
            }
            Toast.makeText(showSongsInfo.getBaseContext(), "《" + songName + "》已加入网络收藏夹", Toast.LENGTH_SHORT).show();
            imageButton.setImageResource(R.drawable.favor);
            new AcceptFavorThread(songId, "F", OLBaseActivity.getAccountName()).start();
        });
        TextView songNameTextView = view.findViewById(R.id.ol_s_n);
        songNameTextView.setText(songName);
        songNameTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        songNameTextView.setHorizontallyScrolling(true);
        ((TextView) view.findViewById(R.id.ol_nandu)).setText("难度:" + songList.get(i).get("degree"));
        double degree = (double) songList.get(i).get("degree");
        ((TextView) view.findViewById(R.id.ol_items)).setText(songList.get(i).get("items").toString());
        TextView winnerUserTextView = view.findViewById(R.id.ol_topuser);
        String winnerUser = (String) (songList.get(i).get("topUser"));
        if (!Objects.equals(winnerUser, "(暂无冠军)")) {
            winnerUserTextView.setOnClickListener(v -> {
                Intent intent = new Intent(showSongsInfo, PopUserInfo.class);
                intent.putExtra("head", 1);
                intent.putExtra("userName", winnerUser);
                showSongsInfo.startActivity(intent);
            });
        } else {
            winnerUserTextView.setOnClickListener(null);
        }
        winnerUserTextView.setText(winnerUser);
        ((TextView) view.findViewById(R.id.ol_topscore)).setText("高分:" + songList.get(i).get("topScore"));
        ((TextView) view.findViewById(R.id.ol_playcount)).setText("播放量:" + songList.get(i).get("playCount"));
        int songsTime = Integer.parseInt(songList.get(i).get("length").toString());
        String songTimeHour = songsTime / 60 >= 10 ? String.valueOf(songsTime / 60) : "0" + songsTime / 60;
        String songTimeMinute = songsTime % 60 >= 10 ? String.valueOf(songsTime % 60) : "0" + songsTime % 60;
        ((TextView) view.findViewById(R.id.ol_length)).setText("时长:" + songTimeHour + ":" + songTimeMinute);
        ((TextView) view.findViewById(R.id.ol_update)).setText("冠军时间:" + songList.get(i).get("update"));
        if (!DeviceUtil.isX86()) {
            view.findViewById(R.id.ol_play_button).setOnClickListener(new ShowSongsInfoPlayClick(
                    this, songName, songId, Integer.parseInt((String) songList.get(i).get("topScore")), degree));
        } else {
            Toast.makeText(showSongsInfo, "您的设备不支持弹奏", Toast.LENGTH_SHORT).show();
        }
        view.findViewById(R.id.ol_play_waterfall).setOnClickListener(new ShowSongsInfoPlayClick(
                this, songId, new Intent(showSongsInfo, WaterfallActivity.class)));
        return view;
    }
}
