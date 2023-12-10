package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.UserListPageAdapter;
import ly.pp.justpiano3.utils.ChatBlackUserUtil;
import ly.pp.justpiano3.view.JPProgressBar;

public class UserListPage extends BaseActivity {
    public ListView listView;
    public JPProgressBar jpProgressBar;

    @Override
    public void onBackPressed() {
        jpProgressBar.dismiss();
        Intent intent = new Intent();
        intent.setClass(this, OLMainMode.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ol_user_list);
        listView = findViewById(R.id.list_view);
        listView.setCacheColorHint(Color.TRANSPARENT);
        TextView textView = findViewById(R.id.ol_top_title);
        textView.setText("聊天屏蔽用户名单");
        TextView descTextView = findViewById(R.id.ol_top_tips);
        descTextView.setVisibility(View.VISIBLE);
        descTextView.setText("此列表下的用户，在接收到Ta的聊天消息时会自动屏蔽，对方无感知。\n屏蔽聊天列表添加用户方法：在房间聊天界面点击人物形象，在小弹窗中选择屏蔽聊天。");
        jpProgressBar = new JPProgressBar(this);
        listView.setAdapter(new UserListPageAdapter(this, ChatBlackUserUtil.getChatBlackList(this)));
    }
}
