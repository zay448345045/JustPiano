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

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.PopUserInfo;
import ly.pp.justpiano3.activity.ShowTopInfo;
import ly.pp.justpiano3.thread.PictureHandle;

public final class TopUserAdapter extends BaseAdapter {
    final ShowTopInfo showTopInfo;
    private final List<Map<String, Object>> peopleList;

    public TopUserAdapter(ShowTopInfo showTopInfo, int i, List<Map<String, Object>> list) {
        this.showTopInfo = showTopInfo;
        peopleList = list;
        showTopInfo.handler = new Handler(showTopInfo);
        showTopInfo.pictureHandle = new PictureHandle(showTopInfo.handler, 0);
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
            view = showTopInfo.layoutInflater.inflate(R.layout.ol_top_view, null);
        }
        view.setKeepScreenOn(true);
        TextView textView = view.findViewById(R.id.ol_position_top);
        TextView textView2 = view.findViewById(R.id.ol_user_top);
        TextView textView3 = view.findViewById(R.id.ol_score_top);
        TextView textView4 = view.findViewById(R.id.ol_nuns_top);
        ImageView imageView = view.findViewById(R.id.user_face);
        imageView.setTag(peopleList.get(i).get("faceID").toString());
        showTopInfo.pictureHandle.mo3027a(showTopInfo.jpapplication, imageView, showTopInfo.m3874a(showTopInfo));
        textView.setText(String.valueOf(showTopInfo.f4997m + i));
        String obj2 = peopleList.get(i).get("userName").toString();
        textView2.setText(obj2);
        ImageView imageView2 = view.findViewById(R.id.ol_user_sex);
        if (peopleList.get(i).get("userSex").toString().equals("f")) {
            imageView2.setImageResource(R.drawable.f);
        } else {
            imageView2.setImageResource(R.drawable.m);
        }
        int intValue = (Integer) peopleList.get(i).get("userScore");
        textView3.setTextColor(0xffff0000);
        switch (showTopInfo.head) {
            case 0:
                textView3.setText("冠军:" + peopleList.get(i).get("userNuns"));
                textView4.setText("总分:" + intValue);
                break;
            case 1:
                textView3.setText("总分:" + intValue);
                textView4.setText("冠军:" + peopleList.get(i).get("userNuns"));
                break;
            case 4:
                textView3.setText("等级:" + peopleList.get(i).get("userNuns"));
                textView4.setText("经验:" + intValue);
                break;
            case 7:
                textView3.setText("祝福:" + intValue);
                textView4.setVisibility(View.GONE);
                break;
            case 9:
                textView3.setText("贡献:" + intValue / 10);
                textView4.setText("等级:" + peopleList.get(i).get("userNuns"));
                break;
            case 10:
                textView4.setText("等级:" + peopleList.get(i).get("userNuns"));
                textView3.setText("" + intValue / 10 + "级" + ((intValue % 10)) + "阶");
                break;
        }
        view.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("head", 1);
            intent.putExtra("userKitiName", obj2);
            intent.setClass(showTopInfo, PopUserInfo.class);
            showTopInfo.startActivity(intent);
        });
        return view;
    }
}
