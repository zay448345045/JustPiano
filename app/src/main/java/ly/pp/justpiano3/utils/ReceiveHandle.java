package ly.pp.justpiano3.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import ly.pp.justpiano3.activity.*;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.Room;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.JPStack;
import org.json.JSONException;
import org.json.JSONObject;
import protobuf.vo.*;

import java.util.Date;
import java.util.Iterator;

final class ReceiveHandle {

    static void miniGrade(OnlineBaseVO msg) {
        int i = 0;
        Message message = Message.obtain();
        if (JPStack.top() instanceof PianoPlay) {
            PianoPlay pianoPlay = (PianoPlay) JPStack.top();
            User User = (User) pianoPlay.userMap.get((byte) msg.getMiniGrade().getRoomPosition());
            Bundle bundle = new Bundle();
            Bundle bundle2;
            User user2;
            int i2 = msg.getMiniGrade().getScore();
            int i3 = msg.getMiniGrade().getCombo();
            if (i2 < 0) {
                User.setOpenPosition();
                User.setPlayerName("");
            } else {
                User.setScore(i2);
                User.setCombo(i3);
            }
            for (byte ba = 1; ba <= 6; ba++) {
                user2 = (User) pianoPlay.userMap.get(ba);
                if (!user2.getPlayerName().isEmpty()) {
                    bundle2 = new Bundle();
                    bundle2.putString("G", String.valueOf(user2.getHand()));
                    bundle2.putString("U", user2.getPlayerName());
                    bundle2.putString("M", String.valueOf(user2.getScore()));
                    bundle2.putString("T", user2.getCombo() + "");
                    bundle.putBundle(String.valueOf(i), bundle2);
                    i++;
                }
            }
            message.what = 2;
            message.setData(bundle);
            pianoPlay.pianoPlayHandler.handleMessage(message);
        }
    }

