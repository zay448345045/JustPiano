package ly.pp.justpiano3.utils;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.User;
import ly.pp.justpiano3.entity.SimpleUser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 屏蔽聊天处理
 *
 * @author as
 */
public class ChatBlackUserUtil {

    public static final String CHAT_BLACK_LIST_STORE_KEY = "chatBlackList";

    /**
     * 屏蔽聊天用户的按钮展示处理
     */
    public static void chatBlackButtonHandle(User user, JPApplication jpApplication,
                                             Button chatBlackButton, Button chatBlackCancelButton, PopupWindow popupWindow) {
        if (isUserInChatBlackList(jpApplication.getChatBlackList(), user.getPlayerName())) {
            chatBlackCancelButton.setVisibility(View.VISIBLE);
            chatBlackButton.setVisibility(View.GONE);
            chatBlackCancelButton.setOnClickListener(v -> {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    jpApplication.chatBlackListRemoveUser(user.getPlayerName());
                }
            });
        } else {
            chatBlackButton.setVisibility(View.VISIBLE);
            chatBlackCancelButton.setVisibility(View.GONE);
            chatBlackButton.setOnClickListener(v -> {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    jpApplication.chatBlackListAddUser(new SimpleUser(user.getSex(), user.getPlayerName(), user.getLevel()));
                }
            });
        }
    }

    /**
     * 用户是否在屏蔽名单内
     */
    public static boolean isUserInChatBlackList(List<SimpleUser> chatBlackList, String userName) {
        for (SimpleUser simpleUser : chatBlackList) {
            if (Objects.equals(simpleUser.getName(), userName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 持久化存储用户屏蔽聊天名单
     */
    public static void saveChatBlackList(SharedPreferences accountListSharedPreferences, List<SimpleUser> chatBlackList) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (SimpleUser user : chatBlackList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("gender", user.getGender());
                jsonObject.put("lv", user.getLv());
                jsonObject.put("name", user.getName());
                jsonArray.put(jsonObject);
            }
            SharedPreferences.Editor edit = accountListSharedPreferences.edit();
            edit.putString(CHAT_BLACK_LIST_STORE_KEY, jsonArray.toString());
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从持久化存储中获取用户屏蔽聊天名单
     */
    public static List<SimpleUser> getStoredChatBlackList(SharedPreferences accountListSharedPreferences) {
        List<SimpleUser> chatBlackList = new ArrayList<>();
        try {
            String chatBlackListJson = accountListSharedPreferences.getString(CHAT_BLACK_LIST_STORE_KEY, "");
            if (!StringUtil.isNullOrEmpty(chatBlackListJson)) {
                JSONArray jsonArray = new JSONArray(chatBlackListJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SimpleUser simpleUser = new SimpleUser(jsonObject.getString("gender"),
                            jsonObject.getString("name"), jsonObject.getInt("lv"));
                    chatBlackList.add(simpleUser);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatBlackList;
    }
}
