package ly.pp.justpiano3;

import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public final class ShowSongsInfoAdapter extends BaseAdapter {
    final ShowSongsInfo showSongsInfo;
    private List<HashMap> songsList;

    ShowSongsInfoAdapter(ShowSongsInfo showSongsInfo, List<HashMap> list) {
        this.showSongsInfo = showSongsInfo;
        songsList = list;
    }

    final void mo3500a(List<HashMap> list) {
        songsList = list;
    }

    @Override
    public final int getCount() {
        return songsList.size();
    }

    @Override
    public final Object getItem(int i) {
        return i;
    }

    @Override
    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = showSongsInfo.layoutInflater.inflate(R.layout.ol_c_view, null);
        }
        view.setKeepScreenOn(true);
        String trim = songsList.get(i).get("songName").toString().trim();
        String valueOf = (String) songsList.get(i).get("songID");
        ImageButton imageButton = view.findViewById(R.id.ol_favor_b);
        if (showSongsInfo.keywords.equals("F")) {
            imageButton.setImageResource(R.drawable.favor);
        } else {
            imageButton.setImageResource(R.drawable.favor_1);
        }
        imageButton.setOnClickListener(v -> {
            if (showSongsInfo.keywords.equals("F")) {
                Toast.makeText(showSongsInfo.getBaseContext(), "《" + trim + "》已移出网络收藏夹", Toast.LENGTH_SHORT).show();
                showSongsInfo.mo2976a(i);
                new AcceptFavorThread(showSongsInfo, valueOf, "U", showSongsInfo.jpapplication.getAccountName()).start();
                return;
            }
            Toast.makeText(showSongsInfo.getBaseContext(), "《" + trim + "》已加入网络收藏夹", Toast.LENGTH_SHORT).show();
            imageButton.setImageResource(R.drawable.favor);
            new AcceptFavorThread(showSongsInfo, valueOf, "F", showSongsInfo.jpapplication.getAccountName()).start();
        });
        TextView songName = view.findViewById(R.id.ol_s_n);
        songName.setText(trim);
        songName.setMovementMethod(ScrollingMovementMethod.getInstance());
        songName.setHorizontallyScrolling(true);
        ((TextView) view.findViewById(R.id.ol_nandu)).setText("难度:" + songsList.get(i).get("degree"));
        double d = (double) songsList.get(i).get("degree");
        ((TextView) view.findViewById(R.id.ol_items)).setText(songsList.get(i).get("items").toString());
        TextView textView = view.findViewById(R.id.ol_topuser);
        String str = (String) (songsList.get(i).get("topUser"));
        textView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("head", 1);
            intent.putExtra("userKitiName", str);
            intent.setClass(showSongsInfo, PopUserInfo.class);
            showSongsInfo.startActivity(intent);
        });
        textView.setText(str);
        ((TextView) view.findViewById(R.id.ol_topscore)).setText("高分:" + songsList.get(i).get("topScore"));
        ((TextView) view.findViewById(R.id.ol_playcount)).setText("播放量:" + songsList.get(i).get("playCount"));
        int songsTime = Integer.parseInt(songsList.get(i).get("length").toString());
        String str1 = songsTime / 60 >= 10 ? "" + songsTime / 60 : "0" + songsTime / 60;
        String str2 = songsTime % 60 >= 10 ? "" + songsTime % 60 : "0" + songsTime % 60;
        ((TextView) view.findViewById(R.id.ol_length)).setText("时长:" + str1 + ":" + str2);
        ((TextView) view.findViewById(R.id.ol_update)).setText("冠军时间:" + songsList.get(i).get("update"));
        view.findViewById(R.id.ol_play_button).setOnClickListener(new ShowSongsInfoPlayClick(this, trim, valueOf, Integer.parseInt((String) songsList.get(i).get("topScore")), d));
        return view;
    }
}
