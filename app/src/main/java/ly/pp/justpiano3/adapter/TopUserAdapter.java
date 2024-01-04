package ly.pp.justpiano3.adapter;

import android.content.Intent;
import android.graphics.Color;
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
import ly.pp.justpiano3.activity.online.ShowTopInfo;
import ly.pp.justpiano3.thread.PictureHandle;

public final class TopUserAdapter extends BaseAdapter {
    final ShowTopInfo showTopInfo;
    private final List<Map<String, Object>> list;

    public TopUserAdapter(ShowTopInfo showTopInfo, List<Map<String, Object>> list) {
        this.showTopInfo = showTopInfo;
        this.list = list;
        showTopInfo.handler = new Handler(showTopInfo);
        showTopInfo.pictureHandle = new PictureHandle(showTopInfo.handler, 0);
    }

    @Override
    public int getCount() {
        return list.size();
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
            view = showTopInfo.layoutInflater.inflate(R.layout.ol_top_view, null);
        }
        TextView positionTextView = view.findViewById(R.id.ol_position_top);
        TextView userNameTextView = view.findViewById(R.id.ol_user_top);
        TextView scoreTextView = view.findViewById(R.id.ol_score_top);
        TextView numTextView = view.findViewById(R.id.ol_nuns_top);
        ImageView avatarImageView = view.findViewById(R.id.user_face);
        avatarImageView.setTag(list.get(i).get("faceID").toString());
        showTopInfo.pictureHandle.setBitmap(avatarImageView, showTopInfo.setDefaultAvatar(showTopInfo));
        positionTextView.setText(String.valueOf(showTopInfo.position + i));
        String userName = list.get(i).get("userName").toString();
        userNameTextView.setText(userName);
        ImageView genderImageView = view.findViewById(R.id.ol_user_sex);
        genderImageView.setImageResource(Objects.equals(list.get(i).get("userSex"), "f") ? R.drawable.f : R.drawable.m);
        int intValue = (Integer) list.get(i).get("userScore");
        scoreTextView.setTextColor(Color.RED);
        switch (showTopInfo.head) {
            case 0 -> {
                scoreTextView.setText("冠军:" + list.get(i).get("userNuns"));
                numTextView.setText("总分:" + intValue);
            }
            case 1 -> {
                scoreTextView.setText("总分:" + intValue);
                numTextView.setText("冠军:" + list.get(i).get("userNuns"));
            }
            case 4 -> {
                scoreTextView.setText("等级:" + list.get(i).get("userNuns"));
                numTextView.setText("经验:" + intValue);
            }
            case 7 -> {
                scoreTextView.setText("祝福:" + intValue);
                numTextView.setVisibility(View.GONE);
            }
            case 9 -> {
                scoreTextView.setText("贡献:" + intValue / 10);
                numTextView.setText("等级:" + list.get(i).get("userNuns"));
            }
            case 10 -> {
                numTextView.setText("等级:" + list.get(i).get("userNuns"));
                scoreTextView.setText(String.valueOf(intValue / 10) + "级" + ((intValue % 10)) + "阶");
            }
        }
        view.setOnClickListener(v -> {
            Intent intent = new Intent(showTopInfo, PopUserInfo.class);
            intent.putExtra("head", 1);
            intent.putExtra("userKitiName", userName);
            showTopInfo.startActivity(intent);
        });
        return view;
    }
}
