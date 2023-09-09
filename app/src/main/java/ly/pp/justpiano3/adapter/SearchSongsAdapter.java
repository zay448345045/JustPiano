package ly.pp.justpiano3.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.PopUserInfo;
import ly.pp.justpiano3.activity.SearchSongs;
import ly.pp.justpiano3.activity.WaterfallActivity;
import ly.pp.justpiano3.listener.SearchSongsPlayClick;
import ly.pp.justpiano3.thread.AcceptFavorThread;

import java.util.HashMap;
import java.util.List;

public final class SearchSongsAdapter extends BaseAdapter {
    public final SearchSongs searchSongs;
    private final List<HashMap> songsList;

    public SearchSongsAdapter(SearchSongs searchSongs, int i, List<HashMap> list) {
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
        view.setKeepScreenOn(true);
        String trim = songsList.get(i).get("songName").toString().trim();
        String songId = (String) songsList.get(i).get("songID");
        ImageButton imageButton = view.findViewById(R.id.ol_favor_b);
        imageButton.setImageResource(R.drawable.favor_1);
        imageButton.setOnClickListener(v -> {
            Toast.makeText(searchSongs.getBaseContext(), "《" + trim + "》已加入网络收藏夹", Toast.LENGTH_SHORT).show();
            imageButton.setImageResource(R.drawable.favor);
            new AcceptFavorThread(searchSongs, songId, "F", searchSongs.jpapplication.getAccountName()).start();
        });
        ((TextView) view.findViewById(R.id.ol_s_n)).setText(trim);
        ((TextView) view.findViewById(R.id.ol_nandu)).setText("难度:" + songsList.get(i).get("degree"));
        double d = (double) songsList.get(i).get("degree");
        ((TextView) view.findViewById(R.id.ol_items)).setText(songsList.get(i).get("items").toString());
        TextView textView = view.findViewById(R.id.ol_topuser);
        String str = songsList.get(i).get("topUser").toString();
        if (!str.equals("(暂无冠军)")) {
            textView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra("head", 1);
                intent.putExtra("userKitiName", str);
                intent.setClass(searchSongs, PopUserInfo.class);
                searchSongs.startActivity(intent);
            });
        } else {
            textView.setOnClickListener(null);
        }
        textView.setText(str);
        ((TextView) view.findViewById(R.id.ol_topscore)).setText("高分:" + songsList.get(i).get("topScore"));
        ((TextView) view.findViewById(R.id.ol_playcount)).setText("播放量:" + songsList.get(i).get("playCount"));
        int songsTime = Integer.parseInt(songsList.get(i).get("length").toString());
        String str1 = songsTime / 60 >= 10 ? "" + songsTime / 60 : "0" + songsTime / 60;
        String str2 = songsTime % 60 >= 10 ? "" + songsTime % 60 : "0" + songsTime % 60;
        ((TextView) view.findViewById(R.id.ol_length)).setText("时长:" + str1 + ":" + str2);
        ((TextView) view.findViewById(R.id.ol_update)).setText("冠军时间:" + songsList.get(i).get("update"));
        view.findViewById(R.id.ol_play_button).setOnClickListener(new SearchSongsPlayClick(this, trim, songId, Integer.parseInt((String) songsList.get(i).get("topScore")), d));
        ImageView waterFallImageView = view.findViewById(R.id.ol_play_waterfall);
        waterFallImageView.setOnClickListener(new SearchSongsPlayClick(this, songId, new Intent().setClass(searchSongs, WaterfallActivity.class)));
        return view;
    }
}
