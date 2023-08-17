package ly.pp.justpiano3.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.UserListPage;
import ly.pp.justpiano3.entity.SimpleUser;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.utils.DateUtil;

import java.util.List;

public final class UserListPageAdapter extends BaseAdapter {
    private final UserListPage userListPage;
    private final List<SimpleUser> userList;

    public UserListPageAdapter(UserListPage userListPage, List<SimpleUser> userList) {
        this.userListPage = userListPage;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
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
        SimpleUser simpleUser = userList.get(i);
        if (view == null) {
            view = userListPage.getLayoutInflater().inflate(R.layout.friend_view, null);
        }
        TextView nameTextView = view.findViewById(R.id.friend_name);
        // 这里使用等级标签显示时间
        TextView timeTextView = view.findViewById(R.id.friend_level);
        ImageView genderImageView = view.findViewById(R.id.friend_sex);
        Button deleteButton = view.findViewById(R.id.friend_dele);
        nameTextView.setText(simpleUser.getName());
        timeTextView.setText("加入屏蔽时间：" + DateUtil.format(simpleUser.getDate(), DateUtil.TEMPLATE_DEFAULT));
        if (simpleUser.getGender().equals("f")) {
            genderImageView.setImageResource(R.drawable.f);
        } else {
            genderImageView.setImageResource(R.drawable.m);
        }
        deleteButton.setOnClickListener(v -> new JPDialog(userListPage)
                .setTitle("删除用户")
                .setMessage("从聊天屏蔽名单中刪除用户[" + simpleUser.getName() + "]?")
                .setFirstButton("确定", (dialogInterface, i1) -> {
                    userListPage.jpApplication.chatBlackListRemoveUser(simpleUser.getName());
                    userListPage.listView.setAdapter(new UserListPageAdapter(userListPage, userListPage.jpApplication.getChatBlackList()));
                    dialogInterface.dismiss();
                })
                .setSecondButton("取消", new DialogDismissClick()).showDialog());
        return view;
    }
}
