package ly.pp.justpiano3.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.UserListPage;
import ly.pp.justpiano3.entity.SimpleUser;
import ly.pp.justpiano3.utils.ChatUtil;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;

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
        // 这里使用等级标签显示时间
        ((TextView) view.findViewById(R.id.friend_name)).setText(simpleUser.getName());
        ((TextView) view.findViewById(R.id.friend_level)).setText(
                "加入屏蔽时间：" + DateUtil.format(simpleUser.getDate(), DateUtil.TEMPLATE_DEFAULT));
        ((ImageView) view.findViewById(R.id.friend_sex)).setImageResource(
                Objects.equals(simpleUser.getGender(), "f") ? R.drawable.f : R.drawable.m);
        view.findViewById(R.id.friend_dele).setOnClickListener(v -> new JPDialogBuilder(userListPage)
                .setTitle("删除用户")
                .setMessage("从聊天屏蔽名单中刪除用户[" + simpleUser.getName() + "]?")
                .setFirstButton("确定", (dialogInterface, i1) -> {
                    ChatUtil.chatBlackListRemoveUser(userListPage, simpleUser.getName());
                    userListPage.listView.setAdapter(new UserListPageAdapter(userListPage,
                            ChatUtil.getChatBlackList(userListPage)));
                    dialogInterface.dismiss();
                })
                .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog());
        return view;
    }
}
