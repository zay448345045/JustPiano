package ly.pp.justpiano3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

final class JsonHandle {
    static ByteBuffer m3945a(byte b, byte b2, byte b3, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        byte[] obj = new byte[]{b, b2, b3};
        byte[] obj2 = new byte[(3 + bytes.length)];
        System.arraycopy(obj, 0, obj2, 0, 3);
        System.arraycopy(bytes, 0, obj2, 3, bytes.length);
        return ByteBuffer.wrap(obj2);
    }

    static ByteBuffer m3946a(byte b, byte b2, byte b3, byte[] bArr) {
        byte[] bArr2 = new byte[]{b, b2, b3};
        ByteBuffer allocate = ByteBuffer.allocateDirect(bArr.length + bArr2.length);
        allocate.put(bArr2);
        allocate.put(bArr);
        return allocate;
    }

    static ByteBuffer m3947a(String str, String str2, String str3, String str4, String str5) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("P", str3);
            jSONObject.put("V", str4);
            jSONObject.put("N", str);
            jSONObject.put("K", str2);
            jSONObject.put("C", str5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JsonHandle.m3945a((byte) 10, (byte) 0, (byte) 0, jSONObject.toString());
    }

    static void m3948a(byte b, String str) {
        int i = 0;
        Message message = new Message();
        try {
            JPStack.create();
            if (JPStack.top() instanceof PianoPlay) {
                JPStack.create();
                PianoPlay pianoPlay = (PianoPlay) JPStack.top();
                JSONObject jSONObject = new JSONObject(str);
                User User = (User) pianoPlay.userMap.get((byte) jSONObject.getInt("I"));
                Bundle bundle = new Bundle();
                Bundle bundle2;
                User User2;
                if (b == (byte) 25) {
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
                }
                message.what = 2;
                message.setData(bundle);
                pianoPlay.pianoPlayHandler.handleMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void m3949a(int i, String str) {
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
                        JPStack.create();
                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                    } else if (JPStack.top() instanceof OLFamily) {
                        ((OLFamily) JPStack.top()).familyHandler.handleMessage(message);
                    } else if (JPStack.top() instanceof OLPlayHall) {
                        ((OLPlayHall) JPStack.top()).olPlayHallHandler.handleMessage(message);
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
                    bundle2.putInt("T", jSONObject4.getInt("T"));
                    bundle2.putInt("D", jSONObject4.getInt("D"));
                    message.setData(bundle2);
                    JPStack.create();
                    if (JPStack.top() instanceof OLPlayRoom) {
                        JPStack.create();
                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
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
                    JPStack.create();
                    if (JPStack.top() instanceof PianoPlay) {
                        JPStack.create();
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
                JPStack.create();
                if (JPStack.top() instanceof OLPlayRoom) {
                    JPStack.create();
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                    return;
                }
                return;
            case 13:
                try {
                    jSONObject2 = new JSONObject(str);
                    message.what = 2;
                    bundle.putString("U", jSONObject2.getString("U"));
                    bundle.putString("M", jSONObject2.getString("M"));
                    bundle.putInt("T", jSONObject2.getInt("T"));
                    if (jSONObject2.getInt("T") == 1) {
                        bundle.putInt("V", jSONObject2.getInt("V"));
                    }
                    message.setData(bundle);
                    JPStack.create();
                    if (JPStack.top() instanceof OLPlayRoom) {
                        JPStack.create();
                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                        return;
                    }
                    return;
                } catch (JSONException e22) {
                    e22.printStackTrace();
                    return;
                }
            case 14:
                try {
                    jSONObject2 = new JSONObject(str);
                    message.what = 10;
                    bundle.putString("R", jSONObject2.getString("R"));
                    message.setData(bundle);
                    JPStack.create();
                    if (JPStack.top() instanceof OLPlayRoom) {
                        JPStack.create();
                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
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
                    JPStack.create();
                    if (JPStack.top() instanceof OLPlayRoom) {
                        JPStack.create();
                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case 17:
                try {
                    jSONObject3 = new JSONObject(str);
                    JPStack.create();
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        JPStack.create();
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
                    JPStack.create();
                    if (JPStack.top() instanceof PianoPlay) {
                        JPStack.create();
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
                    bundle.putInt("D", jSONObject2.getInt("D"));
                    bundle.putInt("T", jSONObject2.getInt("T"));
                    message.setData(bundle);
                    JPStack.create();
                    if (JPStack.top() instanceof OLPlayRoom) {
                        JPStack.create();
                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
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
                            JPStack.create();
                            if (JPStack.top() instanceof OLPlayRoom) {
                                JPStack.create();
                                handler = ((OLPlayRoom) JPStack.top()).olPlayRoomHandler;
                                message.what = 9;
                                break;
                            }
                            return;
                        case 1:
                            JPStack.create();
                            if (JPStack.top() instanceof OLPlayHall) {
                                JPStack.create();
                                handler = ((OLPlayHall) JPStack.top()).olPlayHallHandler;
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
                    JPStack.create();
                    if (JPStack.top() instanceof PianoPlay) {
                        JPStack.create();
                        ((PianoPlay) JPStack.top()).pianoPlayHandler.handleMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            case 37:
                try {
                    jSONObject3 = new JSONObject(str);
                    length = jSONObject3.getInt("T");
                    handler = null;
                    switch (jSONObject3.getInt("S")) {
                        case 0:
                            JPStack.create();
                            if (JPStack.top() instanceof OLPlayRoom) {
                                JPStack.create();
                                handler = ((OLPlayRoom) JPStack.top()).olPlayRoomHandler;
                                message.what = 14;
                                break;
                            }
                            return;
                        case 1:
                            JPStack.create();
                            if (JPStack.top() instanceof OLPlayHall) {
                                JPStack.create();
                                handler = ((OLPlayHall) JPStack.top()).olPlayHallHandler;
                                message.what = 9;
                                break;
                            }
                            return;
                        case 2:
                            JPStack.create();
                            if (JPStack.top() instanceof OLPlayHallRoom) {
                                JPStack.create();
                                handler = ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler;
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
            case 45:
                message.what = 22;
                JPStack.create();
                if (JPStack.top() instanceof OLPlayRoom) {
                    bundle.putString("MSG", str);
                    message.setData(bundle);
                    JPStack.create();
                    ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                }
            default:
        }
    }

    static void m3950a(String str) {
        Bundle bundle = new Bundle();
        Message message = new Message();
        OLPlayHall olPlayHall = null;
        message.what = 3;
        try {
            JSONArray jSONArray = new JSONArray(GZIP.ZIPTo(new JSONObject(str).getString("L")));
            int length = jSONArray.length();
            if (length > 0) {
                int i = 0;
                while (i < length) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    Bundle bundle2 = new Bundle();
                    Room room = new Room((byte) jSONObject.getInt("I"), jSONObject.getString("N"), jSONObject.getInt("F"), jSONObject.getInt("M"), jSONObject.getInt("S"), jSONObject.getInt("P"), jSONObject.getInt("V"), jSONObject.getInt("C"), jSONObject.getInt("D"));
                    JPStack.create();
                    if (JPStack.top() instanceof OLPlayHall) {
                        JPStack.create();
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
                        i++;
                    } else {
                        return;
                    }
                }
                message.setData(bundle);
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void m3951a(String str, String str2) {
        JPStack.create();
        if (JPStack.top() instanceof OLPlayHall) {
            JPStack.create();
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

    static ByteBuffer m3952b(byte b, byte b2, byte b3, String str) {
        switch (b) {
            case (byte) 3:
            case (byte) 4:
            case (byte) 5:
            case (byte) 12:
            case (byte) 13:
            case (byte) 14:
            case (byte) 15:
            case (byte) 25:
                return JsonHandle.m3945a(b, b2, b3, str);
            default:
                return null;
        }
    }

    static void m3953b(byte b, String str) {
        int i = 0;
        Message message = new Message();
        Bundle bundle;
        JSONObject jSONObject;
        JSONObject jSONObject2;
        int i2;
        JSONArray jSONArray;
        int length;
        Bundle bundle2;
        switch (b) {
            case (byte) 28:
                bundle = new Bundle();
                Bundle bundle3 = new Bundle();
                message.what = 0;
                try {
                    JSONObject jSONObject3 = new JSONObject(str);
                    JSONArray jSONArray2 = new JSONArray(GZIP.ZIPTo(jSONObject3.getString("L")));
                    int length2 = jSONArray2.length();
                    while (i < length2) {
                        jSONObject = jSONArray2.getJSONObject(i);
                        Bundle bundle4 = new Bundle();
                        bundle4.putByte("I", (byte) jSONObject.getInt("I"));
                        bundle4.putString("N", jSONObject.getString("N"));
                        bundle4.putInt("PN", jSONObject.getInt("P"));
                        bundle4.putInt("TN", jSONObject.getInt("T"));
                        bundle4.putInt("W", jSONObject.getInt("W"));
                        bundle3.putBundle(String.valueOf(i), bundle4);
                        i++;
                    }
                    bundle.putBundle("L", bundle3);
                    bundle.putString("U", jSONObject3.getString("U"));
                    bundle.putString("S", jSONObject3.getString("S"));
                    bundle.putString("I", jSONObject3.getString("I"));
                    bundle.putInt("LV", jSONObject3.getInt("LV"));
                    bundle.putInt("CL", jSONObject3.getInt("CL"));
                    bundle.putInt("E", jSONObject3.getInt("E"));
                    bundle.putInt("X", jSONObject3.getInt("X"));
                    bundle.putInt("M", jSONObject3.getInt("M"));
                    bundle.putString("DR", jSONObject3.getString("DR"));
                    bundle.putInt("CP", jSONObject3.getInt("CP"));
                    message.setData(bundle);
                    JPStack.create();
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        JPStack.create();
                        ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                        return;
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            case (byte) 33:
                try {
                    JSONObject jSONObject3 = new JSONObject(str);
                    int type = jSONObject3.getInt("T");
                    message.what = type;
                    Bundle bundle5 = new Bundle();
                    switch (type) {
                        case 0:  //保存服装
                            bundle5.putString("I", jSONObject3.getString("I"));
                            break;
                        case 1:  //进入换衣间加载音符和解锁情况
                            bundle5.putString("G", jSONObject3.getString("G"));
                            bundle5.putString("U", jSONObject3.getString("U"));
                            break;
                        case 2:  //购买服装
                            bundle5.putString("I", jSONObject3.getString("I"));
                            bundle5.putString("G", jSONObject3.getString("G"));
                            bundle5.putString("U", jSONObject3.getString("U"));
                            break;
                    }
                    message.setData(bundle5);
                    JPStack.create();
                    if (JPStack.top() instanceof OLPlayDressRoom) {
                        JPStack.create();
                        ((OLPlayDressRoom) JPStack.top()).olPlayDressRoomHandler.handleMessage(message);
                    }
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
                return;
            case (byte) 34:
                bundle = new Bundle();
                try {
                    jSONObject2 = new JSONObject(str);
                    String string = jSONObject2.getString("T");
                    switch (string) {
                        case "L":
                            i2 = jSONObject2.getInt("S");
                            jSONArray = new JSONArray(GZIP.ZIPTo(jSONObject2.getString("L")));
                            length = jSONArray.length();
                            if (length > 0) {
                                while (i < length) {
                                    bundle2 = new Bundle();
                                    jSONObject = jSONArray.getJSONObject(i);
                                    bundle2.putString("F", jSONObject.getString("F"));
                                    bundle2.putInt("O", jSONObject.getInt("O"));
                                    bundle2.putString("S", jSONObject.getString("S"));
                                    bundle2.putInt("LV", jSONObject.getInt("LV"));
                                    bundle.putBundle(String.valueOf(i), bundle2);
                                    i++;
                                }
                            }
                            switch (i2) {
                                case 0:
                                    message.what = 3;
                                    new Bundle().putBundle("L", bundle);
                                    message.setData(bundle);
                                    JPStack.create();
                                    if (JPStack.top() instanceof OLPlayHallRoom) {
                                        JPStack.create();
                                        ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                                        return;
                                    }
                                    return;
                                case 1:
                                    message.what = 5;
                                    message.setData(bundle);
                                    JPStack.create();
                                    if (JPStack.top() instanceof OLPlayHall) {
                                        JPStack.create();
                                        ((OLPlayHall) JPStack.top()).olPlayHallHandler.handleMessage(message);
                                        return;
                                    }
                                    return;
                                case 2:
                                    JPStack.create();
                                    if (JPStack.top() instanceof OLPlayRoom) {
                                        message.what = 11;
                                        message.setData(bundle);
                                        JPStack.create();
                                        ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
                                        return;
                                    }
                                    return;
                                default:
                                    return;
                            }
                        case "M":
                            JSONArray jSONArray3 = new JSONArray(GZIP.ZIPTo(jSONObject2.getString("L")));
                            length = jSONArray3.length();
                            if (length > 0) {
                                while (i < length) {
                                    Bundle bundle6 = new Bundle();
                                    JSONObject jSONObject4 = jSONArray3.getJSONObject(i);
                                    bundle6.putString("F", jSONObject4.getString("F"));
                                    bundle6.putString("M", jSONObject4.getString("M"));
                                    bundle6.putString("T", jSONObject4.getString("T"));
                                    bundle6.putInt("type", 0);
                                    bundle.putBundle(String.valueOf(i), bundle6);
                                    i++;
                                }
                            }
                            message.what = 4;
                            message.setData(bundle);
                            JPStack.create();
                            if (JPStack.top() instanceof OLPlayHallRoom) {
                                JPStack.create();
                                ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                                return;
                            }
                            return;
                        case "C":
                            bundle.putString("U", jSONObject2.getString("U"));
                            bundle.putString("S", jSONObject2.getString("S"));
                            bundle.putInt("LV", jSONObject2.getInt("L"));
                            bundle.putInt("CL", jSONObject2.getInt("C"));
                            bundle.putString("DR", jSONObject2.getString("D"));
                            bundle.putString("IN", jSONObject2.getString("I"));
                            bundle.putInt("CP", jSONObject2.getInt("P"));
                            message.what = 22;
                            message.setData(bundle);
                            JPStack.create();
                            if (JPStack.top() instanceof OLPlayHallRoom) {
                                JPStack.create();
                                ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                } catch (Exception e3) {
                    e3.printStackTrace();
                    return;
                }
            case (byte) 36:
                bundle = new Bundle();
                try {
                    jSONObject2 = new JSONObject(str);
                    i2 = jSONObject2.getInt("S");
                    jSONArray = new JSONArray(GZIP.ZIPTo(jSONObject2.getString("L")));
                    length = jSONArray.length();
                    if (length > 0) {
                        while (i < length) {
                            bundle2 = new Bundle();
                            jSONObject = jSONArray.getJSONObject(i);
                            bundle2.putString("U", jSONObject.getString("U"));
                            bundle2.putString("S", jSONObject.getString("S"));
                            bundle2.putInt("V", jSONObject.getInt("V"));
                            bundle2.putInt("LV", jSONObject.getInt("LV"));
                            bundle2.putInt("R", jSONObject.getInt("R"));
                            bundle.putBundle(String.valueOf(i), bundle2);
                            i++;
                        }
                    }
                    switch (i2) {
                        case 0:
                            message.what = 7;
                            message.setData(bundle);
                            JPStack.create();
                            if (JPStack.top() instanceof OLPlayHall) {
                                JPStack.create();
                                ((OLPlayHall) JPStack.top()).olPlayHallHandler.handleMessage(message);
                                return;
                            }
                            return;
                        case 1:
                            message.what = 15;
                            message.setData(bundle);
                            JPStack.create();
                            if (JPStack.top() instanceof OLPlayRoom) {
                                JPStack.create();
                                ((OLPlayRoom) JPStack.top()).olPlayRoomHandler.handleMessage(message);
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
            JPStack.create();
            if (JPStack.top() instanceof OLPlayHall) {
                JSONObject jSONObject = new JSONObject(str);
                JSONArray jSONArray = jSONObject.getJSONArray("L");
                int i = jSONObject.getInt("R");
                JPStack.create();
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


    static void m3955c(String str) {
        Bundle bundle = new Bundle();
        Message message = new Message();
        message.what = 3;
        try {
            JPStack.create();
            if (JPStack.top() instanceof OLPlayHall) {
                JPStack.create();
                OLPlayHall olPlayHall = (OLPlayHall) JPStack.top();
                assert olPlayHall != null;
                JSONObject jSONObject = new JSONObject(str);
                Room room = new Room((byte) jSONObject.getInt("I"), jSONObject.getString("N"), jSONObject.getInt("F"), jSONObject.getInt("M"), jSONObject.getInt("S"), jSONObject.getInt("P"), jSONObject.getInt("V"), jSONObject.getInt("C"), jSONObject.getInt("D"));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void m3956d(String str) {
        try {
            Message message = new Message();
            message.what = 1;
            JSONArray jSONArray = new JSONArray(str);
            int length = jSONArray.length();
            JPStack.create();
            if (JPStack.top() instanceof OLPlayRoom) {
                User user;
                JPStack.create();
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void m3957e(String str) {
        JPStack.create();
        if (JPStack.top() instanceof OLPlayRoom) {
            JPStack.create();
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

    static void challenge(String str) {
        Message message = new Message();
        JPStack.create();
        try {
            JSONObject jSONObject = new JSONObject(str);
            switch (jSONObject.getInt("K")) {
                case 1:
                    if (JPStack.top() instanceof OLChallenge) {
                        JPStack.create();
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
                        JPStack.create();
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
                        JPStack.create();
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
                        JPStack.create();
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
                        JPStack.create();
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

    static void family(String str) {
        Message message = new Message();
        JPStack.create();
        try {
            JSONObject jSONObject = new JSONObject(str);
            switch (jSONObject.getInt("K")) {
                case 0:  //退出家族中心，请求界面切换至家族选项卡
                    break;
                case 1:  //进入家族中心
                    if (JPStack.top() instanceof OLFamily) {
                        JPStack.create();
                        OLFamily family = (OLFamily) JPStack.top();
                        JSONArray jSONArray = new JSONArray(GZIP.ZIPTo(jSONObject.getString("L")));
                        assert family != null;
                        message.what = 1;
                        family.jpprogressBar.dismiss();
                        Bundle bundle = new Bundle();
                        int length = jSONArray.length();
                        int i = 0;
                        while (true) {
                            int i2 = i;
                            if (i2 >= length) {
                                bundle.putString("D", jSONObject.getString("D"));
                                bundle.putString("T", jSONObject.getString("T"));
                                bundle.putString("Z", jSONObject.getString("Z"));
                                bundle.putString("N", jSONObject.getString("N"));
                                bundle.putString("C", jSONObject.getString("C"));
                                bundle.putString("P", jSONObject.getString("P"));
                                message.setData(bundle);
                                family.familyHandler.handleMessage(message);
                                return;
                            }
                            Bundle bundle2 = new Bundle();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                            bundle2.putString("N", jSONObject2.getString("N"));
                            bundle2.putString("C", jSONObject2.getString("C"));
                            bundle2.putString("L", jSONObject2.getString("L"));
                            bundle2.putString("S", jSONObject2.getString("S"));
                            bundle2.putString("O", jSONObject2.getString("O"));
                            bundle2.putString("P", jSONObject2.getString("P"));
                            bundle.putBundle(String.valueOf(i2), bundle2);
                            i = i2 + 1;
                        }
                    }
                    break;
                case 2:
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        JPStack.create();
                        OLPlayHallRoom ophr = (OLPlayHallRoom) JPStack.top();
                        JSONArray jSONArray = new JSONArray(GZIP.ZIPTo(jSONObject.getString("L")));
                        assert ophr != null;
                        message.what = 2;
                        ophr.jpprogressBar.dismiss();
                        Bundle bundle = new Bundle();
                        int length = jSONArray.length();
                        int i = 0;
                        while (true) {
                            int i2 = i;
                            if (i2 >= length) {
                                bundle.putString("C", jSONObject.getString("C"));
                                bundle.putString("P", jSONObject.getString("P"));
                                bundle.putString("N", jSONObject.getString("N"));
                                bundle.putString("T", jSONObject.getString("T"));
                                bundle.putString("U", jSONObject.getString("U"));
                                bundle.putString("I", jSONObject.getString("I"));
                                bundle.putByteArray("J", GZIP.ZIPToArray(jSONObject.getString("J")));
                                message.setData(bundle);
                                ophr.olPlayHallRoomHandler.handleMessage(message);
                                return;
                            }
                            Bundle bundle2 = new Bundle();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                            bundle2.putString("N", jSONObject2.getString("N"));
                            bundle2.putString("C", jSONObject2.getString("C"));
                            bundle2.putString("T", jSONObject2.getString("T"));
                            bundle2.putString("U", jSONObject2.getString("U"));
                            bundle2.putString("I", jSONObject2.getString("I"));
                            bundle2.putByteArray("J", GZIP.ZIPToArray(jSONObject2.getString("J")));
                            bundle.putBundle(String.valueOf(i2), bundle2);
                            i = i2 + 1;
                        }
                    }
                    break;
                case 3:
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        JPStack.create();
                        OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) JPStack.top();
                        Bundle bundle = new Bundle();
                        bundle.putString("I", jSONObject.getString("I"));
                        bundle.putInt("R", jSONObject.getInt("R"));
                        message.setData(bundle);
                        message.what = 9;
                        assert olPlayHallRoom != null;
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                        return;
                    }
                    break;
                case 4:
                    if (JPStack.top() instanceof OLPlayHallRoom) {
                        JPStack.create();
                        OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) JPStack.top();
                        Bundle bundle = new Bundle();
                        bundle.putInt("R", jSONObject.getInt("R"));
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
                        JPStack.create();
                        OLFamily family = (OLFamily) JPStack.top();
                        Bundle bundle = new Bundle();
                        bundle.putString("I", jSONObject.getString("I"));
                        message.setData(bundle);
                        message.what = 5;
                        assert family != null;
                        family.familyHandler.handleMessage(message);
                        return;
                    }
                    break;
                case 8:
                    if (JPStack.top() instanceof OLFamily) {
                        JPStack.create();
                        OLFamily family = (OLFamily) JPStack.top();
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString("I", jSONObject.getString("I"));
                            bundle.putInt("R", jSONObject.getInt("R"));
                        } catch (Exception ignore) {
                        }
                        message.setData(bundle);
                        message.what = 8;
                        assert family != null;
                        family.familyHandler.handleMessage(message);
                        return;
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
