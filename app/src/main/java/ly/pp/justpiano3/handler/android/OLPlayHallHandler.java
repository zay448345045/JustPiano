package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.widget.RadioButton;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Objects;

import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.OLMainMode;
import ly.pp.justpiano3.activity.online.OLPlayHall;
import ly.pp.justpiano3.activity.online.OLPlayKeyboardRoom;
import ly.pp.justpiano3.activity.online.OLPlayRoom;
import ly.pp.justpiano3.activity.local.PianoPlay;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.RoomModeEnum;
import ly.pp.justpiano3.utils.ChatUtil;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.DialogUtil;
import ly.pp.justpiano3.utils.EncryptUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.SoundEffectPlayUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import protobuf.dto.OnlineClTestDTO;
import protobuf.dto.OnlineEnterRoomDTO;
import protobuf.dto.OnlineSetUserInfoDTO;

public final class OLPlayHallHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLPlayHallHandler(OLPlayHall olPlayHall) {
        weakReference = new WeakReference<>(olPlayHall);
    }

    @Override
    public void handleMessage(Message message) {
        OLPlayHall olPlayHall = (OLPlayHall) weakReference.get();
        switch (message.what) {
            case 1 -> post(() -> {
                if (olPlayHall.msgList.size() > Consts.MAX_CHAT_SAVE_COUNT) {
                    olPlayHall.msgList.remove(0);
                }
                String time = "";
                if (GlobalSetting.INSTANCE.getShowChatTime()) {
                    time = DateUtil.format(new Date(EncryptUtil.getServerTime()), GlobalSetting.INSTANCE.getShowChatTimeModes());
                }
                message.getData().putString("TIME", time);
                // 如果聊天人没在屏蔽名单中，则将聊天消息加入list进行渲染展示
                if (!ChatUtil.isUserInChatBlackList(olPlayHall, message.getData().getString("U"))) {
                    olPlayHall.msgList.add(message.getData());
                }
                // 聊天音效播放
                if (GlobalSetting.INSTANCE.getChatsSound() && !Objects.equals(message.getData().getString("U"), OLBaseActivity.getKitiName())) {
                    SoundEffectPlayUtil.playSoundEffect(olPlayHall, Uri.parse(GlobalSetting.INSTANCE.getChatsSoundFile()));
                }
                // 聊天记录存储
                ChatUtil.chatsSaveHandle(message, olPlayHall, time);
                olPlayHall.bindChatAdapter(olPlayHall.msgListView, olPlayHall.msgList);
            });
            case 2 -> {
                Bundle data = message.getData();
                data.putBundle("bundle", olPlayHall.hallInfoBundle);
                int mode = data.getInt("mode");
                Intent intent;
                intent = new Intent(olPlayHall, mode == RoomModeEnum.KEYBOARD.getCode() ? OLPlayKeyboardRoom.class : OLPlayRoom.class);
                intent.putExtras(data);
                olPlayHall.startActivity(intent);
                olPlayHall.finish();
            }
            case 3 -> post(() -> {
                olPlayHall.roomList.clear();
                Bundle data1 = message.getData();
                int size = data1.size();
                for (int i = 0; i < size; i++) {
                    olPlayHall.roomList.add(data1.getBundle(String.valueOf(i)));
                }
                olPlayHall.bindAdapter(olPlayHall.roomListView, olPlayHall.roomList);
            });
            case 4 -> post(() -> Toast.makeText(olPlayHall, message.getData().getString("result"), Toast.LENGTH_SHORT).show());
            case 5 -> post(() -> {
                olPlayHall.friendList.clear();
                Bundle data16 = message.getData();
                int size = data16.size();
                if (size >= 0) {
                    for (int i = 0; i < size; i++) {
                        olPlayHall.friendList.add(data16.getBundle(String.valueOf(i)));
                    }
                    olPlayHall.bindMainGameAdapter(olPlayHall.friendListView, olPlayHall.friendList, 1, true);
                }
                olPlayHall.pageIsEnd = size < 20;
            });
            case 6 -> post(() -> {
                olPlayHall.tabHost.setCurrentTab(1);
                String string = message.getData().getString("U");
                if (!Objects.equals(string, OLBaseActivity.kitiName)) {
                    olPlayHall.sendTo = "@" + string + ":";
                    olPlayHall.sendTextView.setText(olPlayHall.sendTo);
                    Spannable text = olPlayHall.sendTextView.getText();
                    if (text != null) {
                        Selection.setSelection(text, text.length());
                    }
                }
            });
            case 7 -> post(() -> {
                olPlayHall.userInHallList.clear();
                Bundle data13 = message.getData();
                int size = data13.size();
                if (size >= 0) {
                    for (int i = 0; i < size; i++) {
                        olPlayHall.userInHallList.add(data13.getBundle(String.valueOf(i)));
                    }
                    olPlayHall.bindMainGameAdapter(olPlayHall.userInHallListView, olPlayHall.userInHallList, 3, true);
                }
            });
            case 8 -> post(() -> {
                String string = message.getData().getString("F");
                switch (message.getData().getInt("T")) {
                    case 0 -> {
                        if (!string.isEmpty()) {
                            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayHall);
                            jpDialogBuilder.setTitle("好友请求");
                            jpDialogBuilder.setMessage("[" + string + "]请求加您为好友,同意吗?");
                            String finalString = string;
                            jpDialogBuilder.setFirstButton("同意", (dialog, which) -> {
                                dialog.dismiss();
                                OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                                builder.setType(1);
                                builder.setName(finalString);
                                builder.setReject(false);
                                olPlayHall.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                            });
                            jpDialogBuilder.setSecondButton("拒绝", (dialog, which) -> {
                                dialog.dismiss();
                                OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                                builder.setType(1);
                                builder.setName(finalString);
                                builder.setReject(true);
                                olPlayHall.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                            });
                            jpDialogBuilder.buildAndShowDialog();
                        }
                    }
                    case 1 -> {
                        DialogUtil.setShowDialog(false);
                        string = message.getData().getString("F");
                        int i = message.getData().getInt("I");
                        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayHall);
                        jpDialogBuilder.setTitle("请求结果");
                        switch (i) {
                            case 0 ->
                                    jpDialogBuilder.setMessage("[" + string + "]同意添加您为好友!");
                            case 1 -> jpDialogBuilder.setMessage("对方拒绝了你的好友请求!");
                            case 2 -> jpDialogBuilder.setMessage("对方已经是你的好友!");
                            case 3 -> {
                                jpDialogBuilder.setTitle(message.getData().getString("title"));
                                jpDialogBuilder.setMessage(message.getData().getString("Message"));
                            }
                        }
                        jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                        jpDialogBuilder.buildAndShowDialog();
                    }
                    default -> {
                    }
                }
            });
            case 9 -> post(() -> {
                if (!DialogUtil.isShowDialog()) {
                    Bundle data14 = message.getData();
                    int i = data14.getInt("T");
                    byte b = (byte) data14.getInt("R");
                    byte b2 = (byte) data14.getInt("H");
                    int i2 = data14.getInt("C");
                    String messageStr = data14.getString("I");
                    String title = data14.getString("Ti");
                    JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayHall);
                    jpDialogBuilder.setTitle(title);
                    title = "确定";
                    if (i2 == 1 && b2 == olPlayHall.hallID && b2 > (byte) 0) {
                        jpDialogBuilder.setMessage(messageStr);
                        jpDialogBuilder.setFirstButton("进入房间", (dialog, which) -> {
                            dialog.dismiss();
                            DialogUtil.setShowDialog(false);
                            if (i == 0) {
                                olPlayHall.enterRoomHandle(data14.getInt("P"), b);
                            } else if (i == 1) {
                                OnlineEnterRoomDTO.Builder builder = OnlineEnterRoomDTO.newBuilder();
                                builder.setRoomId(b);
                                builder.setPassword(data14.getString("P"));
                                olPlayHall.sendMsg(OnlineProtocolType.ENTER_ROOM, builder.build());
                            }
                        });
                        jpDialogBuilder.setSecondButton("取消", (dialog, which) -> {
                            dialog.dismiss();
                            DialogUtil.setShowDialog(false);
                        });
                    } else {
                        jpDialogBuilder.setMessage(messageStr);
                        jpDialogBuilder.setFirstButton(title, (dialog, which) -> {
                            dialog.dismiss();
                            DialogUtil.setShowDialog(false);
                        });
                    }
                    DialogUtil.handleGoldSend(jpDialogBuilder, i, data14.getString("N"), data14.getString("F"));
                    jpDialogBuilder.buildAndShowDialog();
                    DialogUtil.setShowDialog(true);
                }
            });
            case 10 -> post(() -> {
                Bundle data12 = message.getData();
                OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                builder.setType(2);
                builder.setName(data12.getString("F"));
                olPlayHall.friendList.remove(message.arg1);
                olPlayHall.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                olPlayHall.bindMainGameAdapter(olPlayHall.friendListView, olPlayHall.friendList, 1, true);
            });
            case 11 -> post(() -> {
                Bundle data15 = message.getData();
                int i = data15.getInt("result");
                String[] msg = data15.getString("info").split("\n");
                String str = "提示";
                String str2 = switch (i) {
                    case 0 -> "确定";
                    case 1 -> "开始考级";
                    default -> null;
                };
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayHall);
                if (msg.length > 1) {
                    jpDialogBuilder.setVisibleRadioGroup(true);
                }
                jpDialogBuilder.setTitle(str);
                jpDialogBuilder.setMessage(msg[0]);
                for (int j = 1; j < msg.length; j++) {
                    RadioButton radioButton = new RadioButton(olPlayHall);
                    radioButton.setText(msg[j]);
                    radioButton.setTextSize(12);
                    radioButton.setTag(j - 1);
                    radioButton.setHeight(114);
                    jpDialogBuilder.addRadioButton(radioButton);
                }
                jpDialogBuilder.setFirstButton(str2, (dialog, which) -> {
                    int checkedId = jpDialogBuilder.getRadioGroupCheckedId();
                    if (checkedId == -1 && msg.length > 1) {
                        Toast.makeText(olPlayHall, "请选择一首考级曲", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        if (i == 1) {
                            OnlineClTestDTO.Builder builder = OnlineClTestDTO.newBuilder();
                            builder.setType(1);
                            builder.setSongIndex(checkedId);
                            olPlayHall.jpprogressBar.show();
                            olPlayHall.sendMsg(OnlineProtocolType.CL_TEST, builder.build());
                        }
                    }
                });
                if (i == 1) {
                    jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
                }
                jpDialogBuilder.buildAndShowDialog();
            });
            case 12 -> post(() -> olPlayHall.showRoomInfo(message.getData()));
            case 13 -> post(() -> {
                if (OnlineUtil.isX86()) {
                    Toast.makeText(olPlayHall, "您的设备不支持弹奏", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle data15 = message.getData();
                Intent intent12 = new Intent(olPlayHall, PianoPlay.class);
                intent12.putExtra("head", 3);
                intent12.putExtra("songBytes", data15.getString("songBytes"));
                intent12.putExtra("times", data15.getInt("songsID"));
                intent12.putExtra("hand", data15.getInt("hand"));
                intent12.putExtra("name", "");
                intent12.putExtra("bundle", olPlayHall.hallInfoBundle);
                intent12.putExtra("bundleHall", olPlayHall.hallInfoBundle);
                olPlayHall.startActivity(intent12);
                olPlayHall.finish();
            });
            case 21 -> post(() -> {
                Toast.makeText(olPlayHall, "您已掉线，请检查您的网络再重新登录", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent();
                intent1.setClass(olPlayHall, OLMainMode.class);
                olPlayHall.startActivity(intent1);
                olPlayHall.finish();
            });
            case 23 -> post(() -> olPlayHall.showInfoDialog(message.getData()));
            default -> {
            }
        }
    }
}
