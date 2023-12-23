package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.SimpleUser;
import ly.pp.justpiano3.entity.User;

/**
 * 聊天处理
 *
 * @author as
 */
public class ChatUtil {

    public static final String CHAT_BLACK_LIST_STORE_KEY = "chatBlackList";

    private static List<SimpleUser> chatBlackList;

    public static List<SimpleUser> getChatBlackList(Context context) {
        if (chatBlackList == null) {
            chatBlackList = ChatUtil.getStoredChatBlackList(context);
        }
        return chatBlackList;
    }

    public static void chatBlackListAddUser(Context context, SimpleUser simpleUser) {
        if (chatBlackList == null) {
            chatBlackList = ChatUtil.getStoredChatBlackList(context);
        }
        chatBlackList.add(simpleUser);
        ChatUtil.saveChatBlackList(context);
    }

    public static void chatBlackListRemoveUser(Context context, String userName) {
        if (chatBlackList == null) {
            chatBlackList = ChatUtil.getStoredChatBlackList(context);
        }
        List<SimpleUser> simpleUserList = new ArrayList<>();
        for (SimpleUser simpleUser : chatBlackList) {
            if (!Objects.equals(simpleUser.getName(), userName)) {
                simpleUserList.add(simpleUser);
            }
        }
        chatBlackList = simpleUserList;
        ChatUtil.saveChatBlackList(context);
    }

    /**
     * 屏蔽聊天用户的按钮展示处理
     */
    public static void chatBlackButtonHandle(Context context, User user, Button chatBlackButton,
                                             Button chatBlackCancelButton, PopupWindow popupWindow) {
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
            chatBlackList = ChatUtil.getStoredChatBlackList(context);
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
            if (!TextUtils.isEmpty(chatBlackListJson)) {
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

    /**
     * 聊天记录存储
     */
    public static void chatsSaveHandle(Message message, Context context, String time) {
        if (GlobalSetting.INSTANCE.getSaveChatRecord()) {
            String date = DateUtil.format(DateUtil.now(), "yyyy-MM-dd聊天记录");
            Uri fileUri = FileUtil.INSTANCE.getOrCreateFileByUriFolder(context,
                    GlobalSetting.INSTANCE.getChatsSavePath(), "Chats", date + ".txt");
            if (fileUri == null) {
                Toast.makeText(context, "聊天记录文件获取失败", Toast.LENGTH_SHORT).show();
                return;
            }
            DocumentFile documentFile = FileUtil.INSTANCE.uriToDocumentFile(context, fileUri);
            if (documentFile != null && documentFile.exists()) {
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(fileUri, "wa")) {
                    writeMessageContent(message, time, outputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(fileUri, "w")) {
                    outputStream.write((date + ":\n").getBytes());
                    writeMessageContent(message, time, outputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeMessageContent(Message message, String time, OutputStream outputStream) throws IOException {
        String msg = message.getData().getString("M");
        if (msg != null && !msg.startsWith("//")) {
            if (message.getData().getInt("T") == OnlineProtocolType.MsgType.PRIVATE_MESSAGE) {
                outputStream.write((time + "[私]" + message.getData().getString("U") + ":" + msg + '\n').getBytes());
            } else if (message.getData().getInt("T") == OnlineProtocolType.MsgType.PUBLIC_MESSAGE) {
                outputStream.write((time + "[公]" + message.getData().getString("U") + ":" + msg + '\n').getBytes());
            } else if (message.getData().getInt("T") == OnlineProtocolType.MsgType.ALL_SERVER_MESSAGE) {
                outputStream.write((time + "[全服消息]" + message.getData().getString("U") + ":" + msg + '\n').getBytes());
            } else if (message.getData().getInt("T") == OnlineProtocolType.MsgType.SONG_RECOMMEND_MESSAGE) {
                outputStream.write((time + "[荐]" + message.getData().getString("U") + ":" + msg + '\n').getBytes());
            }
        }
    }
}
