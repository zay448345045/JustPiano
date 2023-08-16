package ly.pp.justpiano3.adapter;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ly.pp.justpiano3.thread.PictureHandle;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.PopUserInfo;
import ly.pp.justpiano3.activity.SearchSongs;

import java.util.HashMap;
import java.util.List;

public final class SearchPeopleAdapter extends BaseAdapter {
    final SearchSongs searchSongs;
    private final List<HashMap> peopleList;

    public SearchPeopleAdapter(SearchSongs searchSongs, List<HashMap> list) {
        this.searchSongs = searchSongs;
        peopleList = list;
        searchSongs.searchSongsHandler = new Handler(searchSongs);
        searchSongs.pictureHandle = new PictureHandle(searchSongs.searchSongsHandler, 0);
    }

    @Override
    public int getCount() {
        return peopleList.size();
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
            view = searchSongs.layoutinflater.inflate(R.layout.ol_top_view, null);
        }
        view.setKeepScreenOn(true);
        TextView textView = view.findViewById(R.id.ol_user_top);
        TextView textView2 = view.findViewById(R.id.ol_score_top);
        TextView textView3 = view.findViewById(R.id.ol_nuns_top);
        ImageView imageView = view.findViewById(R.id.user_face);
        ((TextView) view.findViewById(R.id.ol_position_top)).setText(String.valueOf(i));
        imageView.setTag(peopleList.get(i).get("faceID").toString());
        searchSongs.pictureHandle.mo3027a(searchSongs.jpapplication, imageView, searchSongs.m3831a(searchSongs));
        String str = peopleList.get(i).get("userName").toString();
        textView.setText(str);
        ImageView imageView2 = view.findViewById(R.id.ol_user_sex);
        if (peopleList.get(i).get("userSex").toString().equals("f")) {
            imageView2.setImageResource(R.drawable.f);
        } else {
            imageView2.setImageResource(R.drawable.m);
        }
        textView2.setText("总分:" + peopleList.get(i).get("userScore"));
        textView3.setText("冠军:" + peopleList.get(i).get("userNuns"));
        view.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("head", 1);
            intent.putExtra("userKitiName", str);
            intent.setClass(searchSongs, PopUserInfo.class);
            searchSongs.startActivity(intent);
        });
        return view;
    }
}