    static void m3949a(int i, OnlineBaseVO msg) {
        int i2 = 0;
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        Bundle bundle2;
        Bundle bundle3;
        Handler handler;
        switch (i) {
            case OnlineProtocolType.USER_INFO_DIALOG:
                OnlineUserInfoDialogVO userInfoDialog = msg.getUserInfoDialog();
                message.what = 23;
                bundle.putString("U", userInfoDialog.getName());
                bundle.putString("F", userInfoDialog.getFamily());
                bundle.putInt("LV", userInfoDialog.getLv());
                bundle.putInt("E", userInfoDialog.getExp());
                bundle.putInt("CL", userInfoDialog.getCl());
                bundle.putInt("W", userInfoDialog.getWinnerNum());
                bundle.putInt("SC", userInfoDialog.getScore());
                bundle.putString("S", userInfoDialog.getGender());
                bundle.putString("P", userInfoDialog.getSignature());
                bundle.putInt("DR_H", userInfoDialog.getClothes().getHair());
                bundle.putInt("DR_E", userInfoDialog.getClothes().getEye());
                bundle.putInt("DR_J", userInfoDialog.getClothes().getJacket());
                bundle.putInt("DR_T", userInfoDialog.getClothes().getTrousers());
                bundle.putInt("DR_S", userInfoDialog.getClothes().getShoes());
                message.setData(bundle);
                if (JPStack.top() instanceof OLPlayRoom) {
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                } else if (JPStack.top() instanceof OLFamily) {
                    ((OLFamily) JPStack.top()).familyHandler.handleMessage(message);
                } else if (JPStack.top() instanceof OLPlayHall) {
                    ((OLPlayHall) JPStack.top()).olPlayHallHandler.handleMessage(message);
                } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                    ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                } else if (JPStack.top() instanceof OLPlayHallRoom) {
                    ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                }
                break;
            case OnlineProtocolType.PLAY_START:
                message.what = 5;
                bundle2 = new Bundle();
                bundle2.putString("S", msg.getPlayStart().getSongPath());
                bundle2.putInt("D", msg.getPlayStart().getTune());
                message.setData(bundle2);
                if (JPStack.top() instanceof OLPlayRoom) {
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                    return;
                } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                    ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                    return;
                }
                return;
            case OnlineProtocolType.PLAY_FINISH:
                message.what = 3;
                OnlinePlayFinishVO playFinish = msg.getPlayFinish();
                for (OnlinePlayGradeVO playGrade : playFinish.getPlayGradeList()) {
                    bundle3 = new Bundle();
                    bundle3.putString("I", playGrade.getIsPlaying() ? "P" : "");
                    bundle3.putString("N", playGrade.getName());
                    bundle3.putString("SC", String.valueOf(playGrade.getScore()));
                    bundle3.putString("P", String.valueOf(playGrade.getGrade().getPerfect()));
                    bundle3.putString("C", String.valueOf(playGrade.getGrade().getCool()));
                    bundle3.putString("G", String.valueOf(playGrade.getGrade().getGreat()));
                    bundle3.putString("B", String.valueOf(playGrade.getGrade().getBad()));
                    bundle3.putString("M", String.valueOf(playGrade.getGrade().getMiss()));
                    bundle3.putString("T", String.valueOf(playGrade.getGrade().getCombo()));
                    bundle3.putString("E", String.valueOf(playGrade.getGrade().getExp()));
                    bundle3.putString("GR", String.valueOf(playGrade.getGrade().getGradeColor()));
                    bundle.putBundle(String.valueOf(i2), bundle3);
                    i2++;
                }
                message.setData(bundle);
                if (JPStack.top() instanceof PianoPlay) {
                    ((PianoPlay) JPStack.top()).pianoPlayHandler.handleMessage(message);
                    return;
                }
                return;
            case OnlineProtocolType.KICKED_QUIT_ROOM:
                message.what = 8;
                if (JPStack.top() instanceof OLPlayRoom) {
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                    return;
                } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                    ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                }
                return;
            case OnlineProtocolType.ROOM_CHAT:
                OnlineRoomChatVO roomChat = msg.getRoomChat();
                message.what = 2;
                bundle.putString("U", roomChat.getUserName());
                bundle.putString("M", roomChat.getMessage());
                bundle.putInt("T", roomChat.getType());
                if (roomChat.getType() == 1) {
                    bundle.putInt("V", roomChat.getColor());
                }
                message.setData(bundle);
                if (JPStack.top() instanceof OLPlayRoom) {
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                    return;
                } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                    ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                    return;
                }
                return;
            case OnlineProtocolType.CHANGE_ROOM_INFO:
                message.what = 10;
                bundle.putString("R", msg.getChangeRoomInfo().getRoomName());
                message.setData(bundle);
                if (JPStack.top() instanceof OLPlayRoom) {
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                    return;
                } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                    ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                    return;
                }
                return;
            case OnlineProtocolType.PLAY_SONG:
                try {
                    message.what = 3;
                    bundle2 = new Bundle();
                    bundle2.putString("song_path", msg.getPlaySong().getSongPath().replace("\\/", "/"));
                    bundle2.putInt("diao", msg.getPlaySong().getTune());
                    message.setData(bundle2);
                    if (JPStack.top() instanceof OLPlayRoom) {
                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                        return;
                    } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                        ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case OnlineProtocolType.BROADCAST:
//                try {
//                    jSONObject3 = new JSONObject(str);
//                    if (JPStack.top() instanceof OLPlayHallRoom) {
//                        handler = ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler;
//                        if (handler != null) {
//                            message.what = 6;
//                            if (!str.isEmpty()) {
//                                bundle.putInt("C", jSONObject3.getInt("C"));
//                                bundle.putInt("D", jSONObject3.getInt("D"));
//                                bundle.putString("U", jSONObject3.getString("U"));
//                                bundle.putString("M", jSONObject3.getString("M"));
//                                message.setData(bundle);
//                                handler.handleMessage(message);
//                                return;
//                            }
//                            return;
//                        }
//                        return;
//                    }
//                    return;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    return;
//                }
                return;
            case OnlineProtocolType.LOAD_PLAY_USER:
                message.what = 1;
                message.arg1 = 0;
                OnlineLoadPlayUserVO loadPlayUser = msg.getLoadPlayUser();
                for (OnlinePlayUserVO playUser : loadPlayUser.getPlayUserList()) {
                    bundle3 = new Bundle();
                    bundle3.putString("G", String.valueOf(playUser.getHand()));
                    bundle3.putString("U", playUser.getName());
                    bundle3.putString("M", String.valueOf(playUser.getMode()));
                    bundle.putBundle(String.valueOf(i2), bundle3);
                    i2++;
                }
                message.setData(bundle);
                if (JPStack.top() instanceof PianoPlay) {
                    ((PianoPlay) JPStack.top()).pianoPlayHandler.handleMessage(message);
                    return;
                }
                return;
            case OnlineProtocolType.RECOMMEND_SONG:
                message.what = 4;
                bundle.putString("U", msg.getRecommendSong().getName());
                bundle.putString("M", "推荐歌曲:");
                bundle.putString("I", msg.getRecommendSong().getSongPath());
                bundle.putInt("D", 0);
                bundle.putInt("T", 0);
                message.setData(bundle);
                if (JPStack.top() instanceof OLPlayRoom) {
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                    return;
                } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                    ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                    return;
                }
                return;
            case OnlineProtocolType.SET_USER_INFO:
                OnlineSetUserInfoVO setUserInfo = msg.getSetUserInfo();
                int type = setUserInfo.getType();
                if (type == 3 || type == 4) {
                    bundle = new Bundle();
                    bundle.putInt("T", 1);
                    bundle.putString("I", setUserInfo.getMessage());
                    bundle.putString("N", setUserInfo.getCoupleTitle());
                    message = Message.obtain();
                    message.what = 1;
                    message.setData(bundle);
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                        return;
                    }
                    return;
                }
                handler = null;
                switch (setUserInfo.getLocation()) {
                    case 0:
                        if (JPStack.top() instanceof OLPlayRoom) {
                            handler = ((OLPlayRoom) JPStack.top()).olPlayRoomHandler;
                            message.what = 9;
                            break;
                        } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                            handler = ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler;
                            message.what = 9;
                            break;
                        }
                        return;
                    case 1:
                        if (JPStack.top() instanceof OLPlayHall) {
                            handler = ((OLPlayHall) JPStack.top()).olPlayHallHandler;
                            message.what = 8;
                            break;
                        } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                            handler = ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler;
                            message.what = 8;
                            break;
                        }
                        return;
                }
                if (handler != null) {
                    String string = setUserInfo.getName();
                    if (!string.isEmpty()) {
                        bundle.putInt("T", type);
                        if (type == 1) {
                            bundle.putInt("I", setUserInfo.getResult());
                            if (setUserInfo.getResult() == 3) {
                                bundle.putString("title", setUserInfo.getFriendTitle());
                                bundle.putString("Message", setUserInfo.getFriendMsg());
                            }
                        }
                        bundle.putString("F", string);
                        message.setData(bundle);
                        handler.handleMessage(message);
                    }
                }
                break;
            case OnlineProtocolType.SET_MINI_GRADE:
                message.what = 1;
                message.arg1 = 1;
                OnlineSetMiniGradeVO setMiniGrade = msg.getSetMiniGrade();
                for (OnlineMiniGradeOnVO miniGradeOn : setMiniGrade.getMiniGradeOnList()) {
                    bundle3 = new Bundle();
                    bundle3.putString("G", String.valueOf(miniGradeOn.getHand()));
                    bundle3.putString("U", miniGradeOn.getName());
                    bundle3.putString("M", String.valueOf(miniGradeOn.getMode()));
                    bundle.putBundle(String.valueOf(i2), bundle3);
                    i2++;
                }
                message.setData(bundle);
                if (JPStack.top() instanceof PianoPlay) {
                    ((PianoPlay) JPStack.top()).pianoPlayHandler.handleMessage(message);
                }
                break;
            case OnlineProtocolType.DIALOG:
                OnlineDialogVO dialog = msg.getDialog();
                type = dialog.getType();
                handler = null;
                switch (dialog.getLocation()) {
                    case 0:
                        if (JPStack.top() instanceof OLPlayRoom) {
                            handler = ((OLPlayRoom) JPStack.top()).olPlayRoomHandler;
                            message.what = 14;
                            break;
                        } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                            handler = ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler;
                            message.what = 14;
                            break;
                        }
                        return;
                    case 1:
                        if (JPStack.top() instanceof OLPlayHall) {
                            handler = ((OLPlayHall) JPStack.top()).olPlayHallHandler;
                            message.what = 9;
                            break;
                        }
                        return;
                    case 2:
                        if (JPStack.top() instanceof OLPlayHallRoom) {
                            handler = ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler;
                            message.what = 5;
                            break;
                        }
                        return;
                }
                if (handler != null) {
                    String string = dialog.getMessage();
                    if (!string.isEmpty()) {
                        bundle.putInt("T", type);
                        bundle.putString("Ti", dialog.getTitle());
                        bundle.putString("P", dialog.getPassword());
                        bundle.putString("I", string);
                        bundle.putInt("C", dialog.getCanEnter() ? 1 : 0);
                        bundle.putInt("H", dialog.getHallId());
                        bundle.putInt("R", dialog.getRoomId());
                        bundle.putString("N", dialog.getName());
                        try {
                            bundle.putString("F", new JSONObject(dialog.getBizData()).getString("handlingFee"));
                        } catch (Exception ignore) {
                        }
                        message.setData(bundle);
                        handler.handleMessage(message);
                    }
                }
                break;
            case OnlineProtocolType.COUPLE:
                message.what = 22;
                OnlineCoupleVO couple = msg.getCouple();
                switch (couple.getType()) {
                    case 2:
                    case 3:
                    case 5:
                        bundle.putString("MSG_C", couple.getContent());
                        bundle.putInt("MSG_CI", couple.getCoupleRoomPosition());
                        bundle.putInt("MSG_CT", couple.getCoupleType());
                        bundle.putInt("MSG_T", couple.getType());
                        break;
                    case 4:
                        JSONObject jsonObject = new JSONObject();
                        JSONObject jsonObjectP = new JSONObject();
                        JSONObject jsonObjectC = new JSONObject();
                        JSONObject jsonObjectI = new JSONObject();
                        try {
                            jsonObjectP.put("D_H", couple.getCoupleDialog().getDialogUser().getClothes().getHair());
                            jsonObjectP.put("D_E", couple.getCoupleDialog().getDialogUser().getClothes().getEye());
                            jsonObjectP.put("D_J", couple.getCoupleDialog().getDialogUser().getClothes().getJacket());
                            jsonObjectP.put("D_T", couple.getCoupleDialog().getDialogUser().getClothes().getTrousers());
                            jsonObjectP.put("D_S", couple.getCoupleDialog().getDialogUser().getClothes().getShoes());
                            jsonObjectP.put("N", couple.getCoupleDialog().getDialogUser().getName());
                            jsonObjectP.put("L", couple.getCoupleDialog().getDialogUser().getLv());
                            jsonObjectP.put("C", couple.getCoupleDialog().getDialogUser().getCl());
                            jsonObjectP.put("S", couple.getCoupleDialog().getDialogUser().getGender());
                            jsonObject.put("P", jsonObjectP);
                            jsonObjectC.put("D_H", couple.getCoupleDialog().getDialogCoupleUser().getClothes().getHair());
                            jsonObjectC.put("D_E", couple.getCoupleDialog().getDialogCoupleUser().getClothes().getEye());
                            jsonObjectC.put("D_J", couple.getCoupleDialog().getDialogCoupleUser().getClothes().getJacket());
                            jsonObjectC.put("D_T", couple.getCoupleDialog().getDialogCoupleUser().getClothes().getTrousers());
                            jsonObjectC.put("D_S", couple.getCoupleDialog().getDialogCoupleUser().getClothes().getShoes());
                            jsonObjectC.put("N", couple.getCoupleDialog().getDialogCoupleUser().getName());
                            jsonObjectC.put("L", couple.getCoupleDialog().getDialogCoupleUser().getLv());
                            jsonObjectC.put("C", couple.getCoupleDialog().getDialogCoupleUser().getCl());
                            jsonObjectC.put("S", couple.getCoupleDialog().getDialogCoupleUser().getGender());
                            jsonObject.put("C", jsonObjectC);
                            jsonObjectI.put("B", couple.getCoupleDialog().getCoupleDialogInfo().getDeclaration());
                            jsonObjectI.put("P", couple.getCoupleDialog().getCoupleDialogInfo().getBlessing());
                            jsonObjectI.put("T", couple.getCoupleDialog().getCoupleDialogInfo().getType());
                            jsonObjectI.put("I", couple.getCoupleDialog().getCoupleDialogInfo().getRoomPosition());
                            jsonObject.put("I", jsonObjectI);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bundle.putString("MSG_C", jsonObject.toString());
                        bundle.putInt("MSG_CI", couple.getCoupleRoomPosition());
                        bundle.putInt("MSG_CT", couple.getCoupleType());
                        bundle.putInt("MSG_T", couple.getType());
                        break;
                }
                if (JPStack.top() instanceof OLPlayRoom) {
                    message.setData(bundle);
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                    message.setData(bundle);
                    ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                }
                break;
            default:
                break;
        }
    }

    static void loadRoomList(OnlineBaseVO msg) {
        Bundle bundle = new Bundle();
        Message message = Message.obtain();
        OLPlayHall olPlayHall;
        message.what = 3;
        int i = 0;
        OnlineLoadRoomListVO loadRoomList = msg.getLoadRoomList();
        if (JPStack.top() instanceof OLPlayHall) {
            olPlayHall = (OLPlayHall) JPStack.top();
            for (OnlineRoomVO roomRaw : loadRoomList.getRoomList()) {
                Bundle bundle2 = new Bundle();
                Room room = new Room((byte) roomRaw.getRoomId(), roomRaw.getRoomName(), roomRaw.getFemaleNum(),
                        roomRaw.getMaleNum(), roomRaw.getIsPlaying() ? 1 : 0, roomRaw.getIsEncrypt() ? 1 : 0,
                        roomRaw.getColor(), roomRaw.getCloseNum(), roomRaw.getRoomMode());
                olPlayHall.putRoomToMap(room.getRoomID(), room);
                bundle2.putByte("I", room.getRoomID());
                bundle2.putString("N", room.getRoomName());
                bundle2.putIntArray("UA", room.getPeople());
                bundle2.putBoolean("IF", room.isPeopleFull());
                bundle2.putInt("IP", room.isPlaying());
                bundle2.putInt("PA", room.isPassword());
                bundle2.putInt("V", room.getRoomKuang());
                bundle2.putInt("D", room.getRoomMode());
                bundle.putBundle(String.valueOf(i), bundle2);
                i++;
            }
            message.setData(bundle);
            olPlayHall.olPlayHallHandler.handleMessage(message);
        }
    }

    static void m3951a(OnlineBaseVO msg, String str2) {
        if (JPStack.top() instanceof OLPlayHall) {
            OLPlayHall olPlayHall = (OLPlayHall) JPStack.top();
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            if (str2.equals("H")) {
                if (msg.getCreateRoom().getIsSuccess()) {
                    message.what = 2;
                    bundle.putString("R", msg.getCreateRoom().getRoomName());
                    bundle.putByte("ID", (byte) msg.getCreateRoom().getRoomId());
                    bundle.putString("isHost", str2);
                    bundle.putInt("mode", msg.getCreateRoom().getRoomMode());
                } else {
                    bundle.putString("result", "房间数已满!");
                    message.what = 4;
                }
            } else if (str2.equals("G")) {
                int i = msg.getEnterRoom().getStatus();
                switch (i) {
                    case 0:
                        bundle.putString("result", "该房间正在弹奏中!");
                        message.what = 4;
                        break;
                    case 1:
                        bundle.putString("result", "该房间人数已满!");
                        message.what = 4;
                        break;
                    case 2:
                        bundle.putString("result", "房间数已满!");
                        message.what = 4;
                        break;
                    case 3:
                        bundle.putString("result", "密码有误!");
                        message.what = 4;
                        break;
                    case 4:
                        message.what = 2;
                        bundle.putString("R", msg.getEnterRoom().getRoomName());
                        bundle.putByte("ID", (byte) msg.getEnterRoom().getRoomId());
                        bundle.putString("isHost", str2);
                        bundle.putInt("mode", msg.getEnterRoom().getRoomMode());
                        break;
                    default:
                        break;
                }
            }
            message.setData(bundle);
            olPlayHall.olPlayHallHandler.handleMessage(message);
        }
    }

    static void m3953b(int b, OnlineBaseVO msg) {
        int i = 0;
        Message message = Message.obtain();
        Bundle bundle;
        int i2;
        Bundle bundle2;
        switch (b) {
            case OnlineProtocolType.LOAD_USER:
                bundle = new Bundle();
                Bundle bundle3 = new Bundle();
                message.what = 0;
                OnlineLoadUserVO loadUser = msg.getLoadUser();
                try {
                    for (OnlineHallVO hall : loadUser.getHallList()) {
                        Bundle bundle4 = new Bundle();
                        bundle4.putByte("I", (byte) hall.getId());
                        bundle4.putString("N", hall.getName());
                        bundle4.putInt("PN", hall.getSize());
                        bundle4.putInt("TN", hall.getCapacity());
                        bundle4.putInt("W", hall.getEncrypt() ? 1 : 0);
                        bundle3.putBundle(String.valueOf(i), bundle4);
                        i++;
                    }
                    bundle.putBundle("L", bundle3);
                    bundle.putString("U", loadUser.getName());
                    bundle.putString("S", loadUser.getGender());
                    bundle.putString("I", String.valueOf(loadUser.getFamily()));
                    bundle.putInt("LV", loadUser.getLv());
                    bundle.putInt("CL", loadUser.getCl());
                    bundle.putInt("E", loadUser.getExp());
                    bundle.putInt("M", loadUser.getMessageNum());
                    bundle.putInt("DR_H", loadUser.getClothes().getHair());
                    bundle.putInt("DR_E", loadUser.getClothes().getEye());
                    bundle.putInt("DR_J", loadUser.getClothes().getJacket());
                    bundle.putInt("DR_T", loadUser.getClothes().getTrousers());
                    bundle.putInt("DR_S", loadUser.getClothes().getShoes());
                    bundle.putInt("CP", loadUser.getCoupleType());
                    message.setData(bundle);
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                        return;
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            case OnlineProtocolType.CHANGE_CLOTHES:
                OnlineChangeClothesVO changeClothes = msg.getChangeClothes();
                int type = changeClothes.getType();
                message.what = type;
                Bundle bundle5 = new Bundle();
                switch (type) {
                    case 0:  // 保存服装
                        bundle5.putString("I", changeClothes.getMessage());
                        break;
                    case 1:  // 进入换衣间加载音符和解锁情况
                        bundle5.putString("G", String.valueOf(changeClothes.getGold()));
                        bundle5.putByteArray("U", GZIPUtil.ZIPToArray(changeClothes.getUnlock()));
                        break;
                    case 2:  // 购买服装
                        bundle5.putString("I", changeClothes.getMessage());
                        bundle5.putString("G", String.valueOf(changeClothes.getGold()));
                        // todo 为什么这个Key是一样的
                        bundle5.putInt("U_T", changeClothes.getBuyClothesType());
                        bundle5.putInt("U_I", changeClothes.getBuyClothesId());
                        break;
                    case 3:  // 服务器下发服装价格
                        message.what = 5;
                        int[] priseArr = new int[changeClothes.getBuyClothesPricesCount()];
                        for (int y = 0; y < changeClothes.getBuyClothesPricesCount(); y++) {
                            priseArr[y] = changeClothes.getBuyClothesPricesList().get(y);
                        }
                        bundle5.putInt("C_T", changeClothes.getBuyClothesType());
                        bundle5.putIntArray("P", priseArr);
                        break;
                }
                message.setData(bundle5);
                if (JPStack.top() instanceof OLPlayDressRoom) {
                    ((OLPlayDressRoom) JPStack.top()).olPlayDressRoomHandler.handleMessage(message);
                }
                return;
            case OnlineProtocolType.LOAD_USER_INFO:
                bundle = new Bundle();
                OnlineLoadUserInfoVO loadUserInfo = msg.getLoadUserInfo();
                type = loadUserInfo.getType();
                switch (type) {
                    case 1:
                        i2 = loadUserInfo.getLoadUserFriend().getLocation();
                        for (OnlineFriendUserVO friendUser : loadUserInfo.getLoadUserFriend().getFriendUserList()) {
                            bundle2 = new Bundle();
                            bundle2.putString("F", friendUser.getName());
                            bundle2.putInt("O", friendUser.getOnline() ? 1 : 0);
                            bundle2.putString("S", friendUser.getGender());
                            bundle2.putInt("LV", friendUser.getLv());
                            bundle.putBundle(String.valueOf(i), bundle2);
                            i++;
                        }
                        switch (i2) {
                            case 0:
                                if (JPStack.top() instanceof OLPlayRoom) {
                                    message.what = 11;
                                    message.setData(bundle);
                                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                                    return;
                                } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                                    message.what = 11;
                                    message.setData(bundle);
                                    ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                                    return;
                                }
                                return;
                            case 1:
                                message.what = 5;
                                message.setData(bundle);
                                if (JPStack.top() instanceof OLPlayHall) {
                                    ((OLPlayHall) JPStack.top()).olPlayHallHandler.handleMessage(message);
                                    return;
                                }
                                return;
                            case 2:
                                message.what = 3;
                                new Bundle().putBundle("L", bundle);
                                message.setData(bundle);
                                if (JPStack.top() instanceof OLPlayHallRoom) {
                                    ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                                    return;
                                }
                                return;
                            default:
                                return;
                        }
                    case 2:
                        for (OnlineMailVO mail : loadUserInfo.getLoadUserMail().getMailList()) {
                            Bundle bundle6 = new Bundle();
                            bundle6.putString("F", mail.getUserFrom());
                            bundle6.putString("M", mail.getMessage());
                            String dateTime = DateUtil.format(new Date(mail.getTime()));
                            bundle6.putString("T", dateTime);
                            bundle6.putInt("type", 0);
                            bundle.putBundle(String.valueOf(i), bundle6);
                            i++;
                        }
                        message.what = 4;
                        message.setData(bundle);
                        if (JPStack.top() instanceof OLPlayHallRoom) {
                            ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                            return;
                        }
                        return;
                    case 3:
                        OnlineLoadUserCoupleVO loadUserCouple = loadUserInfo.getLoadUserCouple();
                        bundle.putString("U", loadUserCouple.getName());
                        bundle.putString("S", loadUserCouple.getGender());
                        bundle.putInt("LV", loadUserCouple.getLv());
                        bundle.putInt("CL", loadUserCouple.getCl());
                        bundle.putInt("DR_H", loadUserCouple.getClothes().getHair());
                        bundle.putInt("DR_E", loadUserCouple.getClothes().getEye());
                        bundle.putInt("DR_J", loadUserCouple.getClothes().getJacket());
                        bundle.putInt("DR_T", loadUserCouple.getClothes().getTrousers());
                        bundle.putInt("DR_S", loadUserCouple.getClothes().getShoes());
                        bundle.putString("IN", loadUserCouple.getDeclaration());
                        bundle.putInt("CP", loadUserCouple.getBlessing());
                        message.what = 22;
                        message.setData(bundle);
                        if (JPStack.top() instanceof OLPlayHallRoom) {
                            ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            case OnlineProtocolType.LOAD_USER_LIST:
                bundle = new Bundle();
                OnlineLoadUserListVO loadUserList = msg.getLoadUserList();
                try {
                    i2 = loadUserList.getIsInRoom() ? 1 : 0;
                    for (OnlineUserVO user : loadUserList.getUserList()) {
                        bundle2 = new Bundle();
                        bundle2.putString("U", user.getName());
                        bundle2.putString("S", user.getGender());
                        bundle2.putInt("LV", user.getLv());
                        bundle2.putInt("R", user.getRoomId());
                        bundle.putBundle(String.valueOf(i), bundle2);
                        i++;
                    }
                    switch (i2) {
                        case 0:
                            message.what = 7;
                            message.setData(bundle);
                            if (JPStack.top() instanceof OLPlayHall) {
                                ((OLPlayHall) JPStack.top()).olPlayHallHandler.handleMessage(message);
                                return;
                            }
                            return;
                        case 1:
                            message.what = 15;
                            message.setData(bundle);
                            if (JPStack.top() instanceof OLPlayRoom) {
                                ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                                return;
                            } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                                ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                } catch (Exception e32) {
                    e32.printStackTrace();
                    return;
                }
            default:
        }
    }

    static void m3954b(OnlineBaseVO msg) {
        Bundle bundle = new Bundle();
        Message message = Message.obtain();
        OnlineLoadRoomUserListVO loadRoomUserList = msg.getLoadRoomUserList();
        if (JPStack.top() instanceof OLPlayHall) {
            int i = loadRoomUserList.getRoomId();
            OLPlayHall olPlayHall = (OLPlayHall) JPStack.top();
            Bundle bundle2 = new Bundle();
            int i2 = 0;
            for (OnlineLoadRoomUserVO loadRoomUser : loadRoomUserList.getLoadRoomUserList()) {
                Bundle bundle3 = new Bundle();
                bundle3.putString("U", loadRoomUser.getName());
                bundle3.putString("S", loadRoomUser.getGender());
                bundle3.putInt("LV", loadRoomUser.getLv());
                bundle3.putInt("R", i);
                bundle2.putBundle(String.valueOf(i2), bundle3);
                i2++;
            }
            bundle.putBundle("L", bundle2);
            bundle.putInt("R", i);
            bundle.putInt("S", loadRoomUserList.getIsPlaying() ? 1 : 0);
            bundle.putInt("P", loadRoomUserList.getIsEncrypt() ? 1 : 0);
            message.setData(bundle);
            message.what = 12;
            olPlayHall.olPlayHallHandler.handleMessage(message);
        }
    }

    static void changeRoomList(OnlineBaseVO msg) {
        Bundle bundle = new Bundle();
        Message message = Message.obtain();
        message.what = 3;
        if (JPStack.top() instanceof OLPlayHall) {
            OLPlayHall olPlayHall = (OLPlayHall) JPStack.top();
            assert olPlayHall != null;
            OnlineRoomVO roomRaw = msg.getChangeRoomList();
            Room room = new Room((byte) roomRaw.getRoomId(), roomRaw.getRoomName(), roomRaw.getFemaleNum(),
                    roomRaw.getMaleNum(), roomRaw.getIsPlaying() ? 1 : 0, roomRaw.getIsEncrypt() ? 1 : 0,
                    roomRaw.getColor(), roomRaw.getCloseNum(), roomRaw.getRoomMode());
            if (room.getFcount() + room.getMcount() == 0) {
                olPlayHall.roomTitleMap.remove(room.getRoomID());
            } else {
                olPlayHall.putRoomToMap(room.getRoomID(), room);
            }
            Iterator it = olPlayHall.roomTitleMap.values().iterator();
            int i = 0;
            while (true) {
                int i2 = i;
                if (it.hasNext()) {
                    room = (Room) it.next();
                    Bundle bundle2 = new Bundle();
                    bundle2.putByte("I", room.getRoomID());
                    bundle2.putString("N", room.getRoomName());
                    bundle2.putIntArray("UA", room.getPeople());
                    bundle2.putBoolean("IF", room.isPeopleFull());
                    bundle2.putInt("IP", room.isPlaying());
                    bundle2.putInt("PA", room.isPassword());
                    bundle2.putInt("V", room.getRoomKuang());
                    bundle2.putInt("D", room.getRoomMode());
                    bundle.putBundle(String.valueOf(i2), bundle2);
                    i = i2 + 1;
                } else {
                    message.setData(bundle);
                    olPlayHall.olPlayHallHandler.handleMessage(message);
                    return;
                }
            }
        }
    }

    static void changeRoomPosition(OnlineBaseVO msg) {
        OnlineChangeRoomPositionVO changeRoomPosition = msg.getChangeRoomPosition();
        Message message = Message.obtain();
        message.what = 1;
        if (JPStack.top() instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) JPStack.top();
            int i = 0;
            for (OnlineRoomPositionUserVO roomPositionUser : changeRoomPosition.getRoomPositionUserList()) {
                User user = new User((byte) roomPositionUser.getPosition(), roomPositionUser.getName(),
                        roomPositionUser.getClothes().getHair(),
                        roomPositionUser.getClothes().getEye(),
                        roomPositionUser.getClothes().getJacket(),
                        roomPositionUser.getClothes().getTrousers(),
                        roomPositionUser.getClothes().getShoes(),
                        roomPositionUser.getGender(),
                        roomPositionUser.getUserStatus(),
                        roomPositionUser.getPositionStatus(),
                        roomPositionUser.getLv(),
                        roomPositionUser.getColor(),
                        roomPositionUser.getCl(),
                        roomPositionUser.getHand(),
                        roomPositionUser.getCoupleType(),
                        String.valueOf(roomPositionUser.getFamily()));
                olPlayRoom.putJPhashMap(user.getPosition(), user);
                i++;
            }
            Bundle bundle = new Bundle();
            Iterator it = olPlayRoom.jpapplication.getHashmap().values().iterator();
            i = 0;
            while (true) {
                int i3 = i;
                if (it.hasNext()) {
                    User user = (User) it.next();
                    Bundle bundle2 = new Bundle();
                    bundle2.putByte("PI", user.getPosition());
                    bundle2.putString("N", user.getPlayerName());
                    bundle2.putString("S", user.getSex());
                    bundle2.putString("IR", user.getStatus());
                    bundle2.putString("IH", user.getIshost());
                    bundle2.putInt("IV", user.getKuang());
                    bundle2.putInt("LV", user.getLevel());
                    bundle2.putInt("TR", user.getTrousers());
                    bundle2.putInt("JA", user.getJacket());
                    bundle2.putInt("EY", user.getEye());
                    bundle2.putInt("HA", user.getHair());
                    bundle2.putInt("SH", user.getShoes());
                    bundle2.putInt("CL", user.getClevel());
                    bundle2.putInt("GR", user.getHand());
                    bundle2.putInt("CP", user.getCpKind());
                    bundle2.putString("I", user.getFamilyID());
                    bundle.putBundle(String.valueOf(i3), bundle2);
                    i = i3 + 1;
                } else {
                    bundle.putString("SI", "");
                    bundle.putInt("diao", 0);
                    bundle.putString("MSG_C", "");
                    bundle.putInt("MSG_CI", 0);
                    bundle.putInt("MSG_CT", 0);
                    bundle.putBoolean("MSG_I", false);
                    message.setData(bundle);
                    olPlayRoom.olPlayRoomHandler.handleMessage(message);
                    return;
                }
            }
        } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) JPStack.top();
            int i = 0;
            for (OnlineRoomPositionUserVO roomPositionUser : changeRoomPosition.getRoomPositionUserList()) {
                User user = new User((byte) roomPositionUser.getPosition(), roomPositionUser.getName(),
                        roomPositionUser.getClothes().getHair(),
                        roomPositionUser.getClothes().getEye(),
                        roomPositionUser.getClothes().getJacket(),
                        roomPositionUser.getClothes().getTrousers(),
                        roomPositionUser.getClothes().getShoes(),
                        roomPositionUser.getGender(),
                        roomPositionUser.getUserStatus(),
                        roomPositionUser.getPositionStatus(),
                        roomPositionUser.getLv(),
                        roomPositionUser.getColor(),
                        roomPositionUser.getCl(),
                        roomPositionUser.getHand(),
                        roomPositionUser.getCoupleType(),
                        String.valueOf(roomPositionUser.getFamily()));
                olPlayKeyboardRoom.putJPhashMap(user.getPosition(), user);
                i++;
            }
            Bundle bundle = new Bundle();
            Iterator it = olPlayKeyboardRoom.jpapplication.getHashmap().values().iterator();
            i = 0;
            while (true) {
                int i3 = i;
                if (it.hasNext()) {
                    User user = (User) it.next();
                    Bundle bundle2 = new Bundle();
                    bundle2.putByte("PI", user.getPosition());
                    bundle2.putString("N", user.getPlayerName());
                    bundle2.putString("S", user.getSex());
                    bundle2.putString("IR", user.getStatus());
                    bundle2.putString("IH", user.getIshost());
                    bundle2.putInt("IV", user.getKuang());
                    bundle2.putInt("LV", user.getLevel());
                    bundle2.putInt("TR", user.getTrousers());
                    bundle2.putInt("JA", user.getJacket());
                    bundle2.putInt("EY", user.getEye());
                    bundle2.putInt("HA", user.getHair());
                    bundle2.putInt("SH", user.getShoes());
                    bundle2.putInt("CL", user.getClevel());
                    bundle2.putInt("GR", user.getHand());
                    bundle2.putInt("CP", user.getCpKind());
                    bundle2.putString("I", user.getFamilyID());
                    bundle.putBundle(String.valueOf(i3), bundle2);
                    i = i3 + 1;
                } else {
                    bundle.putString("SI", "");
                    bundle.putInt("diao", 0);
                    message.setData(bundle);
                    olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                    return;
                }
            }
        }
    }

    static void loadRoomPosition(OnlineBaseVO msg) {
        if (JPStack.top() instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) JPStack.top();
            OnlineLoadRoomPositionVO loadRoomPosition = msg.getLoadRoomPosition();
            Message message = Message.obtain();
            message.what = 1;
            Bundle bundle = new Bundle();
            int i = 0;
            for (OnlineRoomPositionUserVO roomPositionUser : loadRoomPosition.getRoomPositionUserList()) {
                Bundle bundle2 = new Bundle();
                User user = new User((byte) roomPositionUser.getPosition(), roomPositionUser.getName(),
                        roomPositionUser.getClothes().getHair(),
                        roomPositionUser.getClothes().getEye(),
                        roomPositionUser.getClothes().getJacket(),
                        roomPositionUser.getClothes().getTrousers(),
                        roomPositionUser.getClothes().getShoes(),
                        roomPositionUser.getGender(),
                        roomPositionUser.getUserStatus(),
                        roomPositionUser.getPositionStatus(),
                        roomPositionUser.getLv(),
                        roomPositionUser.getColor(),
                        roomPositionUser.getCl(),
                        roomPositionUser.getHand(),
                        roomPositionUser.getCoupleType(),
                        String.valueOf(roomPositionUser.getFamily()));
                olPlayRoom.putJPhashMap(user.getPosition(), user);
                bundle2.putByte("PI", user.getPosition());
                bundle2.putString("N", user.getPlayerName());
                bundle2.putString("S", user.getSex());
                bundle2.putString("IR", user.getStatus());
                bundle2.putString("IH", user.getIshost());
                bundle2.putInt("IV", user.getKuang());
                bundle2.putInt("LV", user.getLevel());
                bundle2.putInt("TR", user.getTrousers());
                bundle2.putInt("JA", user.getJacket());
                bundle2.putInt("EY", user.getEye());
                bundle2.putInt("HA", user.getHair());
                bundle2.putInt("SH", user.getShoes());
                bundle2.putInt("CL", user.getClevel());
                bundle2.putInt("GR", user.getHand());
                bundle2.putInt("CP", user.getCpKind());
                bundle2.putString("I", user.getFamilyID());
                bundle.putBundle(String.valueOf(i), bundle2);
                i++;
            }
            bundle.putString("SI", loadRoomPosition.getSongPath());
            bundle.putInt("diao", loadRoomPosition.getTune());
            bundle.putString("MSG_C", loadRoomPosition.getRoomPositionCouple().getCoupleName());
            bundle.putInt("MSG_CI", loadRoomPosition.getRoomPositionCouple().getCouplePosition());
            bundle.putInt("MSG_CT", loadRoomPosition.getRoomPositionCouple().getCoupleType());
            bundle.putBoolean("MSG_I", loadRoomPosition.getRoomPositionCouple().getInvite());
            message.setData(bundle);
            olPlayRoom.olPlayRoomHandler.handleMessage(message);
        } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) JPStack.top();
            OnlineLoadRoomPositionVO loadRoomPosition = msg.getLoadRoomPosition();
            Message message = Message.obtain();
            message.what = 1;
            Bundle bundle = new Bundle();
            int i = 0;
            for (OnlineRoomPositionUserVO roomPositionUser : loadRoomPosition.getRoomPositionUserList()) {
                Bundle bundle2 = new Bundle();
                User user = new User((byte) roomPositionUser.getPosition(), roomPositionUser.getName(),
                        roomPositionUser.getClothes().getHair(),
                        roomPositionUser.getClothes().getEye(),
                        roomPositionUser.getClothes().getJacket(),
                        roomPositionUser.getClothes().getTrousers(),
                        roomPositionUser.getClothes().getShoes(),
                        roomPositionUser.getGender(),
                        roomPositionUser.getUserStatus(),
                        roomPositionUser.getPositionStatus(),
                        roomPositionUser.getLv(),
                        roomPositionUser.getColor(),
                        roomPositionUser.getCl(),
                        roomPositionUser.getHand(),
                        roomPositionUser.getCoupleType(),
                        String.valueOf(roomPositionUser.getFamily()));
                olPlayKeyboardRoom.putJPhashMap(user.getPosition(), user);
                bundle2.putByte("PI", user.getPosition());
                bundle2.putString("N", user.getPlayerName());
                bundle2.putString("S", user.getSex());
                bundle2.putString("IR", user.getStatus());
                bundle2.putString("IH", user.getIshost());
                bundle2.putInt("IV", user.getKuang());
                bundle2.putInt("LV", user.getLevel());
                bundle2.putInt("TR", user.getTrousers());
                bundle2.putInt("JA", user.getJacket());
                bundle2.putInt("EY", user.getEye());
                bundle2.putInt("HA", user.getHair());
                bundle2.putInt("SH", user.getShoes());
                bundle2.putInt("CL", user.getClevel());
                bundle2.putInt("GR", user.getHand());
                bundle2.putInt("CP", user.getCpKind());
                bundle2.putString("I", user.getFamilyID());
                bundle.putBundle(String.valueOf(i), bundle2);
                i++;
            }
            bundle.putString("SI", loadRoomPosition.getSongPath());
            bundle.putInt("diao", loadRoomPosition.getTune());
            message.setData(bundle);
            olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
        }
    }

    static void keyboardNotes(OnlineBaseVO msg) {
        if (JPStack.top() instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) JPStack.top();
            try {
                byte[] keyboardNotes = msg.getKeyboardNote().getData().toByteArray();
                Bundle bundle = new Bundle();
                bundle.putByteArray("NOTES", keyboardNotes);
                Message message = Message.obtain();
                message.what = 5;
                message.setData(bundle);
                olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void challenge(OnlineBaseVO msg) {
        OnlineChallengeVO challenge = msg.getChallenge();
        Message message = Message.obtain();
        switch (challenge.getType()) {
            case 1:
                if (JPStack.top() instanceof OLChallenge) {
                    OLChallenge olChallenge = (OLChallenge) JPStack.top();
                    message.what = 1;
                    assert olChallenge != null;
                    Bundle bundle = new Bundle();
                    int i = 0;
                    for (OnlineChallengeUserVO challengeUser : challenge.getChallengeEnter().getUserScoreList()) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("N", challengeUser.getName());
                        bundle2.putString("S", String.valueOf(challengeUser.getScore()));
                        bundle2.putString("T", challengeUser.getTime());
                        bundle.putBundle(String.valueOf(i), bundle2);
                        i++;
                    }
                    bundle.putInt("S", challenge.getChallengeEnter().getScore());
                    bundle.putString("P", challenge.getChallengeEnter().getPosition());
                    bundle.putString("T", String.valueOf(challenge.getChallengeEnter().getChallengeNum()));
                    bundle.putString("Z", challenge.getChallengeEnter().getYesterdayPosition());
                    message.setData(bundle);
                    olChallenge.challengeHandler.handleMessage(message);
                    return;
                }
                break;
            case 2:
                if (JPStack.top() instanceof OLChallenge) {
                    OLChallenge olChallenge = (OLChallenge) JPStack.top();
                    assert olChallenge != null;
                    olChallenge.jpprogressBar.dismiss();
                    message.what = 2;
                    Bundle bundle = new Bundle();
                    bundle.putInt("R", challenge.getChallengeDialog().getAllowed() ? 1 : 0);
                    bundle.putString("I", challenge.getChallengeDialog().getMessage());
                    bundle.putString("P", GZIPUtil.ZIPTo(challenge.getChallengeDialog().getSongContent()));
                    message.setData(bundle);
                    olChallenge.challengeHandler.handleMessage(message);
                }
                break;
            case 3:
                if (JPStack.top() instanceof PianoPlay) {
                    PianoPlay pianoPlay = (PianoPlay) JPStack.top();
                    message.setData(new Bundle());
                    message.what = 5;
                    assert pianoPlay != null;
                    pianoPlay.pianoPlayHandler.handleMessage(message);
                    return;
                }
                break;
            case 4:
                if (JPStack.top() instanceof PianoPlay) {
                    PianoPlay pianoPlay = (PianoPlay) JPStack.top();
                    Bundle bundle = new Bundle();
                    bundle.putString("I", challenge.getChallengeFinish().getMessage());
                    message.setData(bundle);
                    message.what = 9;
                    assert pianoPlay != null;
                    pianoPlay.pianoPlayHandler.handleMessage(message);
                    return;
                }
                break;
            case 5:
                if (JPStack.top() instanceof OLChallenge) {
                    OLChallenge olChallenge = (OLChallenge) JPStack.top();
                    message.what = 5;
                    Bundle bundle = new Bundle();
                    bundle.putInt("P", challenge.getChallengePrize().getPrizeType());
                    bundle.putString("N", challenge.getChallengePrize().getPrizeName());
                    message.setData(bundle);
                    olChallenge.challengeHandler.handleMessage(message);
                }
                break;
            default:
                break;
        }
    }

    static void family(OnlineBaseVO msg) {
        Message message = Message.obtain();
        OnlineFamilyVO family = msg.getFamily();
        switch (family.getType()) {
            case 1:
                if (JPStack.top() instanceof OLFamily) {
                    OLFamily olFamily = (OLFamily) JPStack.top();
                    assert olFamily != null;
                    message.what = 1;
                    olFamily.jpprogressBar.dismiss();
                    Bundle bundle = new Bundle();
                    int i = 0;
                    for (OnlineFamilyUserVO familyUser : family.getFamilyEnter().getFamilyUserList()) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("N", familyUser.getName());
                        bundle2.putString("C", String.valueOf(familyUser.getContribution()));
                        bundle2.putString("L", String.valueOf(familyUser.getLv()));
                        bundle2.putString("S", familyUser.getGender());
                        bundle2.putString("O", String.valueOf(familyUser.getOnline() ? 1 : 0));
                        bundle2.putString("P", String.valueOf(familyUser.getPosition()));
                        bundle2.putString("D", familyUser.getLoginDate());
                        bundle.putBundle(String.valueOf(i), bundle2);
                        i++;
                    }
                    bundle.putString("D", family.getFamilyEnter().getDeclaration());
                    String dateTime = DateUtil.format(new Date(family.getFamilyEnter().getCreateDate()));
                    bundle.putString("T", dateTime);
                    bundle.putString("Z", family.getFamilyEnter().getLeader());
                    bundle.putString("N", family.getFamilyEnter().getName());
                    bundle.putString("C", String.valueOf(family.getFamilyEnter().getContribution()));
                    bundle.putString("P", family.getFamilyEnter().getPosition());
                    message.setData(bundle);
                    olFamily.familyHandler.handleMessage(message);
                }
                break;
            case 2:
                if (JPStack.top() instanceof OLPlayHallRoom) {
                    OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) JPStack.top();
                    assert olPlayHallRoom != null;
                    message.what = 2;
                    olPlayHallRoom.jpprogressBar.dismiss();
                    Bundle bundle = new Bundle();
                    int i = 0;
                    for (OnlineFamilyInfoVO familyInfo : family.getFamilyList().getFamilyInfoList()) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("N", familyInfo.getName());
                        bundle2.putString("C", String.valueOf(familyInfo.getContribution()));
                        bundle2.putString("T", String.valueOf(familyInfo.getCapacity()));
                        bundle2.putString("U", String.valueOf(familyInfo.getSize()));
                        bundle2.putString("I", String.valueOf(familyInfo.getFamilyId()));
                        bundle2.putByteArray("J", GZIPUtil.ZIPToArray(familyInfo.getPicture()));
                        bundle.putBundle(String.valueOf(i), bundle2);
                        i++;
                    }
                    bundle.putString("C", String.valueOf(family.getFamilyList().getContribution()));
                    bundle.putString("P", String.valueOf(family.getFamilyList().getPosition()));
                    bundle.putString("N", family.getFamilyList().getName());
                    bundle.putString("T", String.valueOf(family.getFamilyList().getCapacity()));
                    bundle.putString("U", String.valueOf(family.getFamilyList().getSize()));
                    bundle.putString("I", String.valueOf(family.getFamilyList().getFamilyId()));
                    bundle.putByteArray("J", GZIPUtil.ZIPToArray(family.getFamilyList().getPicture()));
                    message.setData(bundle);
                    olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                }
                break;
            case 3:
                if (JPStack.top() instanceof OLPlayHallRoom) {
                    OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) JPStack.top();
                    Bundle bundle = new Bundle();
                    bundle.putString("I", family.getFamilyDialog().getMessage());
                    bundle.putInt("R", family.getFamilyDialog().getAllowed() ? 1 : 0);
                    message.setData(bundle);
                    message.what = 9;
                    assert olPlayHallRoom != null;
                    olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    return;
                }
                break;
            case 4:
                if (JPStack.top() instanceof OLPlayHallRoom) {
                    OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) JPStack.top();
                    Bundle bundle = new Bundle();
                    bundle.putInt("R", family.getFamilyCreate().getResult());
                    message.setData(bundle);
                    message.what = 10;
                    assert olPlayHallRoom != null;
                    olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    return;
                }
                break;
            case 5:
            case 6:
            case 7:
            case 10:
                if (JPStack.top() instanceof OLFamily) {
                    OLFamily olFamily = (OLFamily) JPStack.top();
                    Bundle bundle = new Bundle();
                    bundle.putString("I", family.getFamilyDialog().getMessage());
                    message.setData(bundle);
                    message.what = 5;
                    assert olFamily != null;
                    olFamily.familyHandler.handleMessage(message);
                    return;
                }
                break;
            case 8:
                if (JPStack.top() instanceof OLFamily) {
                    OLFamily olFamily = (OLFamily) JPStack.top();
                    Bundle bundle = new Bundle();
                    try {
                        bundle.putString("I", family.getFamilyDialog().getMessage());
                        bundle.putInt("R", family.getFamilyDialog().getAllowed() ? 1 : 0);
                    } catch (Exception ignore) {
                    }
                    message.setData(bundle);
                    message.what = 8;
                    assert olFamily != null;
                    olFamily.familyHandler.handleMessage(message);
                    return;
                }
                break;
        }
    }

    static void shop(OnlineBaseVO msg) {
        OnlineShopVO shop = msg.getShop();
        Message message = Message.obtain();
        switch (shop.getType()) {
            case 1:  // 加载商品
                if (JPStack.top() instanceof OLPlayDressRoom) {
                    OLPlayDressRoom olPlayDressRoom = (OLPlayDressRoom) JPStack.top();
                    assert olPlayDressRoom != null;
                    message.what = 3;
                    olPlayDressRoom.jpprogressBar.dismiss();
                    Bundle bundle = new Bundle();
                    int i = 0;
                    for (OnlineShopProductVO shopProduct : shop.getShopProductShow().getProductList()) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("N", shopProduct.getName());
                        bundle2.putString("D", shopProduct.getDescription());
                        bundle2.putInt("G", shopProduct.getPrice());
                        bundle2.putString("P", shopProduct.getPicture());
                        bundle2.putInt("I", shopProduct.getId());
                        bundle.putBundle(String.valueOf(i), bundle2);
                        i++;
                    }
                    bundle.putString("G", String.valueOf(shop.getShopProductShow().getGold()));
                    message.setData(bundle);
                    olPlayDressRoom.olPlayDressRoomHandler.handleMessage(message);
                }
                break;
            case 2:  // 购买商品
                if (JPStack.top() instanceof OLPlayDressRoom) {
                    OLPlayDressRoom olPlayDressRoom = (OLPlayDressRoom) JPStack.top();
                    Bundle bundle = new Bundle();
                    bundle.putString("I", shop.getShopProductBuy().getMessage());
                    bundle.putString("G", String.valueOf(shop.getShopProductBuy().getGold()));
                    message.setData(bundle);
                    message.what = 4;
                    assert olPlayDressRoom != null;
                    olPlayDressRoom.olPlayDressRoomHandler.handleMessage(message);
                    return;
                }
                break;
        }
    }

    static void daily(OnlineBaseVO msg) {
        Message message = Message.obtain();
        OnlineDailyVO daily = msg.getDaily();
        switch (daily.getType()) {
            case 0:
                break;
            case 1:
                if (JPStack.top() instanceof OLPlayHallRoom) {
                    OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) JPStack.top();
                    assert olPlayHallRoom != null;
                    message.what = 11;
                    olPlayHallRoom.jpprogressBar.dismiss();
                    Bundle bundle = new Bundle();
                    int i = 0;
                    for (OnlineDailyTimeUserVO dailyTimeUser : daily.getDailyTimeList().getDailyTimeUserList()) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("N", dailyTimeUser.getName());
                        bundle2.putString("T", String.valueOf(dailyTimeUser.getOnlineTime()));
                        bundle2.putString("B", dailyTimeUser.getBonus());
                        bundle2.putString("G", String.valueOf(dailyTimeUser.getBonusGet() ? 1 : 0));
                        bundle.putBundle(String.valueOf(i), bundle2);
                        i++;
                    }
                    bundle.putString("M", daily.getDailyTimeList().getTomorrowBonus());
                    bundle.putString("T", String.valueOf(daily.getDailyTimeList().getTodayOnlineTime()));
                    message.setData(bundle);
                    olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                }
                break;
            case 2:
                if (JPStack.top() instanceof OLPlayHallRoom) {
                    OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) JPStack.top();
                    Bundle bundle = new Bundle();
                    bundle.putString("M", daily.getDailyPrizeGet().getMessage());
                    message.setData(bundle);
                    message.what = 12;
                    assert olPlayHallRoom != null;
                    olPlayHallRoom.jpprogressBar.dismiss();
                    olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    return;
                }
                break;
        }
    }
}
