package ly.pp.justpiano3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import ly.pp.justpiano3.protobuf.response.OnlineResponse;

final class JsonHandle {

    static void m3948a(String str) {
        int i = 0;
        Message message = new Message();
        try {
            if (JPStack.top() instanceof PianoPlay) {
                PianoPlay pianoPlay = (PianoPlay) JPStack.top();
                JSONObject jSONObject = new JSONObject(str);
                User User = (User) pianoPlay.userMap.get((byte) jSONObject.getInt("I"));
                Bundle bundle = new Bundle();
                Bundle bundle2;
                User User2;
                int i2 = jSONObject.getInt("S");
                int i3 = jSONObject.getInt("T");
                if (i2 < 0) {
                    User.setOpenPosition();
                    User.setPlayerName("");
                } else {
                    User.setScore(i2);
                    User.setCombo(i3);
                }
                for (byte ba = 1; ba <= 6; ba++) {
                    User2 = (User) pianoPlay.userMap.get(ba);
                    if (!User2.getPlayerName().isEmpty()) {
                        bundle2 = new Bundle();
                        bundle2.putString("G", String.valueOf(User2.getHand()));
                        bundle2.putString("U", User2.getPlayerName());
                        bundle2.putString("M", User2.getScore());
                        bundle2.putString("T", User2.getCombo() + "");
                        bundle.putBundle(String.valueOf(i), bundle2);
                        i++;
                    }
                }
                message.what = 2;
                message.setData(bundle);
                pianoPlay.pianoPlayHandler.handleMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void m3949a(int i, String str, OnlineResponse.Message msg) {
        int i2 = 0;
        Message message = new Message();
        Bundle bundle = new Bundle();
        Bundle bundle2;
        JSONObject jSONObject;
        Bundle bundle3;
        JSONObject jSONObject2;
        JSONObject jSONObject3;
        Handler handler;
        JSONArray jSONArray;
        int length;
        String string;
        switch (i) {
            case 2:
                try {
                    jSONObject2 = new JSONObject(GZIP.ZIPTo(str));
                    message.what = 23;
                    bundle.putString("U", jSONObject2.getString("U"));
                    bundle.putString("F", jSONObject2.getString("F"));
                    bundle.putInt("LV", jSONObject2.getInt("LV"));
                    bundle.putInt("E", jSONObject2.getInt("E"));
                    bundle.putInt("CL", jSONObject2.getInt("CL"));
                    bundle.putInt("W", jSONObject2.getInt("W"));
                    bundle.putInt("SC", jSONObject2.getInt("SC"));
                    bundle.putString("P", jSONObject2.getString("P"));
                    bundle.putString("DR", jSONObject2.getString("DR"));
                    message.setData(bundle);
                    if (JPStack.top() instanceof OLPlayRoom) {
                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                    } else if (JPStack.top() instanceof OLFamily) {
                        ((OLFamily) JPStack.top()).familyHandler.handleMessage(message);
                    } else if (JPStack.top() instanceof OLPlayHall) {
                        ((OLPlayHall) JPStack.top()).olPlayHallHandler.handleMessage(message);
                    } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                        ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                message.what = 5;
                bundle2 = new Bundle();
                try {
                    JSONObject jSONObject4 = new JSONObject(str);
                    bundle2.putString("S", jSONObject4.getString("S"));
                    bundle2.putInt("D", jSONObject4.getInt("D"));
                    message.setData(bundle2);
                    if (JPStack.top() instanceof OLPlayRoom) {
                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                        return;
                    } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                        ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                        return;
                    }
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            case 5:
                try {
                    message.what = 3;
                    JSONArray jSONArray2 = new JSONArray(GZIP.ZIPTo(new JSONObject(str).getString("L")));
                    int length2 = jSONArray2.length();
                    while (i2 < length2) {
                        jSONObject = jSONArray2.getJSONObject(i2);
                        bundle3 = new Bundle();
                        bundle3.putString("I", jSONObject.getString("I"));
                        bundle3.putString("N", jSONObject.getString("N"));
                        bundle3.putString("SC", jSONObject.getString("SC"));
                        JSONObject jSONObject5 = new JSONObject(jSONObject.getString("SI"));
                        bundle3.putString("P", jSONObject5.getString("P"));
                        bundle3.putString("C", jSONObject5.getString("C"));
                        bundle3.putString("G", jSONObject5.getString("G"));
                        bundle3.putString("B", jSONObject5.getString("B"));
                        bundle3.putString("M", jSONObject5.getString("M"));
                        bundle3.putString("T", jSONObject5.getString("T"));
                        bundle3.putString("E", jSONObject5.getString("E"));
                        bundle3.putString("GR", jSONObject5.getString("GR"));
                        bundle.putBundle(String.valueOf(i2), bundle3);
                        i2++;
                    }
                    message.setData(bundle);
                    if (JPStack.top() instanceof PianoPlay) {
                        ((PianoPlay) JPStack.top()).pianoPlayHandler.handleMessage(message);
                        return;
                    }
                    return;
                } catch (JSONException e2) {
                    e2.printStackTrace();
                    return;
                }
            case 9:
                message.what = 8;
                if (JPStack.top() instanceof OLPlayRoom) {
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                    return;
                } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                    ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                }
                return;
            case 13:
                OnlineResponse.RoomChat roomChat = msg.getRoomChat();
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
            case 14:
                try {
                    jSONObject2 = new JSONObject(str);
                    message.what = 10;
                    bundle.putString("R", jSONObject2.getString("R"));
                    message.setData(bundle);
                    if (JPStack.top() instanceof OLPlayRoom) {
                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                        return;
                    } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                        ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                        return;
                    }
                    return;
                } catch (JSONException e22) {
                    e22.printStackTrace();
                    return;
                }
            case 15:
                try {
                    jSONObject3 = new JSONObject(str);
                    message.what = 3;
                    bundle2 = new Bundle();
                    bundle2.putString("song_path", jSONObject3.getString("S").replace("\\/", "/"));
                    bundle2.putInt("diao", jSONObject3.getInt("D"));
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
            case 17:
                try {
                    jSONObject3 = new JSONObject(str);
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        handler = ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler;
                        if (handler != null) {
                            message.what = 6;
                            if (!str.isEmpty()) {
                                bundle.putInt("C", jSONObject3.getInt("C"));
                                bundle.putInt("D", jSONObject3.getInt("D"));
                                bundle.putString("U", jSONObject3.getString("U"));
                                bundle.putString("M", jSONObject3.getString("M"));
                                message.setData(bundle);
                                handler.handleMessage(message);
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            case 24:
                message.what = 1;
                message.arg1 = 0;
                try {
                    jSONArray = new JSONArray(str);
                    length = jSONArray.length();
                    while (i2 < length) {
                        jSONObject = jSONArray.getJSONObject(i2);
                        bundle3 = new Bundle();
                        bundle3.putString("G", jSONObject.getString("G"));
                        bundle3.putString("U", jSONObject.getString("U"));
                        bundle3.putString("M", jSONObject.getString("M"));
                        bundle.putBundle(String.valueOf(i2), bundle3);
                        i2++;
                    }
                    message.setData(bundle);
                    if (JPStack.top() instanceof PianoPlay) {
                        ((PianoPlay) JPStack.top()).pianoPlayHandler.handleMessage(message);
                        return;
                    }
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            case 27:
                try {
                    jSONObject2 = new JSONObject(str);
                    message.what = 4;
                    bundle.putString("U", jSONObject2.getString("U"));
                    bundle.putString("M", "推荐歌曲:");
                    bundle.putString("I", jSONObject2.getString("I"));
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            case 31:
                try {
                    jSONObject3 = new JSONObject(str);
                    length = jSONObject3.getInt("T");
                    handler = null;
                    switch (jSONObject3.getInt("S")) {
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
                        string = jSONObject3.getString("F");
                        if (!string.isEmpty()) {
                            bundle.putInt("T", length);
                            if (length == 1) {
                                bundle.putInt("I", jSONObject3.getInt("I"));
                                if (jSONObject3.getInt("I") == 3) {
                                    bundle.putString("title", jSONObject3.getString("H"));
                                    bundle.putString("Message", jSONObject3.getString("M"));
                                }
                            }
                            bundle.putString("F", string);
                            message.setData(bundle);
                            handler.handleMessage(message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 32:
                message.what = 1;
                message.arg1 = 1;
                try {
                    jSONArray = new JSONArray(str);
                    length = jSONArray.length();
                    while (i2 < length) {
                        jSONObject = jSONArray.getJSONObject(i2);
                        bundle3 = new Bundle();
                        bundle3.putString("G", jSONObject.getString("G"));
                        bundle3.putString("U", jSONObject.getString("U"));
                        bundle3.putString("M", jSONObject.getString("M"));
                        bundle.putBundle(String.valueOf(i2), bundle3);
                        i2++;
                    }
                    message.setData(bundle);
                    if (JPStack.top() instanceof PianoPlay) {
                        ((PianoPlay) JPStack.top()).pianoPlayHandler.handleMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 37:
                try {
                    jSONObject3 = new JSONObject(str);
                    length = jSONObject3.getInt("T");
                    handler = null;
                    switch (jSONObject3.getInt("S")) {
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
                            } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                                handler = ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler;
                                message.what = 9;
                                break;
                            }
                            return;
                        case 2:
                            if (JPStack.top() instanceof OLPlayHallRoom) {
                                handler = ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler;
                                message.what = 5;
                                break;
                            } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                                handler = ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler;
                                message.what = 5;
                                break;
                            }
                            return;
                    }
                    if (handler != null) {
                        string = jSONObject3.getString("I");
                        if (!string.isEmpty()) {
                            bundle.putInt("T", length);
                            bundle.putString("Ti", jSONObject3.getString("N"));
                            bundle.putString("P", jSONObject3.getString("P"));
                            bundle.putString("I", string);
                            bundle.putInt("C", jSONObject3.getInt("C"));
                            bundle.putInt("H", jSONObject3.getInt("H"));
                            bundle.putInt("R", jSONObject3.getInt("R"));
                            message.setData(bundle);
                            handler.handleMessage(message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 45:
                message.what = 22;
                if (JPStack.top() instanceof OLPlayRoom) {
                    bundle.putString("MSG", str);
                    message.setData(bundle);
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                    bundle.putString("MSG", str);
                    message.setData(bundle);
                    ((OLPlayKeyboardRoom) JPStack.top()).olPlayKeyboardRoomHandler.handleMessage(message);
                }
                break;
            default:
                break;
        }
    }

    static void loadRoomList(OnlineResponse.Message msg) {
        Bundle bundle = new Bundle();
        Message message = new Message();
        OLPlayHall olPlayHall = null;
        message.what = 3;
        int i = 0;
        OnlineResponse.LoadRoomList loadRoomList = msg.getLoadRoomList();
        for (OnlineResponse.Room roomRaw : loadRoomList.getRoomList()) {
            Bundle bundle2 = new Bundle();
            Room room = new Room((byte) roomRaw.getRoomId(), roomRaw.getRoomName(), roomRaw.getFemaleNum(),
                    roomRaw.getMaleNum(), roomRaw.getIsPlaying() ? 1 : 0, roomRaw.getIsEncrypt() ? 1 : 0,
                    roomRaw.getColor(), roomRaw.getCloseNum(), roomRaw.getRoomMode());
            if (JPStack.top() instanceof OLPlayHall) {
                olPlayHall = (OLPlayHall) JPStack.top();
                olPlayHall.mo2825a(room.getRoomID(), room);
                bundle2.putByte("I", room.getRoomID());
                bundle2.putString("N", room.getRoomName());
                bundle2.putIntArray("UA", room.getPeople());
                bundle2.putBoolean("IF", room.getPeopleFull());
                bundle2.putInt("IP", room.getIsPlaying());
                bundle2.putInt("PA", room.getIsPassword());
                bundle2.putInt("V", room.getRoomKuang());
                bundle2.putInt("D", room.getRoomMode());
                bundle.putBundle(String.valueOf(i), bundle2);
            }
        }
        message.setData(bundle);
        olPlayHall.olPlayHallHandler.handleMessage(message);
    }

    static void m3951a(String str, String str2) {
        if (JPStack.top() instanceof OLPlayHall) {
            OLPlayHall olPlayHall = (OLPlayHall) JPStack.top();
            try {
                JSONObject jSONObject = new JSONObject(str);
                int i = jSONObject.getInt("G");
                Message message = new Message();
                Bundle bundle;
                if (str2.equals("H")) {
                    bundle = new Bundle();
                    switch (i) {
                        case 0:
                            bundle.putString("result", "房间数已满!");
                            message.what = 4;
                            break;
                        case 1:
                            message.what = 2;
                            bundle.putString("R", jSONObject.getString("R"));
                            bundle.putByte("ID", (byte) jSONObject.getInt("I"));
                            bundle.putString("isHost", str2);
                            bundle.putInt("mode", jSONObject.getInt("M"));
                            break;
                    }
                    message.setData(bundle);
                } else if (str2.equals("G")) {
                    bundle = new Bundle();
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
                            bundle.putString("R", jSONObject.getString("R"));
                            bundle.putByte("ID", (byte) jSONObject.getInt("I"));
                            bundle.putString("isHost", str2);
                            bundle.putInt("mode", jSONObject.getInt("M"));
                            break;
                    }
                    message.setData(bundle);
                }
                olPlayHall.olPlayHallHandler.handleMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    static void m3953b(int b, String str, OnlineResponse.Message msg) {
        int i = 0;
        Message message = new Message();
        Bundle bundle;
        int i2;
        Bundle bundle2;
        switch (b) {
            case (byte) 28:
                bundle = new Bundle();
                Bundle bundle3 = new Bundle();
                message.what = 0;
                OnlineResponse.LoadUser loadUser = msg.getLoadUser();
                try {
                    for (OnlineResponse.Hall hall : loadUser.getHallList()) {
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
                    bundle.putInt("X", loadUser.getLvUpNeedExp());
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
            case (byte) 33:
                OnlineResponse.ChangeClothes changeClothes = msg.getChangeClothes();
                int type = changeClothes.getType();
                message.what = type;
                Bundle bundle5 = new Bundle();
                switch (type) {
                    case 0:  // 保存服装
                        bundle5.putString("I", changeClothes.getMessage());
                        break;
                    case 1:  // 进入换衣间加载音符和解锁情况
                        bundle5.putString("G", String.valueOf(changeClothes.getGold()));
                        bundle5.putByteArray("U", GZIP.ZIPToArray(changeClothes.getUnlock()));
                        break;
                    case 2:  // 购买服装
                        bundle5.putString("I", changeClothes.getMessage());
                        bundle5.putString("G", String.valueOf(changeClothes.getGold()));
                        bundle5.putInt("U_T", changeClothes.getBuyClothesType());
                        bundle5.putInt("U_I", changeClothes.getBuyClothesId());
                        break;
                }
                message.setData(bundle5);
                if (JPStack.top() instanceof OLPlayDressRoom) {
                    ((OLPlayDressRoom) JPStack.top()).olPlayDressRoomHandler.handleMessage(message);
                }
                return;
            case (byte) 34:
                bundle = new Bundle();
                OnlineResponse.LoadUserInfo loadUserInfo = msg.getLoadUserInfo();
                type = loadUserInfo.getType();
                switch (type) {
                    case 1:
                        i2 = loadUserInfo.getLoadUserFriend().getLocation();
                        for (OnlineResponse.FriendUser friendUser : loadUserInfo.getLoadUserFriend().getFriendUserList()) {
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
                        for (OnlineResponse.Mail mail : loadUserInfo.getLoadUserMail().getMailList()) {
                            Bundle bundle6 = new Bundle();
                            bundle6.putString("F", mail.getUserFrom());
                            bundle6.putString("M", mail.getMessage());
                            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(new Date(mail.getTime()));
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
                        OnlineResponse.LoadUserCouple loadUserCouple = loadUserInfo.getLoadUserCouple();
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
            case (byte) 36:
                bundle = new Bundle();
                OnlineResponse.LoadUserList loadUserList = msg.getLoadUserList();
                try {
                    i2 = loadUserList.getIsInRoom() ? 1 : 0;
                    for (OnlineResponse.User user : loadUserList.getUserList()) {
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

    static void m3954b(String str) {
        Bundle bundle = new Bundle();
        Message message = new Message();
        try {
            if (JPStack.top() instanceof OLPlayHall) {
                JSONObject jSONObject = new JSONObject(str);
                JSONArray jSONArray = jSONObject.getJSONArray("L");
                int i = jSONObject.getInt("R");
                OLPlayHall olPlayHall = (OLPlayHall) JPStack.top();
                Bundle bundle2 = new Bundle();
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    Bundle bundle3 = new Bundle();
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                    bundle3.putString("U", jSONObject2.getString("U"));
                    bundle3.putString("S", jSONObject2.getString("S"));
                    bundle3.putInt("LV", jSONObject2.getInt("LV"));
                    bundle3.putInt("R", i);
                    bundle2.putBundle(String.valueOf(i2), bundle3);
                }
                bundle.putBundle("L", bundle2);
                bundle.putInt("R", i);
                bundle.putInt("S", jSONObject.getInt("S"));
                bundle.putInt("P", jSONObject.getInt("P"));
                message.setData(bundle);
                message.what = 12;
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    static void changeRoomList(OnlineResponse.Message msg) {
        Bundle bundle = new Bundle();
        Message message = new Message();
        message.what = 3;
        if (JPStack.top() instanceof OLPlayHall) {
            OLPlayHall olPlayHall = (OLPlayHall) JPStack.top();
            assert olPlayHall != null;
            OnlineResponse.Room roomRaw = msg.getRoomListChange();
            Room room = new Room((byte) roomRaw.getRoomId(), roomRaw.getRoomName(), roomRaw.getFemaleNum(),
                    roomRaw.getMaleNum(), roomRaw.getIsPlaying() ? 1 : 0, roomRaw.getIsEncrypt() ? 1 : 0,
                    roomRaw.getColor(), roomRaw.getCloseNum(), roomRaw.getRoomMode());
            if (room.getFCount() + room.getMCount() == 0) {
                olPlayHall.roomTitleMap.remove(room.getRoomID());
            } else {
                olPlayHall.mo2825a(room.getRoomID(), room);
            }
            Iterator it = olPlayHall.roomTitleMap.values().iterator();
            int i = 0;
            while (true) {
                int i2 = i;
                if (it.hasNext()) {
                    room = (Room) it.next();
                    room.getRoomName();
                    Bundle bundle2 = new Bundle();
                    bundle2.putByte("I", room.getRoomID());
                    bundle2.putString("N", room.getRoomName());
                    bundle2.putIntArray("UA", room.getPeople());
                    bundle2.putBoolean("IF", room.getPeopleFull());
                    bundle2.putInt("IP", room.getIsPlaying());
                    bundle2.putInt("PA", room.getIsPassword());
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

    static void m3956d(String str) {
        try {
            Message message = new Message();
            message.what = 1;
            JSONArray jSONArray = new JSONArray(str);
            int length = jSONArray.length();
            if (JPStack.top() instanceof OLPlayRoom) {
                User user;
                OLPlayRoom olPlayRoom = (OLPlayRoom) JPStack.top();
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 >= length) {
                        break;
                    }
                    JSONObject jSONObject = jSONArray.getJSONObject(i2);
                    user = jSONObject.getString("N").isEmpty() ?
                            new User((byte) jSONObject.getInt("P"), jSONObject.getString("N"), null, jSONObject.getString("S"), jSONObject.getString("R"), jSONObject.getString("H"), jSONObject.getInt("L"), jSONObject.getInt("V"), jSONObject.getInt("C"), jSONObject.getInt("G"), jSONObject.getInt("O"), jSONObject.getString("I"))
                            : new User((byte) jSONObject.getInt("P"), jSONObject.getString("N"), jSONObject.getJSONObject("F"), jSONObject.getString("S"), jSONObject.getString("R"), jSONObject.getString("H"), jSONObject.getInt("L"), jSONObject.getInt("V"), jSONObject.getInt("C"), jSONObject.getInt("G"), jSONObject.getInt("O"), jSONObject.getString("I"));
                    olPlayRoom.putJPhashMap(user.getPosition(), user);
                    i = i2 + 1;
                }
                Bundle bundle = new Bundle();
                Iterator it = olPlayRoom.jpapplication.getHashmap().values().iterator();
                i = 0;
                while (true) {
                    int i3 = i;
                    if (it.hasNext()) {
                        user = (User) it.next();
                        Bundle bundle2 = new Bundle();
                        bundle2.putByte("PI", user.getPosition());
                        bundle2.putString("N", user.getPlayerName());
                        bundle2.putString("S", user.getSex());
                        bundle2.putString("IR", user.getStatus());
                        bundle2.putString("IH", user.getIsHost());
                        bundle2.putInt("IV", user.getKuang());
                        bundle2.putInt("LV", user.getLevel());
                        bundle2.putInt("TR", user.getTrousers());
                        bundle2.putInt("JA", user.getJacket());
                        bundle2.putInt("EY", user.getEye());
                        bundle2.putInt("HA", user.getHair());
                        bundle2.putInt("SH", user.getShoes());
                        bundle2.putInt("CL", user.getCLevel());
                        bundle2.putInt("GR", user.getHand());
                        bundle2.putInt("CP", user.getCpKind());
                        bundle2.putString("I", user.getFamilyID());
                        bundle.putBundle(String.valueOf(i3), bundle2);
                        i = i3 + 1;
                    } else {
                        bundle.putString("SI", "");
                        bundle.putInt("diao", 0);
                        bundle.putString("MSG", "");
                        message.setData(bundle);
                        olPlayRoom.olPlayRoomHandler.handleMessage(message);
                        return;
                    }
                }
            } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
                User user;
                OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) JPStack.top();
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 >= length) {
                        break;
                    }
                    JSONObject jSONObject = jSONArray.getJSONObject(i2);
                    user = jSONObject.getString("N").isEmpty() ?
                            new User((byte) jSONObject.getInt("P"), jSONObject.getString("N"), null, jSONObject.getString("S"), jSONObject.getString("R"), jSONObject.getString("H"), jSONObject.getInt("L"), jSONObject.getInt("V"), jSONObject.getInt("C"), jSONObject.getInt("G"), jSONObject.getInt("O"), jSONObject.getString("I"))
                            : new User((byte) jSONObject.getInt("P"), jSONObject.getString("N"), jSONObject.getJSONObject("F"), jSONObject.getString("S"), jSONObject.getString("R"), jSONObject.getString("H"), jSONObject.getInt("L"), jSONObject.getInt("V"), jSONObject.getInt("C"), jSONObject.getInt("G"), jSONObject.getInt("O"), jSONObject.getString("I"));
                    olPlayKeyboardRoom.putJPhashMap(user.getPosition(), user);
                    i = i2 + 1;
                }
                Bundle bundle = new Bundle();
                Iterator it = olPlayKeyboardRoom.jpapplication.getHashmap().values().iterator();
                i = 0;
                while (true) {
                    int i3 = i;
                    if (it.hasNext()) {
                        user = (User) it.next();
                        Bundle bundle2 = new Bundle();
                        bundle2.putByte("PI", user.getPosition());
                        bundle2.putString("N", user.getPlayerName());
                        bundle2.putString("S", user.getSex());
                        bundle2.putString("IR", user.getStatus());
                        bundle2.putString("IH", user.getIsHost());
                        bundle2.putInt("IV", user.getKuang());
                        bundle2.putInt("LV", user.getLevel());
                        bundle2.putInt("TR", user.getTrousers());
                        bundle2.putInt("JA", user.getJacket());
                        bundle2.putInt("EY", user.getEye());
                        bundle2.putInt("HA", user.getHair());
                        bundle2.putInt("SH", user.getShoes());
                        bundle2.putInt("CL", user.getCLevel());
                        bundle2.putInt("GR", user.getHand());
                        bundle2.putInt("CP", user.getCpKind());
                        bundle2.putString("I", user.getFamilyID());
                        bundle.putBundle(String.valueOf(i3), bundle2);
                        i = i3 + 1;
                    } else {
                        bundle.putString("SI", "");
                        bundle.putInt("diao", 0);
                        bundle.putString("MSG", "");
                        message.setData(bundle);
                        olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                        return;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void m3957e(String str) {
        if (JPStack.top() instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) JPStack.top();
            try {
                JSONObject jSONObject = new JSONObject(str);
                JSONArray jSONArray = new JSONArray(GZIP.ZIPTo(jSONObject.getString("P")));
                Message message = new Message();
                message.what = 1;
                Bundle bundle = new Bundle();
                int length = jSONArray.length();
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 >= length) {
                        bundle.putString("SI", jSONObject.getString("S"));
                        bundle.putInt("diao", jSONObject.getInt("D"));
                        bundle.putString("MSG", jSONObject.getString("MSG"));
                        message.setData(bundle);
                        olPlayRoom.olPlayRoomHandler.handleMessage(message);
                        return;
                    }
                    Bundle bundle2 = new Bundle();
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                    User user = jSONObject2.getString("N").isEmpty() ?
                            new User((byte) jSONObject2.getInt("P"), jSONObject2.getString("N"), null, jSONObject2.getString("S"), jSONObject2.getString("R"), jSONObject2.getString("H"), jSONObject2.getInt("L"), jSONObject2.getInt("V"), jSONObject2.getInt("C"), jSONObject2.getInt("G"), jSONObject2.getInt("O"), jSONObject2.getString("I"))
                            : new User((byte) jSONObject2.getInt("P"), jSONObject2.getString("N"), jSONObject2.getJSONObject("F"), jSONObject2.getString("S"), jSONObject2.getString("R"), jSONObject2.getString("H"), jSONObject2.getInt("L"), jSONObject2.getInt("V"), jSONObject2.getInt("C"), jSONObject2.getInt("G"), jSONObject2.getInt("O"), jSONObject2.getString("I"));
                    olPlayRoom.putJPhashMap(user.getPosition(), user);
                    bundle2.putByte("PI", user.getPosition());
                    bundle2.putString("N", user.getPlayerName());
                    bundle2.putString("S", user.getSex());
                    bundle2.putString("IR", user.getStatus());
                    bundle2.putString("IH", user.getIsHost());
                    bundle2.putInt("IV", user.getKuang());
                    bundle2.putInt("LV", user.getLevel());
                    bundle2.putInt("TR", user.getTrousers());
                    bundle2.putInt("JA", user.getJacket());
                    bundle2.putInt("EY", user.getEye());
                    bundle2.putInt("HA", user.getHair());
                    bundle2.putInt("SH", user.getShoes());
                    bundle2.putInt("CL", user.getCLevel());
                    bundle2.putInt("GR", user.getHand());
                    bundle2.putInt("CP", user.getCpKind());
                    bundle2.putString("I", user.getFamilyID());
                    bundle.putBundle(String.valueOf(i2), bundle2);
                    i = i2 + 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) JPStack.top();
            try {
                JSONObject jSONObject = new JSONObject(str);
                JSONArray jSONArray = new JSONArray(GZIP.ZIPTo(jSONObject.getString("P")));
                Message message = new Message();
                message.what = 1;
                Bundle bundle = new Bundle();
                int length = jSONArray.length();
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 >= length) {
                        bundle.putString("SI", jSONObject.getString("S"));
                        bundle.putInt("diao", jSONObject.getInt("D"));
                        bundle.putString("MSG", jSONObject.getString("MSG"));
                        message.setData(bundle);
                        olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                        return;
                    }
                    Bundle bundle2 = new Bundle();
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                    User user = jSONObject2.getString("N").isEmpty() ?
                            new User((byte) jSONObject2.getInt("P"), jSONObject2.getString("N"), null, jSONObject2.getString("S"), jSONObject2.getString("R"), jSONObject2.getString("H"), jSONObject2.getInt("L"), jSONObject2.getInt("V"), jSONObject2.getInt("C"), jSONObject2.getInt("G"), jSONObject2.getInt("O"), jSONObject2.getString("I"))
                            : new User((byte) jSONObject2.getInt("P"), jSONObject2.getString("N"), jSONObject2.getJSONObject("F"), jSONObject2.getString("S"), jSONObject2.getString("R"), jSONObject2.getString("H"), jSONObject2.getInt("L"), jSONObject2.getInt("V"), jSONObject2.getInt("C"), jSONObject2.getInt("G"), jSONObject2.getInt("O"), jSONObject2.getString("I"));
                    olPlayKeyboardRoom.putJPhashMap(user.getPosition(), user);
                    bundle2.putByte("PI", user.getPosition());
                    bundle2.putString("N", user.getPlayerName());
                    bundle2.putString("S", user.getSex());
                    bundle2.putString("IR", user.getStatus());
                    bundle2.putString("IH", user.getIsHost());
                    bundle2.putInt("IV", user.getKuang());
                    bundle2.putInt("LV", user.getLevel());
                    bundle2.putInt("TR", user.getTrousers());
                    bundle2.putInt("JA", user.getJacket());
                    bundle2.putInt("EY", user.getEye());
                    bundle2.putInt("HA", user.getHair());
                    bundle2.putInt("SH", user.getShoes());
                    bundle2.putInt("CL", user.getCLevel());
                    bundle2.putInt("GR", user.getHand());
                    bundle2.putInt("CP", user.getCpKind());
                    bundle2.putString("I", user.getFamilyID());
                    bundle.putBundle(String.valueOf(i2), bundle2);
                    i = i2 + 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    static void keyboardNotes(String str) {
        if (JPStack.top() instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) JPStack.top();
            try {
                byte[] keyboardNotes = str.getBytes(StandardCharsets.ISO_8859_1);
                Bundle bundle = new Bundle();
                bundle.putByteArray("NOTES", keyboardNotes);
                Message message = new Message();
                message.what = 5;
                message.setData(bundle);
                olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void challenge(String str) {
        Message message = new Message();
        try {
            JSONObject jSONObject = new JSONObject(str);
            switch (jSONObject.getInt("K")) {
                case 1:
                    if (JPStack.top() instanceof OLChallenge) {
                        OLChallenge challenge = (OLChallenge) JPStack.top();
                        JSONArray jSONArray = new JSONArray(GZIP.ZIPTo(jSONObject.getString("L")));
                        message.what = 1;
                        assert challenge != null;
                        Bundle bundle = new Bundle();
                        int length = jSONArray.length();
                        int i = 0;
                        while (true) {
                            int i2 = i;
                            if (i2 >= length) {
                                bundle.putInt("S", jSONObject.getInt("S"));
                                bundle.putString("P", jSONObject.getString("P"));
                                bundle.putString("T", jSONObject.getString("T"));
                                bundle.putString("Z", jSONObject.getString("Z"));
                                message.setData(bundle);
                                challenge.challengeHandler.handleMessage(message);
                                return;
                            }
                            Bundle bundle2 = new Bundle();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                            bundle2.putString("N", jSONObject2.getString("N"));
                            bundle2.putString("S", jSONObject2.getString("S"));
                            bundle2.putString("T", jSONObject2.getString("T"));
                            bundle.putBundle(String.valueOf(i2), bundle2);
                            i = i2 + 1;
                        }
                    }
                    break;
                case 2:
                    if (JPStack.top() instanceof OLChallenge) {
                        OLChallenge challenge = (OLChallenge) JPStack.top();
                        assert challenge != null;
                        challenge.jpprogressBar.dismiss();
                        message.what = 2;
                        Bundle bundle = new Bundle();
                        bundle.putInt("R", jSONObject.getInt("R"));
                        bundle.putString("I", jSONObject.getString("I"));
                        bundle.putString("P", GZIP.ZIPTo(jSONObject.getString("P")));
                        message.setData(bundle);
                        challenge.challengeHandler.handleMessage(message);
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
                        bundle.putString("I", jSONObject.getString("I"));
                        message.setData(bundle);
                        message.what = 9;
                        assert pianoPlay != null;
                        pianoPlay.pianoPlayHandler.handleMessage(message);
                        return;
                    }
                    break;
                case 5:
                    if (JPStack.top() instanceof OLChallenge) {
                        OLChallenge challenge = (OLChallenge) JPStack.top();
                        message.what = 5;
                        Bundle bundle = new Bundle();
                        bundle.putInt("P", jSONObject.getInt("P"));
                        bundle.putString("N", jSONObject.getString("N"));
                        message.setData(bundle);
                        challenge.challengeHandler.handleMessage(message);
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void family(OnlineResponse.Message msg) {
        Message message = new Message();
        OnlineResponse.Family family = msg.getFamily();
        switch (family.getType()) {
            case 1:
                if (JPStack.top() instanceof OLFamily) {
                    OLFamily olFamily = (OLFamily) JPStack.top();
                    assert olFamily != null;
                    message.what = 1;
                    olFamily.jpprogressBar.dismiss();
                    Bundle bundle = new Bundle();
                    int i = 0;
                    for (OnlineResponse.FamilyUser familyUser : family.getFamilyEnter().getFamilyUserList()) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("N", familyUser.getName());
                        bundle2.putString("C", String.valueOf(familyUser.getContribution()));
                        bundle2.putString("L", String.valueOf(familyUser.getLv()));
                        bundle2.putString("S", familyUser.getGender());
                        bundle2.putString("O", String.valueOf(familyUser.getOnline() ? 1 : 0));
                        bundle2.putString("P", String.valueOf(familyUser.getPosition()));
                        bundle.putBundle(String.valueOf(i), bundle2);
                        i++;
                    }
                    bundle.putString("D", family.getFamilyEnter().getDeclaration());
                    String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                            Locale.CHINESE).format(new Date(family.getFamilyEnter().getCreateDate()));
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
                    for (OnlineResponse.FamilyInfo familyInfo : family.getFamilyList().getFamilyInfoList()) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("N", familyInfo.getName());
                        bundle2.putString("C", String.valueOf(familyInfo.getContribution()));
                        bundle2.putString("T", String.valueOf(familyInfo.getCapacity()));
                        bundle2.putString("U", String.valueOf(familyInfo.getSize()));
                        bundle2.putString("I", String.valueOf(familyInfo.getFamilyId()));
                        bundle2.putByteArray("J", GZIP.ZIPToArray(familyInfo.getPicture()));
                        bundle.putBundle(String.valueOf(i), bundle2);
                        i++;
                    }
                    bundle.putString("C", String.valueOf(family.getFamilyList().getContribution()));
                    bundle.putString("P", String.valueOf(family.getFamilyList().getPosition()));
                    bundle.putString("N", family.getFamilyList().getName());
                    bundle.putString("T", String.valueOf(family.getFamilyList().getCapacity()));
                    bundle.putString("U", String.valueOf(family.getFamilyList().getSize()));
                    bundle.putString("I", String.valueOf(family.getFamilyList().getFamilyId()));
                    bundle.putByteArray("J", GZIP.ZIPToArray(family.getFamilyList().getPicture()));
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

    static void shop(OnlineResponse.Message msg) {
        OnlineResponse.Shop shop = msg.getShop();
        Message message = new Message();
        switch (shop.getType()) {
            case 1:  // 加载商品
                if (JPStack.top() instanceof OLPlayDressRoom) {
                    OLPlayDressRoom olPlayDressRoom = (OLPlayDressRoom) JPStack.top();
                    assert olPlayDressRoom != null;
                    message.what = 3;
                    olPlayDressRoom.jpprogressBar.dismiss();
                    Bundle bundle = new Bundle();
                    int i = 0;
                    for (OnlineResponse.ShopProduct shopProduct : shop.getShopProductShow().getProductList()) {
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

    static void daily(String str) {
        Message message = new Message();
        try {
            JSONObject jSONObject = new JSONObject(str);
            switch (jSONObject.getInt("K")) {
                case 0:
                    break;
                case 1:
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        OLPlayHallRoom ophr = (OLPlayHallRoom) JPStack.top();
                        JSONArray jSONArray = new JSONArray(GZIP.ZIPTo(jSONObject.getString("L")));
                        assert ophr != null;
                        message.what = 11;
                        ophr.jpprogressBar.dismiss();
                        Bundle bundle = new Bundle();
                        int length = jSONArray.length();
                        int i = 0;
                        while (true) {
                            int i2 = i;
                            if (i2 >= length) {
                                bundle.putString("M", jSONObject.getString("M"));
                                bundle.putString("T", jSONObject.getString("T"));
                                message.setData(bundle);
                                ophr.olPlayHallRoomHandler.handleMessage(message);
                                return;
                            }
                            Bundle bundle2 = new Bundle();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                            bundle2.putString("N", jSONObject2.getString("N"));
                            bundle2.putString("T", jSONObject2.getString("T"));
                            bundle2.putString("B", jSONObject2.getString("B"));
                            bundle2.putString("G", jSONObject2.getString("G"));
                            bundle.putBundle(String.valueOf(i2), bundle2);
                            i = i2 + 1;
                        }
                    }
                case 2:
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        OLPlayHallRoom ophr = (OLPlayHallRoom) JPStack.top();
                        Bundle bundle = new Bundle();
                        bundle.putString("M", jSONObject.getString("M"));
                        message.setData(bundle);
                        message.what = 12;
                        assert ophr != null;
                        ophr.jpprogressBar.dismiss();
                        ophr.olPlayHallRoomHandler.handleMessage(message);
                        return;
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
