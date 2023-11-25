package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.adapter.KeyboardPlayerImageAdapter;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.utils.ColorUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;

public final class OLPlayKeyboardRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLPlayKeyboardRoomHandler(OLPlayKeyboardRoom olPlayKeyboardRoom) {
        weakReference = new WeakReference<>(olPlayKeyboardRoom);
    }

    @Override
    public void handleMessage(Message message) {
        OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) weakReference.get();
        try {
            switch (message.what) {
                case 1:
                case 7:
                    post(() -> olPlayKeyboardRoom.initPlayer(olPlayKeyboardRoom.playerGrid, message.getData()));
                    return;
                case 2:
                case 4:
                    post(() -> olPlayKeyboardRoom.handleChat(message));
                    return;
                case 5:
                    post(() -> {
                        // 1.读取键盘音符事件信息，为空或长度为零则直接结束
                        long[] notes = message.getData().getLongArray("NOTES");
                        if (notes == null || notes.length == 0) {
                            return;
                        }
                        // 2.提取键盘音符事件的头信息，根据楼号确定用户
                        int roomPositionSub1 = (byte) (notes[0] & 0xF);
                        User user = olPlayKeyboardRoom.getRoomPlayerMap().get((byte) (roomPositionSub1 + 1));
                        if (user == null) {
                            return;
                        }
                        // 3.继续读头信息，根据midi键盘是否开启，更新midi键盘图标显示
                        boolean midiKeyboardOn = ((notes[0] >> 4) & 1) == 1;
                        if (olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].getMidiKeyboardOn() != midiKeyboardOn) {
                            olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(midiKeyboardOn);
                            if (olPlayKeyboardRoom.playerGrid.getAdapter() != null) {
                                ((KeyboardPlayerImageAdapter) (olPlayKeyboardRoom.playerGrid.getAdapter())).notifyDataSetChanged();
                            } else {
                                olPlayKeyboardRoom.playerGrid.setAdapter(new KeyboardPlayerImageAdapter(olPlayKeyboardRoom.playerList, olPlayKeyboardRoom));
                            }
                        }
                        // 4.执行人物形象闪烁
                        olPlayKeyboardRoom.blinkView(roomPositionSub1);
                        // 5.循环计算音符事件的总持续时间，如果总持续时间为0，直接进行播放和琴键按下/抬起处理，否则启动线程进行sleep逐个处理
                        long totalIntervalTime = 0;
                        for (int i = 1; i < notes.length; i += 3) {
                            totalIntervalTime += notes[i];
                        }
                        boolean sustainPedalOn = ((notes[0] >> 5) & 1) == 1;
                        if (totalIntervalTime == 0) {
                            for (int i = 1; i < notes.length; i += 3) {
                                handleKeyboardView(olPlayKeyboardRoom, notes, user, i, sustainPedalOn);
                            }
                        } else {
                            olPlayKeyboardRoom.receiveThreadPool.execute(() -> {
                                long lastTimeMillis = 0; // 初始化上一个时间戳为0
                                for (int i = 1; i < notes.length; i += 3) {
                                    long currentTimeMillis = notes[i]; // 当前时间戳
                                    if (lastTimeMillis > 0 && currentTimeMillis > lastTimeMillis) {
                                        try {
                                            Thread.sleep(currentTimeMillis - lastTimeMillis);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    lastTimeMillis = currentTimeMillis;
                                    int finalI = i;
                                    olPlayKeyboardRoom.runOnUiThread(() -> handleKeyboardView(olPlayKeyboardRoom, notes, user, finalI, sustainPedalOn));
                                }
                            });
                        }
                    });
                    return;
                case 8:
                    post(olPlayKeyboardRoom::handleKicked);
                    return;
                case 9:
                    post(() -> olPlayKeyboardRoom.handleFriendRequest(message));
                    return;
                case 10:
                    post(() -> {
                        String name = message.getData().getString("R");
                        olPlayKeyboardRoom.roomNameView.setText("[" + olPlayKeyboardRoom.roomId + "]" + name);
                    });
                    return;
                case 11:
                    post(() -> olPlayKeyboardRoom.handleRefreshFriendList(message));
                    return;
                case 12:
                    post(() -> olPlayKeyboardRoom.handlePrivateChat(message));
                    return;
                case 13:
                    post(() -> olPlayKeyboardRoom.handleRefreshFriendListWithoutPage(message));
                    return;
                case 14:
                    post(() -> olPlayKeyboardRoom.handleDialog(message));
                    return;
                case 15:
                    post(() -> olPlayKeyboardRoom.handleInvitePlayerList(message));
                    return;
                case 16:
                    post(() -> olPlayKeyboardRoom.handleSetUserInfo(message));
                    return;
                case 21:
                    post(olPlayKeyboardRoom::handleOffline);
                    return;
                case 22:
                    post(() -> {
                        int i = message.getData().getInt("MSG_T");
                        int i2 = message.getData().getInt("MSG_CT");
                        String string = message.getData().getString("MSG_C");
                        if (i != 0) {
                            olPlayKeyboardRoom.mo2860a(i, string, i2);
                        }
                    });
                    return;
                case 23:
                    post(() -> olPlayKeyboardRoom.showInfoDialog(message.getData()));
                    return;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleKeyboardView(OLPlayKeyboardRoom olPlayKeyboardRoom, long[] notes, User user, int i, boolean sustainPedalOn) {
        if (i + 2 >= notes.length || olPlayKeyboardRoom.roomPositionSub1 < 0
                || olPlayKeyboardRoom.roomPositionSub1 >= olPlayKeyboardRoom.olKeyboardStates.length) {
            return;
        }
        byte pitch = (byte) notes[i + 1];
        byte volume = (byte) notes[i + 2];
        if (volume > 0) {
            if (!olPlayKeyboardRoom.olKeyboardStates[olPlayKeyboardRoom.roomPositionSub1].getMuted()) {
                SoundEngineUtil.playSound(pitch, volume);
            }
            Integer color = user.getColor() == 0 ? null : ColorUtil.getUserColorByUserColorIndex(olPlayKeyboardRoom, user.getColor());
            olPlayKeyboardRoom.keyboardView.fireKeyDown(pitch, volume, color);
            olPlayKeyboardRoom.onlineWaterfallKeyDownHandle(pitch, volume, color == null ?
                    GlobalSetting.INSTANCE.getWaterfallFreeStyleColor() : color);
        } else {
            if (!sustainPedalOn && !olPlayKeyboardRoom.olKeyboardStates[olPlayKeyboardRoom.roomPositionSub1].getMuted()) {
                SoundEngineUtil.stopPlaySound(pitch);
            }
            olPlayKeyboardRoom.keyboardView.fireKeyUp(pitch);
            olPlayKeyboardRoom.onlineWaterfallKeyUpHandle(pitch);
        }
    }
}
