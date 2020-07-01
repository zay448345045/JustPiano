package ly.pp.justpiano3;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

final class FriendMailPageAdapter extends BaseAdapter {
    private final FriendMailPage friendMailPage;
    private List<JSONObject> list;

    FriendMailPageAdapter(FriendMailPage friendMailPage, List<JSONObject> arrayList) {
        this.friendMailPage = friendMailPage;
        list = arrayList;
    }

    final void mo3634a(List<JSONObject> arrayList) {
        list = arrayList;
    }

    @Override
    public final int getCount() {
        return list.size();
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
        JSONObject jSONObject;
        TextView textView;
        TextView textView2;
        Button button;
        Button button2;
        if (friendMailPage.f4024f.equals("F")) {
            if (view == null) {
                view = friendMailPage.getLayoutInflater().inflate(R.layout.friend_view, null);
            }
            jSONObject = list.get(i);
            textView = view.findViewById(R.id.friend_name);
            textView2 = view.findViewById(R.id.friend_level);
            ImageView imageView = view.findViewById(R.id.friend_sex);
            button = view.findViewById(R.id.friend_info);
            button2 = view.findViewById(R.id.friend_dele);
            Button button3 = view.findViewById(R.id.friend_send);
            try {
                String string = jSONObject.getString("N");
                textView.setText(string);
                textView2.setText("LV." + jSONObject.getInt("L"));
                if (jSONObject.getString("S").equals("f")) {
                    imageView.setImageResource(R.drawable.f);
                } else {
                    imageView.setImageResource(R.drawable.m);
                }
                button.setOnClickListener(v -> friendMailPage.mo2683a(0, i, string, null));
                button2.setOnClickListener(v -> friendMailPage.mo2683a(1, i, string, null));
                button3.setOnClickListener(v -> friendMailPage.mo2683a(2, i, string, null));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (friendMailPage.f4024f.equals("M")) {
            if (view == null) {
                view = friendMailPage.getLayoutInflater().inflate(R.layout.ol_mail_view, null);
            }
            jSONObject = list.get(i);
            textView = view.findViewById(R.id.ol_from_user);
            textView2 = view.findViewById(R.id.ol_mail_msg);
            TextView textView3 = view.findViewById(R.id.ol_mail_time);
            button = view.findViewById(R.id.mail_send);
            button2 = view.findViewById(R.id.mail_dele);
            try {
                String string2 = jSONObject.getString("F");
                String string3 = jSONObject.getString("M");
                int i2 = jSONObject.has("type") ? jSONObject.getInt("type") : 0;
                TextView textView4 = view.findViewById(R.id.fromTo);
                if (i2 == 0) {
                    textView4.setText("From:");
                } else if (i2 == 1) {
                    textView4.setText("To:");
                }
                CharSequence string4 = jSONObject.getString("T");
                textView.setText(string2);
                if ("".equals(string3)) {
                    textView2.setText(string2 + " 请求加你为好友");
                    button.setText("同意");
                } else {
                    textView2.setText(string3);
                    button.setText("回复");
                }
                button.setOnClickListener(v -> friendMailPage.mo2683a(3, i, string2, string3));
                textView3.setText(string4);
                button2.setOnClickListener(v -> friendMailPage.mo2683a(4, i, string2, null));
                textView.setOnClickListener(v -> friendMailPage.mo2683a(0, i, string2, null));
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return view;
    }
}
