package ly.pp.justpiano3.utils;

import android.os.Bundle;
import android.os.Message;
import ly.pp.justpiano3.activity.OLMainMode;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.activity.OLPlayHallRoom;
import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.vo.OnlineBaseVO;
import protobuf.vo.OnlineClTestVO;
import protobuf.vo.OnlineEnterHallVO;
import protobuf.vo.OnlineHallChatVO;

public final class Receive {

    public static void receive(int msgType, OnlineBaseVO msg) {
        try {
            OLPlayHall olPlayHall;
            Message message;
            Bundle bundle;
            switch (msgType) {
                case OnlineProtocolType.USER_INFO_DIALOG:
                case OnlineProtocolType.PLAY_START:
                case OnlineProtocolType.PLAY_FINISH:
                case OnlineProtocolType.KICKED_QUIT_ROOM:
                case OnlineProtocolType.ROOM_CHAT:
                case OnlineProtocolType.CHANGE_ROOM_INFO:
                case OnlineProtocolType.PLAY_SONG:
                case OnlineProtocolType.BROADCAST:
                case OnlineProtocolType.LOAD_PLAY_USER:
                case OnlineProtocolType.RECOMMEND_SONG:
                case OnlineProtocolType.SET_USER_INFO:
                case OnlineProtocolType.SET_MINI_GRADE:
                case OnlineProtocolType.DIALOG:
                case OnlineProtocolType.COUPLE:
                    ReceiveHandle.m3949a(msgType, msg);
                    return;
                case OnlineProtocolType.CREATE_ROOM:
                    ReceiveHandle.m3951a(msg, "H");
                    return;
                case OnlineProtocolType.ENTER_ROOM:
                    ReceiveHandle.m3951a(msg, "G");
                    return;
                case OnlineProtocolType.LOGIN:
                    if (JPStack.top() instanceof OLMainMode) {
                        OLMainMode oLMainMode = (OLMainMode) JPStack.top();
                        Message message2 = Message.obtain(oLMainMode.olMainModeHandler);
                        String status = msg.getLogin().getStatus();
                        switch (status) {
                            case "N":
                                message2.what = 4;
                                break;
                            case "E":
                                message2.what = 5;
                                break;
                            case "V":
                                message2.what = 6;
                                break;
                            default:
                                message2.what = 1;
                                break;
                        }
                        assert oLMainMode != null;
                        oLMainMode.jpapplication.setServerTimeInterval(msg.getLogin().getTime());
                        oLMainMode.olMainModeHandler.handleMessage(message2);
                        return;
                    }
                    return;
                case OnlineProtocolType.HALL_CHAT:
                    if (JPStack.top() instanceof OLPlayHall) {
                        olPlayHall = (OLPlayHall) JPStack.top();
                        OnlineHallChatVO hallChat = msg.getHallChat();
                        message = Message.obtain(olPlayHall.olPlayHallHandler);
                        message.what = 1;
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("M", hallChat.getMessage());
                        bundle2.putString("U", hallChat.getUserName());
                        bundle2.putInt("T", hallChat.getType());
                        if (hallChat.getType() == 1) {
                            bundle2.putInt("V", 0);
                        }
                        message.setData(bundle2);
                        assert olPlayHall != null;
                        olPlayHall.olPlayHallHandler.handleMessage(message);
                        return;
                    }
                    return;
                case OnlineProtocolType.CHALLENGE:
                    ReceiveHandle.challenge(msg);
                    return;
                case OnlineProtocolType.FAMILY:
                    ReceiveHandle.family(msg);
                    return;
                case OnlineProtocolType.LOAD_ROOM_LIST:
                    ReceiveHandle.loadRoomList(msg);
                    return;
                case OnlineProtocolType.CHANGE_ROOM_POSITION:
                    ReceiveHandle.changeRoomPosition(msg);
                    return;
                case OnlineProtocolType.LOAD_ROOM_POSITION:
                    ReceiveHandle.loadRoomPosition(msg);
                    return;
                case OnlineProtocolType.CHANGE_ROOM_LIST:
                    ReceiveHandle.changeRoomList(msg);
                    return;
                case OnlineProtocolType.MINI_GRADE:
                    ReceiveHandle.miniGrade(msg);
                    return;
                case OnlineProtocolType.SHOP:
                    ReceiveHandle.shop(msg);
                    return;
                case OnlineProtocolType.LOAD_USER:
                case OnlineProtocolType.CHANGE_CLOTHES:
                case OnlineProtocolType.LOAD_USER_INFO:
                case OnlineProtocolType.LOAD_USER_LIST:
                    ReceiveHandle.m3953b(msgType, msg);
                    return;
                case OnlineProtocolType.ENTER_HALL:
                    OnlineEnterHallVO enterHall = msg.getEnterHall();
                    bundle = new Bundle();
                    if (enterHall.getAllowed()) {
                        bundle.putInt("T", 0);
                        bundle.putByte("hallID", Byte.parseByte(enterHall.getHallIdOrMsg()));
                        bundle.putString("hallName", enterHall.getNameOrTitle());
                    } else {
                        bundle.putInt("T", 1);
                        bundle.putString("I", enterHall.getHallIdOrMsg());
                        bundle.putString("N", enterHall.getNameOrTitle());
                    }
                    message = Message.obtain();
                    message.what = 1;
                    message.setData(bundle);
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                        return;
                    }
                    return;
                case OnlineProtocolType.DAILY:
                    ReceiveHandle.daily(msg);
                    return;
                case OnlineProtocolType.KEYBOARD:
                    ReceiveHandle.keyboardNotes(msg);
                    return;
                case OnlineProtocolType.CL_TEST:
                    OnlineClTestVO clTest = msg.getClTest();
                    bundle = new Bundle();
                    message = Message.obtain();
                    PianoPlay pianoPlay;
                    switch (clTest.getType()) {
                        case 0:
                            if (JPStack.top() instanceof OLPlayHall) {
                                olPlayHall = (OLPlayHall) JPStack.top();
                                assert olPlayHall != null;
                                olPlayHall.jpprogressBar.dismiss();
                                bundle.putInt("result", clTest.getClTestDialog().getAllowed() ? 1 : 0);
                                bundle.putString("info", clTest.getClTestDialog().getMessage());
                                message.setData(bundle);
                                message.what = 11;
                                olPlayHall.olPlayHallHandler.handleMessage(message);
                                return;
                            }
                            return;
                        case 1:
                            if (JPStack.top() instanceof OLPlayHall) {
                                olPlayHall = (OLPlayHall) JPStack.top();
                                assert olPlayHall != null;
                                olPlayHall.jpprogressBar.dismiss();
                                bundle.putInt("songsID", clTest.getClTestSong().getCl());
                                bundle.putString("songBytes", GZIPUtil.ZIPTo(clTest.getClTestSong().getSongContent()));
                                bundle.putInt("hand", clTest.getClTestSong().getHand());
                                message.setData(bundle);
                                message.what = 13;
                                olPlayHall.olPlayHallHandler.handleMessage(message);
                                return;
                            }
                            return;
                        case 2:
                            if (JPStack.top() instanceof PianoPlay) {
                                pianoPlay = (PianoPlay) JPStack.top();
                                message.setData(bundle);
                                message.what = 5;
                                assert pianoPlay != null;
                                pianoPlay.pianoPlayHandler.handleMessage(message);
                                return;
                            }
                            return;
                        case 3:
                            if (JPStack.top() instanceof PianoPlay) {
                                pianoPlay = (PianoPlay) JPStack.top();
                                bundle.putInt("R", clTest.getClTestFinish().getStatus());
                                bundle.putInt("G", clTest.getClTestFinish().getTargetScore());
                                bundle.putString("S", clTest.getClTestFinish().getScore());
                                bundle.putString("E", clTest.getClTestFinish().getExp());
                                message.setData(bundle);
                                message.what = 6;
                                assert pianoPlay != null;
                                pianoPlay.pianoPlayHandler.handleMessage(message);
                                return;
                            }
                            return;
                    }
                    return;
                case OnlineProtocolType.LOAD_ROOM_USER_LIST:
                    ReceiveHandle.m3954b(msg);
                    return;
                default:
            }
        } catch (Exception e222) {
            e222.printStackTrace();
        }
    }
}
