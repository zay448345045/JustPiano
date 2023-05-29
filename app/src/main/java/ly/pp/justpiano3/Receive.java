package ly.pp.justpiano3;

import android.os.Bundle;
import android.os.Message;

import protobuf.vo.OnlineBaseVO;
import protobuf.vo.OnlineClTestVO;
import protobuf.vo.OnlineEnterHallVO;
import protobuf.vo.OnlineHallChatVO;
import protobuf.vo.OnlineBaseVO;

final class Receive {

    static void receive(int msgType, OnlineBaseVO msg) {
        try {
            OLPlayHall olPlayHall;
            Message message;
            Bundle bundle;
            switch (msgType) {
                case 2:
                case 3:
                case 5:
                case 9:
                case 13:
                case 14:
                case 15:
                case 17:
                case 23:
                case 27:
                case 31:
                case 32:
                case 37:
                case 45:
                    ReceiveHandle.m3949a(msgType, msg);
                    return;
                case 6:
                    ReceiveHandle.m3951a(msg, "H");
                    return;
                case 7:
                    ReceiveHandle.m3951a(msg, "G");
                    return;
                case 10:
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
                case 12:
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
                case 16:
                    ReceiveHandle.challenge(msg);
                    return;
                case 18:
                    ReceiveHandle.family(msg);
                    return;
                case 19:
                    ReceiveHandle.loadRoomList(msg);
                    return;
                case 20:
                    ReceiveHandle.changeRoomPosition(msg);
                    return;
                case 21:
                    ReceiveHandle.loadRoomPosition(msg);
                    return;
                case 22:
                    ReceiveHandle.changeRoomList(msg);
                    return;
                case 25:
                    ReceiveHandle.miniGrade(msg);
                    return;
                case 26:
                    ReceiveHandle.shop(msg);
                    return;
                case 28:
                case 33:
                case 34:
                case 36:
                    ReceiveHandle.m3953b(msgType, msg);
                    return;
                case 29:
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
                case 38:
                    ReceiveHandle.daily(msg);
                    return;
                case 39:
                    ReceiveHandle.keyboardNotes(msg);
                    return;
                case 40:
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
                                bundle.putString("songBytes", GZIP.ZIPTo(clTest.getClTestSong().getSongContent()));
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
                case 43:
                    ReceiveHandle.m3954b(msg);
                    return;
                default:
            }
        } catch (Exception e222) {
            e222.printStackTrace();
        }
    }
}
