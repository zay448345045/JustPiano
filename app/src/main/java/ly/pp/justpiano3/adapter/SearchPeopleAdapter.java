package ly.pp.justpiano3.adapter;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.PopUserInfo;
import ly.pp.justpiano3.activity.online.SearchSongs;
import ly.pp.justpiano3.thread.PictureHandle;

public final class SearchPeopleAdapter extends BaseAdapter {
    final SearchSongs searchSongs;
    private final List<Map<String, Object>> peopleList;

    public SearchPeopleAdapter(SearchSongs searchSongs, List<Map<String, Object>> list) {
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
        TextView userNameTextView = view.findViewById(R.id.ol_user_top);
        TextView scoreTextView = view.findViewById(R.id.ol_score_top);
        TextView numTextView = view.findViewById(R.id.ol_nuns_top);
        ImageView avatarImageView = view.findViewById(R.id.user_face);
        ((TextView) view.findViewById(R.id.ol_position_top)).setText(String.valueOf(i));
        avatarImageView.setTag(peopleList.get(i).get("faceID").toString());
        searchSongs.pictureHandle.setBitmap(avatarImageView, searchSongs.loadNailFace(searchSongs));
        String userName = peopleList.get(i).get("userName").toString();
        userNameTextView.setText(userName);
        ImageView genderImageView = view.findViewById(R.id.ol_user_sex);
        genderImageView.setImageResource(Objects.equals(peopleList.get(i).get("userSex"), "f") ? R.drawable.f : R.drawable.m);
        scoreTextView.setText("总分:" + peopleList.get(i).get("userScore"));
        numTextView.setText("冠军:" + peopleList.get(i).get("userNuns"));
        view.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("head", 1);
            intent.putExtra("userKitiName", userName);
            intent.setClass(searchSongs, PopUserInfo.class);
            searchSongs.startActivity(intent);
        });
        return view;
    }
}
