package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.adapter.KeyboardPlayerImageAdapter;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.utils.ColorUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;

import java.lang.ref.WeakReference;

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
                        byte[] notes = message.getData().getByteArray("NOTES");
                        if (notes.length == 0) {
                            return;
                        }
                        int roomPositionSub1 = (byte) (notes[0] & 0xF);
                        User user = olPlayKeyboardRoom.jpapplication.getRoomPlayerMap().get((byte) (roomPositionSub1 + 1));
                        if (user == null) {
                            return;
                        }
                        boolean midiKeyboardOn = (notes[0] >> 4) > 0;
                        boolean notify = false;
                        if (olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].isMidiKeyboardOn() != midiKeyboardOn) {
                            olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(midiKeyboardOn);
                            notify = true;
                        }
                        if (!olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].isPlaying()) {
                            olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].setPlaying(true);
                            notify = true;
                        }
                        if (notify) {
                            if (olPlayKeyboardRoom.playerGrid.getAdapter() != null) {
                                ((KeyboardPlayerImageAdapter) (olPlayKeyboardRoom.playerGrid.getAdapter())).notifyDataSetChanged();
                            } else {
                                olPlayKeyboardRoom.playerGrid.setAdapter(new KeyboardPlayerImageAdapter(olPlayKeyboardRoom.playerList, olPlayKeyboardRoom));
                            }
                        }
                        int totalIntervalTime = 0;
                        for (int i = 1; i < notes.length; i += 3) {
                            totalIntervalTime += notes[i];
                        }
                        // 无间隔时间，不用启动子线程，提升一键一发的执行效率
                        if (totalIntervalTime == 0) {
                            for (int i = 1; i < notes.length; i += 3) {
                                if (!olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].isMuted()) {
                                    SoundEngineUtil.playSound(notes[i + 1], notes[i + 2]);
                                }
                                handleKeyboardView(olPlayKeyboardRoom, notes, user, i);
                            }
                            if (olPlayKeyboardRoom.playerGrid.getAdapter() != null && olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].isPlaying()) {
                                olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].setPlaying(false);
                                ((KeyboardPlayerImageAdapter) (olPlayKeyboardRoom.playerGrid.getAdapter())).notifyDataSetChanged();
                            }
                        } else {
                            olPlayKeyboardRoom.receiveThreadPool.execute(() -> {
                                for (int i = 1; i < notes.length; i += 3) {
                                    if (notes[i] > 0) {
                                        try {
                                            Thread.sleep(notes[i]);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (!olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].isMuted()) {
                                        SoundEngineUtil.playSound(notes[i + 1], notes[i + 2]);
                                    }
                                    int finalI = i;
                                    olPlayKeyboardRoom.runOnUiThread(() -> handleKeyboardView(olPlayKeyboardRoom, notes, user, finalI));
                                }
                                if (olPlayKeyboardRoom.playerGrid.getAdapter() != null && olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].isPlaying()) {
                                    olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].setPlaying(false);
                                    olPlayKeyboardRoom.runOnUiThread(() -> ((KeyboardPlayerImageAdapter) (olPlayKeyboardRoom.playerGrid.getAdapter())).notifyDataSetChanged());
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
                        byte b = (byte) message.getData().getInt("MSG_CI");
                        String string = message.getData().getString("MSG_C");
                        if (i != 0) {
                            olPlayKeyboardRoom.mo2860a(i, string, i2, b);
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

    private void handleKeyboardView(OLPlayKeyboardRoom olPlayKeyboardRoom, byte[] notes, User user, int i) {
        if (notes[i + 2] > 0) {
            olPlayKeyboardRoom.keyboardView.fireKeyDown(notes[i + 1], notes[i + 2],
                    user.getKuang() == 0 ? null : ColorUtil.getKuangColorByKuangIndex(olPlayKeyboardRoom, user.getKuang()));
        } else {
            olPlayKeyboardRoom.keyboardView.fireKeyUp(notes[i + 1]);
        }
    }
}
