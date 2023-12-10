package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.entity.SimpleUser;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.view.JPPopupWindow;

/**
 * 屏蔽聊天处理
 *
 * @author as
 */
public class ChatBlackUserUtil {

    public static final String CHAT_BLACK_LIST_STORE_KEY = "chatBlackList";

    private static List<SimpleUser> chatBlackList;

    public static List<SimpleUser> getChatBlackList(Context context) {
        if (chatBlackList == null) {
            chatBlackList = ChatBlackUserUtil.getStoredChatBlackList(context);
        }
        return chatBlackList;
    }

    public static void chatBlackListAddUser(Context context, SimpleUser simpleUser) {
        if (chatBlackList == null) {
            chatBlackList = ChatBlackUserUtil.getStoredChatBlackList(context);
        }
        chatBlackList.add(simpleUser);
        ChatBlackUserUtil.saveChatBlackList(context);
    }

    public static void chatBlackListRemoveUser(Context context, String userName) {
        if (chatBlackList == null) {
            chatBlackList = ChatBlackUserUtil.getStoredChatBlackList(context);
        }
        List<SimpleUser> simpleUserList = new ArrayList<>();
        for (SimpleUser simpleUser : chatBlackList) {
            if (!Objects.equals(simpleUser.getName(), userName)) {
                simpleUserList.add(simpleUser);
            }
        }
        chatBlackList = simpleUserList;
        ChatBlackUserUtil.saveChatBlackList(context);
    }

    /**
     * 屏蔽聊天用户的按钮展示处理
     */
    public static void chatBlackButtonHandle(Context context, User user, Button chatBlackButton,
                                             Button chatBlackCancelButton, JPPopupWindow popupWindow) {
        if (isUserInChatBlackList(context, user.getPlayerName())) {
            chatBlackCancelButton.setVisibility(View.VISIBLE);
            chatBlackButton.setVisibility(View.GONE);
            chatBlackCancelButton.setOnClickListener(v -> {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    chatBlackListRemoveUser(context, user.getPlayerName());
                }
            });
        } else {
            chatBlackButton.setVisibility(View.VISIBLE);
            chatBlackCancelButton.setVisibility(View.GONE);
            chatBlackButton.setOnClickListener(v -> {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    chatBlackListAddUser(context, new SimpleUser(user.getSex(), user.getPlayerName(), user.getLevel(), DateUtil.now()));
                }
            });
        }
    }

    /**
     * 用户是否在屏蔽名单内
     */
    public static boolean isUserInChatBlackList(Context context, String userName) {
        if (chatBlackList == null) {
            chatBlackList = ChatBlackUserUtil.getStoredChatBlackList(context);
        }
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
    private static void saveChatBlackList(Context context) {
        SharedPreferences accountListSharedPreferences = context.getSharedPreferences("account_list", Context.MODE_PRIVATE);
        try {
            JSONArray jsonArray = new JSONArray();
            for (SimpleUser user : chatBlackList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("gender", user.getGender());
                jsonObject.put("time", DateUtil.format(user.getDate()));
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
    private static List<SimpleUser> getStoredChatBlackList(Context context) {
        SharedPreferences accountListSharedPreferences = context.getSharedPreferences("account_list", Context.MODE_PRIVATE);
        List<SimpleUser> chatBlackList = new ArrayList<>();
        try {
            String chatBlackListJson = accountListSharedPreferences.getString(CHAT_BLACK_LIST_STORE_KEY, "");
            if (!StringUtil.isNullOrEmpty(chatBlackListJson)) {
                JSONArray jsonArray = new JSONArray(chatBlackListJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SimpleUser simpleUser = new SimpleUser(jsonObject.getString("gender"),
                            jsonObject.getString("name"), 0, DateUtil.parseDontThrow(jsonObject.getString("time"), DateUtil.TEMPLATE_DEFAULT));
                    chatBlackList.add(simpleUser);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatBlackList;
    }
}
